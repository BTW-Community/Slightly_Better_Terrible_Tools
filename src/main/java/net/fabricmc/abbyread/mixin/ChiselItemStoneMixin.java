package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.BlockBreakingOverrides;
import btw.community.abbyread.UniformEfficiencyModifier;
import btw.item.items.ChiselItemStone;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChiselItemStone.class)
public class ChiselItemStoneMixin {

    // Follow-up BTW's efficiencyOnProperMaterial /= 2 with a *= effMod
    @Inject(
            method = "applyStandardEfficiencyModifiers",
            at = @At("TAIL"),
            remap = false
    )
    private void abbyread$effModApplication(CallbackInfo ci) {
        ToolItemAccessor accessor = (ToolItemAccessor) this;
        float original = accessor.getEfficiencyOnProperMaterial();
        final float effMod = UniformEfficiencyModifier.VALUE;
        accessor.setEfficiencyOnProperMaterial(original * effMod);
    }

    /**
     * Adjust getStrVsBlock to enforce boosted efficiency on blocks defined
     * in BlockBreakingOverrides, ignoring ChiselItemStone's /=2 divisor.
     */
    @Inject(
            method = "getStrVsBlock",
            at = @At("RETURN"),
            cancellable = true,
            remap = false
    )
    private void abbyread$boostInefficientBlock(ItemStack stack, World world, Block block, int i, int j, int k, CallbackInfoReturnable<Float> cir) {
        if (block == null) return;

        float originalStrength = cir.getReturnValueF();
        float boostedStrength = BlockBreakingOverrides.getBoostedStrength(block);

        // Only override if the block is meant to be boosted and original is less than boosted
        if (boostedStrength > 1.0F && originalStrength < boostedStrength) {
            cir.setReturnValue(boostedStrength);
        }
    }
}
