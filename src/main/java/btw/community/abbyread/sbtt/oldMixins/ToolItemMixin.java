package btw.community.abbyread.sbtt.oldMixins;

import btw.community.abbyread.EfficiencyHelper;
import btw.item.items.ToolItem;
import net.minecraft.src.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ToolItem.class)
public abstract class ToolItemMixin {
    // ThreadLocal cache of effectiveness between getStrVsBlock and onBlockDestroyed

    @Inject(method = "getStrVsBlock", at = @At("HEAD"), cancellable = true)
    private void abbyread$getStrVsBlock(ItemStack stack, World world, Block block,
                                        int x, int y, int z,
                                        CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(EfficiencyHelper.getStrVsBlock(stack, world, block, x, y, z));
    }

    @Inject(method = "onBlockDestroyed", at = @At("HEAD"), cancellable = true)
    private void abbyread$preventWastedUses(ItemStack stack, World world, int blockID,
                                            int x, int y, int z,
                                            EntityLivingBase entity,
                                            CallbackInfoReturnable<Boolean> cir) {
        if (stack == null) return;
        if (!world.isRemote) {
            if (EfficiencyHelper.getLastEffective()) {
                stack.damageItem(1, entity);
            }
        }

        EfficiencyHelper.setLastEffective(false); // cleanup for next call

        cir.setReturnValue(true);
    }
}
