package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.BlockBreakingOverrides;
import btw.community.abbyread.EfficiencyHelper;
import btw.item.items.ChiselItemWood;
import btw.item.items.ToolItem;
import net.minecraft.src.Block;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ToolItem.class)
public abstract class ToolItemMixin {
    // ThreadLocal cache of effectiveness between getStrVsBlock and onBlockDestroyed

    @Inject(method = "getStrVsBlock", at = @At("HEAD"), cancellable = true)
    private void abbyread$getStrVsBlock(ItemStack stack, World world, Block block,
                                        int x, int y, int z,
                                        CallbackInfoReturnable<Float> cir) {
        if (stack == null || block == null) return;

        if (stack.getItem() instanceof ChiselItemWood) {
            if (world != null) {
                System.out.println("ChiselItemWood detected.");
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
                    if (BlockBreakingOverrides.isUniversallyEasyBlock(block)){
                        System.out.println("Block is universally easy.");
                        potentialOverride = BlockBreakingOverrides.baselineEfficiency(block);
                    }
                    EfficiencyHelper.setLastEffective(false);
                    // Prevent boost by picking minimum.
                    //   (universally easy blocks already max to potentialOverride)
                    cir.setReturnValue(Math.min(potentialOverride, minimum)); // 1F is the default getStrVsBlock
                }
                return;
            }
        }

        if (world != null) {
            boolean effective = EfficiencyHelper.isToolItemEfficientVsBlock(stack, world, block, x, y, z);
            if (effective) {
                EfficiencyHelper.setLastEffective(true);
                ToolItemAccessor accessor = (ToolItemAccessor) this;
                cir.setReturnValue(accessor.getEfficiencyOnProperMaterial());
            } else {
                // Not effective -> maybe boosted fallback
                EfficiencyHelper.setLastEffective(false);
            }
        }
    }

    @Inject(method = "onBlockDestroyed", at = @At("HEAD"), cancellable = true)
    private void abbyread$preventWastedUses(ItemStack stack, World world, int blockID,
                                            int x, int y, int z,
                                            EntityLivingBase entity,
                                            CallbackInfoReturnable<Boolean> cir) {
        if (stack == null) return;
        if (!world.isRemote) {
            System.out.println("Handling check for item damage.");
            System.out.println("EfficiencyHelper.getLastEffective(): " + EfficiencyHelper.getLastEffective());
            if (EfficiencyHelper.getLastEffective()) {
                System.out.println("Is effective.  Damaging.");
                stack.damageItem(1, entity);
            }
        }

        EfficiencyHelper.setLastEffective(false); // cleanup for next call

        cir.setReturnValue(true);
    }
}
