package btw.community.abbyread.sbtt.mixin;

import btw.community.abbyread.categories.ItemUseRegistry;
import btw.community.abbyread.sbtt.Convert;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public class PlayerControllerMPMixin {

    @Unique
    private boolean checking = false;

    @Inject(method = "onPlayerRightClick",
            at = @At("HEAD"),
            cancellable = true)
    public void onPlayerRightClickIntercept(EntityPlayer player, World world, ItemStack stack,
                                            int x, int y, int z, int side, Vec3 hitVec,
                                            CallbackInfoReturnable<Boolean> cir) {
        if (checking || world == null || player == null || stack == null) {
            return;
        }

        checking = true;
        try {
            Block block = Block.blocksList[world.getBlockId(x, y, z)];
            if (block == null) return;

            // Transfer state from Convert globals (see if block converted via that helper)
            boolean conversionByTool = Convert.justConverted;
            Convert.justConverted = false;
            int itemDamageAmount = Convert.itemDamageAmount;
            Convert.itemDamageAmount = 1;

            // Checks if this item + block combo counts as "useful"
            boolean specialCase = ItemUseRegistry.usefulRightClickCombo(stack, block, world.getBlockMetadata(x, y, z));

            boolean shouldDamage = conversionByTool || specialCase;

            if (shouldDamage) {
                // Confusing, but item-side logic for handling consequences of block interaction
                boolean didAffectTool = stack.getItem().onBlockDestroyed(stack, world, block.blockID, x, y, z, player);
                if (didAffectTool) {
                    player.addStat(StatList.objectUseStats[stack.itemID], itemDamageAmount);
                }
            }

            // Cancel original so we control damage fully
            cir.setReturnValue(true);

        } finally {
            checking = false;
        }
    }
}
