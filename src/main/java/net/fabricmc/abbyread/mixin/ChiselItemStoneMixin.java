package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.BlockBreakingOverrides;
import btw.community.abbyread.EfficiencyHelper;
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

    // Override in the source code calls the original before
    @Inject(method = "getStrVsBlock", at = @At("HEAD"), cancellable = true)
    private void abbyread$getStrVsBlock(ItemStack stack, World world, Block block,
                                        int x, int y, int z,
                                        CallbackInfoReturnable<Float> cir) {
        if (stack == null || block == null) return;

        if (world != null) {
            ToolItemAccessor accessor = (ToolItemAccessor) this;
            float effProp = accessor.getEfficiencyOnProperMaterial();
            float effMod = UniformEfficiencyModifier.VALUE;
            float effBoost = effProp * effMod * 2;
            boolean effective = EfficiencyHelper.isToolItemEfficientVsBlock(stack, world, block, x, y, z);
            if (effective) {
                EfficiencyHelper.setLastEffective(true);
                cir.setReturnValue(effBoost);
            } else {
                float minimum = BlockBreakingOverrides.baselineEfficiency(block);
                EfficiencyHelper.setLastEffective(false);
                cir.setReturnValue(Math.max(effProp, minimum));
            }
        }
    }
}
