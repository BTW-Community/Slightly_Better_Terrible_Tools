package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.EfficiencyHelper;
import btw.item.items.ShovelItem;
import net.minecraft.src.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShovelItem.class)
public class ShovelItemMixin {

    // Ensure damage is calculated correctly
    @Inject(method = "isToolTypeEfficientVsBlockType", at = @At("RETURN"), remap = false)
    private void abbyread$tellHelper(Block block, CallbackInfoReturnable<Boolean> cir) {
        EfficiencyHelper.setLastEffective(cir.getReturnValue());
    }
}