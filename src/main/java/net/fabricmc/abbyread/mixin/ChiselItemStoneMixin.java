package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.UniformEfficiencyModifier;
import btw.item.items.ChiselItemStone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
}
