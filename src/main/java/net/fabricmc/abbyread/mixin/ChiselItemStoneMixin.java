package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.BlockBreakingOverrides;
import btw.community.abbyread.EfficiencyHelper;
import btw.community.abbyread.UniformEfficiencyModifier;
import btw.item.items.ChiselItemStone;
import btw.item.items.ChiselItemWood;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChiselItemStone.class)
public class ChiselItemStoneMixin {

    // Follow-up BTW's efficiencyOnProperMaterial /= 2 with a *= effMod
    @Inject(
            method = "applyStandardEfficiencyModifiers",
            at = @At("TAIL"),
            remap = false
    )
    private void abbyread$effModApplication(CallbackInfo ci) {
        ToolItemAccessor accessor = (ToolItemAccessor) this;
        float original = accessor.getEfficiencyOnProperMaterial();
        final float effMod = UniformEfficiencyModifier.VALUE;
        accessor.setEfficiencyOnProperMaterial(original * effMod);
    }

    // --- Efficiency override for pointy stick ---
    @Inject(method = "getStrVsBlock", at = @At("HEAD"), cancellable = true)
    private void abbyread$getStrVsBlock(ItemStack stack, World world, Block block, int x, int y, int z,
                                        CallbackInfoReturnable<Float> cir) {
        if (stack == null) return;

        if (stack.getItem() instanceof ChiselItemWood) {
            if (world != null && EfficiencyHelper.isToolItemEfficientVsBlock(stack, world, block, x, y, z)) {
                float efficiency = ((ToolItemAccessor) this).getEfficiencyOnProperMaterial();
                cir.setReturnValue(efficiency);
            } else {
                cir.setReturnValue(1F); // When ToolItem determines it's not efficient, it calls to Item for this value.
            }
        }
    }
}
