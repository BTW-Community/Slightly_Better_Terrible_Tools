package net.fabricmc.abbyread.mixin;

import btw.block.blocks.*;
import btw.community.abbyread.UniformEfficiencyModifier;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryPlayer.class)
public class InventoryPlayerMixin {
    @Unique float effMod = UniformEfficiencyModifier.VALUE;

    // Boost empty-handed
    @Inject(method = "getStrVsBlock", at = @At("RETURN"), cancellable = true)
    private void abbyread$effModBoost(World world, Block block, int x, int y, int z, CallbackInfoReturnable<Float> cir){
        @SuppressWarnings("ConstantConditions")
        InventoryPlayer inv = (InventoryPlayer) (Object) this;
        if (inv.mainInventory[inv.currentItem] == null) {
            float efficiency = cir.getReturnValue();
            if (
                    block instanceof BlockSand
            ) {
                efficiency *= effMod;
            } else if (
                    block instanceof LooseDirtBlock ||
                    block instanceof LooseSparseGrassBlock ||
                    block instanceof LooseSparseGrassSlabBlock ||
                    block instanceof LooseDirtSlabBlock ||
                    block instanceof ChewedLogBlock ||
                    block instanceof BlockLog
            ) {
                efficiency *= effMod * 1.5F;
            }
            cir.setReturnValue(efficiency);
        }
    }
}
