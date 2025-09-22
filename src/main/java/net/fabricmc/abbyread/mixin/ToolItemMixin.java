package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.EfficiencyHelper;
import btw.item.BTWItems;
import btw.item.items.ChiselItemStone;
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

    // --- Efficiency override for pointy stick / sharp stone ---
    @Inject(method = "getStrVsBlock", at = @At("HEAD"), cancellable = true, remap = false)
    private void abbyread$getStrVsBlock(ItemStack stack, World world, Block block, int x, int y, int z,
                                        CallbackInfoReturnable<Float> cir) {
        if (stack == null) return;

        int id = stack.getItem().itemID;
        if (id == BTWItems.pointyStick.itemID || id == BTWItems.sharpStone.itemID) {
            if (world != null && EfficiencyHelper.isToolItemEfficientVsBlock(stack, world, block, x, y, z)) {
                float eff = ((ToolItemAccessor) this).getEfficiencyOnProperMaterial();
                cir.setReturnValue(eff);
            } else {
                cir.setReturnValue(1.0F);
            }
        }
    }

    // --- Durability only when effective ---
    @Inject(method = "onBlockDestroyed", at = @At("HEAD"), cancellable = true, remap = false)
    private void abbyread$preventWastedUses(ItemStack stack, World world, int blockID, int x, int y, int z,
                                            EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null || world == null) return;

        Block block = Block.blocksList[blockID];
        boolean effective;

        if (stack.getItem() instanceof ChiselItemWood || stack.getItem() instanceof ChiselItemStone) {
            effective = EfficiencyHelper.isToolItemEfficientVsBlock(stack, world, block, x, y, z);
        } else {
            effective = stack.getItem().isEfficientVsBlock(stack, world, block, x, y, z);
        }

        if (!world.isRemote && effective) {
            stack.damageItem(1, entity);
        }

        // Always cancel vanilla handling — we’ve replaced it fully.
        cir.setReturnValue(true);
    }
}
