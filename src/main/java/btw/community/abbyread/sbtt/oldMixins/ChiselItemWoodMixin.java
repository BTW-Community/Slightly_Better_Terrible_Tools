package btw.community.abbyread.sbtt.oldMixins;

import btw.community.abbyread.EfficiencyHelper;
import btw.item.items.ChiselItemWood;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChiselItemWood.class)
public abstract class ChiselItemWoodMixin {

    // Increase pointy stick uses to 4
    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lbtw/item/items/ChiselItem;<init>(ILnet/minecraft/src/EnumToolMaterial;I)V"
            ),
            index = 2
    )
    private static int abbyread$increaseUses(int original) {
        return 4;
    }

    // Follow-up BTW's efficiencyOnProperMaterial /= 4 with a *= effMod
    @Inject(
            method = "applyStandardEfficiencyModifiers",
            at = @At("TAIL"), remap = false
    )
    private void abbyread$effModApplication(CallbackInfo ci) {
        ToolItemAccessor accessor = (ToolItemAccessor) this;
        float original = accessor.getEfficiencyOnProperMaterial();
        final float effMod = EfficiencyHelper.effMod;
        accessor.setEfficiencyOnProperMaterial(original * effMod);
    }
}
