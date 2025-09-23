package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.BlockBreakingOverrides;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "getStrVsBlock", at = @At("RETURN"), cancellable = true)
    private void abbyread$enforceMinimumStr(World world, Block block, int x, int y, int z, CallbackInfoReturnable<Float> cir) {
        if (block == null) return;
        float current = cir.getReturnValue();
        float minimum = BlockBreakingOverrides.baselineEfficiency(block);
        cir.setReturnValue(Math.max(current, minimum));
    }
}
