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
            at = @At("RETURN"))
    public void onPlayerRightClickDamage(EntityPlayer player, World world, ItemStack stack,
                                         int x, int y, int z, int side, Vec3 hitVec,
                                         CallbackInfoReturnable<Boolean> cir) {
        if (checking || world == null || player == null || stack == null) {
            return;
        }

        checking = true;
        try {
            Block block = Block.blocksList[world.getBlockId(x, y, z)];
            if (block == null) return;

            // Transfer state from Convert globals
            boolean conversionByTool = Convert.justConverted;
            Convert.justConverted = false;
            int itemDamageAmount = Convert.itemDamageAmount;
            Convert.itemDamageAmount = 1;

            // Checks if this item + block combo counts as "useful"
            boolean specialCase = ItemUseRegistry.usefulRightClickCombo(stack, block, world.getBlockMetadata(x, y, z));

            // Only damage if conversion or special case triggered
            boolean shouldDamage = conversionByTool || specialCase;

            if (shouldDamage) {
                boolean didAffectTool = stack.getItem().onBlockDestroyed(stack, world, block.blockID, x, y, z, player);
                if (didAffectTool) {
                    player.addStat(StatList.objectUseStats[stack.itemID], itemDamageAmount);
                }
            }

        } finally {
            checking = false;
        }
    }
}
