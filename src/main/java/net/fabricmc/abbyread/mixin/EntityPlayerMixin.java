package net.fabricmc.abbyread.mixin;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import btw.community.abbyread.BlockBreakingOverrides;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin {

    @Shadow public InventoryPlayer inventory;

    @ModifyVariable(
            method = "getCurrentPlayerStrVsBlock",
            at = @At("RETURN"),
            ordinal = 0
    )
    private float abbyread$getMinimumStrVsBlock(float current, Block block, int meta) {
        if (block == null) return current;

        float minimum = BlockBreakingOverrides.baselineEfficiency(block);
        return Math.max(current, minimum);

    }
/* Check item held class
    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void abbyread$debugHeldItem(CallbackInfo ci) {
        EntityPlayer self = (EntityPlayer) (Object) this;
        ItemStack held = self.getCurrentEquippedItem();
        if (held != null && held.getItem() != null) {
            System.out.println("[DEBUG] Held: "
                    + held.getDisplayName() + " -> "
                    + held.getItem().getClass().getName());
        }
    }
 */
}
