package btw.community.abbyread.sbtt.mixin;

import btw.community.abbyread.categories.ItemUseRegistry;
import btw.community.abbyread.sbtt.Convert;
import btw.community.abbyread.sbtt.ItemDamage;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("FieldCanBeLocal")
@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Unique private boolean DEBUG = false;
    @Unique private boolean processingDamage = false; // recursion guard / item damage flag

    @Inject(method = "onBlockDestroyed", at = @At("HEAD"), cancellable = true)
    public void onBlockDestroyedIntercept(World world, int blockId, int x, int y, int z, EntityPlayer player, CallbackInfo ci) {
        // --- Prevent recursive entry ---
        if (processingDamage) return;
        processingDamage = true;
        try {
            if (world == null || player == null) return;
            @SuppressWarnings("DataFlowIssue")
            ItemStack self = (ItemStack)(Object)this;

            Block block = Block.blocksList[blockId];
            if (block == null) return;

            // --- Compare block destruction speeds ---
            float toolSpeed = block.getPlayerRelativeBlockHardness(player, world, x, y, z);

            // Bare-hand baseline
            ItemStack held = player.inventory.mainInventory[player.inventory.currentItem];
            player.inventory.mainInventory[player.inventory.currentItem] = null;
            float bareHandSpeed = block.getPlayerRelativeBlockHardness(player, world, x, y, z);
            player.inventory.mainInventory[player.inventory.currentItem] = held;
            float multiplier = (bareHandSpeed > 0.0f) ? (toolSpeed / bareHandSpeed) : toolSpeed;

            // Transfer state from Convert globals
            boolean conversionByTool = Convert.justConverted;
            Convert.justConverted = false;
            int itemDamageAmount = ItemDamage.amount;
            ItemDamage.amount = 1; // default

            boolean specialCase = ItemUseRegistry.usefulLeftClickCombo(self, block, world.getBlockMetadata(x, y, z));
            boolean betterThanNothing = multiplier > 1.0f || conversionByTool || specialCase;

            if (betterThanNothing) {
                boolean didAffectTool = Item.itemsList[self.itemID].onBlockDestroyed(self, world, blockId, x, y, z, player);
                if (didAffectTool) {
                    player.addStat(StatList.objectUseStats[self.itemID], 1);
                }

                if (DEBUG) {
                    System.out.printf(
                            "[Tool Check] %s vs %s at (%d,%d,%d): tool=%.3f, bare=%.3f, x%.2f%n",
                            self.getDisplayName(),
                            block.getLocalizedName(),
                            x, y, z,
                            toolSpeed, bareHandSpeed, multiplier
                    );
                }
            }

            // Cancel original to prevent default behavior of damaging regardless
            ci.cancel();
        } finally {
            processingDamage = false; // release guard
        }
    }

    /*    // No way to make use of this for damage calc due to lack of access to block info
    @Inject(method = "useItemRightClick", at = @At("RETURN"), cancellable = true)
    private void damageIfUsed(World world, EntityPlayer player, CallbackInfoReturnable<ItemStack> cir) {
        if (world.isRemote) return;
        System.out.println("Right-clicked.");
    }
    */ // See PlayerControllerMPMixin for damage calc inject targeting right-click use
}
