package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.UniformEfficiencyModifier;
import btw.item.items.ChiselItemWood;
import btw.item.items.ToolItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChiselItemWood.class)
public abstract class ChiselItemWoodMixin {
    @Unique
    private final float effMod = UniformEfficiencyModifier.UNIFORM_EFFICIENCY_MODIFIER;

    /*
	Change the third arg to be passed to the ChiselItem constructor
	*/
    @ModifyArg(
            method = "<init>", // target constructor
            at = @At(
                    value = "INVOKE",
                    target = "Lbtw/item/items/ChiselItem;<init>(ILnet/minecraft/src/EnumToolMaterial;I)V"
            ),
            index = 2 // 0=itemID, 1=material, 2=uses
    )
    private static int abbyread$increaseUses(int original) {
        return 4;
    }

    @Inject(
            method = "applyStandardEfficiencyModifiers",
            at = @At("RETURN"),
            remap = false
    )
    private void tweakEfficiency(CallbackInfo ci)
    {
        ((ToolItem)(Object)this).addCustomEfficiencyMultiplier(effMod);
    }
}