package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.EfficiencyHelper;
import btw.item.items.ChiselItem;
import net.minecraft.src.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChiselItem.class)
public class ChiselItemMixin {
    @Inject(method = "isToolTypeEfficientVsBlockType", at = @At("HEAD"), remap = false)
    private void abbyread$additionalEfficiencyChecks(Block block, CallbackInfoReturnable<Boolean> cir) {
        if (block.arechiselseffectiveon()) EfficiencyHelper.setLastEffective(true);
    }
}
