package net.fabricmc.abbyread.mixin;

import btw.item.items.ChiselItemWood;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

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
}
