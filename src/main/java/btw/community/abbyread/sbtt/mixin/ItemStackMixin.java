package btw.community.abbyread.sbtt.mixin;

import btw.community.abbyread.categories.ItemUseRegistry;
import btw.community.abbyread.sbtt.Convert;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Unique boolean DEBUG = false;

    @Inject(method = "onBlockDestroyed", at = @At("HEAD"), cancellable = true)
    public void onBlockDestroyedIntercept(World world, int blockId, int x, int y, int z, EntityPlayer player, CallbackInfo ci) {
        if (world == null || player == null) return;
        ItemStack self = (ItemStack)(Object)this;

        Block block = Block.blocksList[blockId];
        if (block == null) return;

        // --- Compare block destruction speeds ---
        // Tool speed
        float toolSpeed = block.getPlayerRelativeBlockHardness(player, world, x, y, z);

        // Bare hand speed (simulate no tool by nulling out the held stack temporarily)
        ItemStack held = player.inventory.mainInventory[player.inventory.currentItem];
        player.inventory.mainInventory[player.inventory.currentItem] = null;
        float bareHandSpeed = block.getPlayerRelativeBlockHardness(player, world, x, y, z);
        player.inventory.mainInventory[player.inventory.currentItem] = held;
        float multiplier = (bareHandSpeed > 0.0f) ? (toolSpeed / bareHandSpeed) : toolSpeed;

        // Transfer state from Convert globals to this locals
        boolean conversionByTool = Convert.justConverted;
        Convert.justConverted = false;
        int itemDamageAmount = Convert.itemDamageAmount;
        Convert.itemDamageAmount = 1; // default amount

        // Account for special instances of tools helping without being faster
        boolean specialCase = ItemUseRegistry.uniquelyUsefulCombo(self, block, world.getBlockMetadata(x, y, z));

        boolean betterThanNothing = multiplier > 1.0f || conversionByTool || specialCase;

        // --- Only damage the tool if it was faster than bare hands ---
        if (betterThanNothing) {
            boolean didAffectTool = Item.itemsList[self.itemID].onBlockDestroyed(self, world, blockId, x, y, z, player);
            if (didAffectTool) {
                player.addStat(StatList.objectUseStats[self.itemID], itemDamageAmount);
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

        // Cancel original to prevent double-calling
        ci.cancel();
    }
}