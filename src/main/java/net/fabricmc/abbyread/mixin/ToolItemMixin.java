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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ToolItem.class)
public abstract class ToolItemMixin {
    // ThreadLocal cache of effectiveness between getStrVsBlock and onBlockDestroyed
    @Unique
    private static final ThreadLocal<Boolean> LAST_EFFECTIVE = ThreadLocal.withInitial(() -> false);

    @Inject(method = "getStrVsBlock", at = @At("HEAD"), cancellable = true)
    private void abbyread$getStrVsBlock(ItemStack stack, World world, Block block,
                                        int x, int y, int z,
                                        CallbackInfoReturnable<Float> cir) {
        if (stack == null || block == null) return;

        boolean effective = world != null &&
                EfficiencyHelper.isToolItemEfficientVsBlock(stack, world, block, x, y, z);

        // cache effectiveness for durability handling
        LAST_EFFECTIVE.set(effective);

        if (effective) {
            // full tool efficiency
            float efficiency = ((ToolItemAccessor) this).getEfficiencyOnProperMaterial();
            cir.setReturnValue(efficiency);
            return;
        }

        // Not effective -> maybe boosted fallback
        float boostedStrength = BlockBreakingOverrides.baselineEfficiency(block);
        if (boostedStrength > 1.0F) {
            cir.setReturnValue(boostedStrength);
            return;
        }

        // Default ineffective value
        cir.setReturnValue(1.0F);
    }

    @Inject(method = "onBlockDestroyed", at = @At("HEAD"), cancellable = true)
    private void abbyread$preventWastedUses(ItemStack stack, World world, int blockID,
                                            int x, int y, int z,
                                            EntityLivingBase entity,
                                            CallbackInfoReturnable<Boolean> cir) {
        if (stack == null) return;
        boolean effective = false;
        if (!world.isRemote) {
            System.out.println("Handling check for item damage.");
            System.out.println("LAST_EFFECTIVE.get(): " + LAST_EFFECTIVE.get());
            if (LAST_EFFECTIVE.get()) {
                System.out.println("Is effective.  Damaging.");
                stack.damageItem(1, entity);
            }
        }

        LAST_EFFECTIVE.remove(); // cleanup for next call


        cir.setReturnValue(true);
    }
}
