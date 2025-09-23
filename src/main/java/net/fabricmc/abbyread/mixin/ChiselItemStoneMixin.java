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

    @Inject(method = "getStrVsBlock", at = @At("HEAD"), cancellable = true)
    private void abbyread$getStrVsBlock(ItemStack stack, World world, Block block,
                                        int x, int y, int z,
                                        CallbackInfoReturnable<Float> cir) {
        if (stack == null || block == null) return;

        if (world != null) {
            System.out.println("ChiselItemStone detected.");
            ToolItemAccessor accessor = (ToolItemAccessor) this;
            float effProp = accessor.getEfficiencyOnProperMaterial();
            // Check efficiency between tool and the block it's used on.
            boolean effective = EfficiencyHelper.isToolItemEfficientVsBlock(stack, world, block, x, y, z);
            if (effective) {
                System.out.println("Tool IS effective on block.");
                EfficiencyHelper.setLastEffective(true);
                cir.setReturnValue(effProp);
            } else {
                // Not effective: Shouldn't boost, shouldn't damage item
                System.out.println("Tool not effective on block.");
                float minimum = 1F;
                float potentialOverride = 1F;
                if (BlockBreakingOverrides.isUniversallyEasyBlock(block)) {
                    System.out.println("Block is universally easy.");
                    potentialOverride = BlockBreakingOverrides.baselineEfficiency(block);
                }
                EfficiencyHelper.setLastEffective(false);
                // Prevent boost by picking minimum.
                //   (universally easy blocks already max to potentialOverride)
                cir.setReturnValue(Math.min(potentialOverride, minimum)); // 1F is the default getStrVsBlock
            }
        }
    }
}
