package btw.community.abbyread.sbtt.mixin.speed;

import btw.community.abbyread.sbtt.Globals;
import btw.community.abbyread.sbtt.mixin.access.ToolItemAccessor;
import btw.item.items.ShovelItemStone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShovelItemStone.class)
public class ShovelItemStoneMixin {

    @Inject(method = "applyStandardEfficiencyModifiers", at = @At("RETURN"),remap = false)
    private void abbyread$modifyEfficiencyOnProperMaterial(CallbackInfo ci) {
        // Boost speed of use on proper material to offset the nerf a bit
        float normal = ((ToolItemAccessor)this).abbyread$getEfficiencyOnProperMaterial();
        ((ToolItemAccessor)this).abbyread$setEfficiencyOnProperMaterial(normal * Globals.modifier);
    }

}
