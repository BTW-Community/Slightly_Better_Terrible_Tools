package net.fabricmc.abbyread.mixin;

import btw.block.blocks.*;
import btw.community.abbyread.UniformEfficiencyModifier;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Unique float effMod = UniformEfficiencyModifier.VALUE;
    @Inject(method = "getStrVsBlock", at = @At("RETURN"), cancellable = true)
    private void abbyread$universalModifier(ItemStack stack, World world, Block block, int x, int y, int z, CallbackInfoReturnable<Float> cir) {
        float efficiency = cir.getReturnValue();
        if (
                block instanceof BlockSand
        ) {
            efficiency *= effMod;
            cir.setReturnValue(efficiency);
        } else if (
                block instanceof LooseDirtBlock ||
                block instanceof LooseSparseGrassBlock ||
                block instanceof LooseSparseGrassSlabBlock ||
                block instanceof LooseDirtSlabBlock ||
                block instanceof ChewedLogBlock ||
                block instanceof BlockLog
        ) {
            efficiency *= effMod * 1.5F;
            cir.setReturnValue(efficiency);
        }
    }
}