package btw.community.abbyread.sbtt.oldMixins;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import btw.community.abbyread.BlockBreakingOverrides;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin {

    @Inject(
            method = "getCurrentPlayerStrVsBlock",
            at = @At("RETURN"),
            cancellable = true)
    private void abbyread$enforceMinimumStr(Block block, int i, int j, int k, CallbackInfoReturnable<Float> cir) {
        if (block == null) return;
        float current = cir.getReturnValue();
        float minimum = BlockBreakingOverrides.baselineEfficiency(block);
        cir.setReturnValue(Math.max(current, minimum));
    }
/* Check item held class
    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void abbyread$debugHeldItem(CallbackInfo ci) {
        EntityPlayer self = (EntityPlayer) (Object) this;
        ItemStack held = self.getCurrentEquippedItem();
        if (held != null && held.getItem() != null) {
                    + held.getDisplayName() + " -> "
                    + held.getItem().getClass().getName());
        }
    }
 */
}
