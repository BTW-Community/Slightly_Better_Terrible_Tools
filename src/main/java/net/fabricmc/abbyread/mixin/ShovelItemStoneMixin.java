package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.sbtt.Efficiency;
import btw.item.items.ShovelItemStone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShovelItemStone.class)
public class ShovelItemStoneMixin {

    // Get 50% more uses to encourage digging anything up besides clay
    // Maybe later triple uses to give three damage tiers (clay: 3, dirt: 2, snow: 1)
    @Inject(method = "<init>", at = @At("RETURN"))
    private void abbyread$modifyConstructor(int iItemID, CallbackInfo ci) {
        int uses = ((ItemAccessor)this).abbyread$getMaxDamage();
        // Get 50% more uses to encourage digging anything up besides clay
        // Maybe later triple uses to give three damage tiers (clay: 3, dirt: 2, snow: 1)
        uses = Math.round(uses * 1.5F);
        ((ItemAccessor)this).abbyread$setMaxDamage(uses);
    }

    @Inject(method = "applyStandardEfficiencyModifiers", at = @At("RETURN"),remap = false)
    private void abbyread$modifyEfficiencyOnProperMaterial(CallbackInfo ci) {
        // Boost speed of use on proper material to offset the nerf a bit
        float normal = ((ToolItemAccessor)this).abbyread$getEfficiencyOnProperMaterial();
        ((ToolItemAccessor)this).abbyread$setEfficiencyOnProperMaterial(normal * Efficiency.modifier);
    }
}
