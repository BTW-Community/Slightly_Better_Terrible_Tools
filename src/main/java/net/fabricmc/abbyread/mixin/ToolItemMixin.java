package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.LooseningHelper;
import btw.community.abbyread.ToolState;
import btw.item.BTWItems;
import btw.item.items.ChiselItemWood;
import btw.item.items.ToolItem;

import net.minecraft.src.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ToolItem.class)
public abstract class ToolItemMixin {

    @Inject(method = "onBlockDestroyed", at = @At("HEAD"), cancellable = true)
    private void handleBlockDestruction(ItemStack stack, World world, int blockID, int x, int y, int z,
                                        EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir) {

        // Track current tool globally
        ToolState.setCurrentTool(stack);

        // Determine if this tool can trigger loosening
        boolean isPointyStick = stack.getItem() == BTWItems.pointyStick;
        boolean isWoodChisel = stack.getItem() instanceof ChiselItemWood;

        if (isPointyStick || isWoodChisel) {
            boolean loosened = LooseningHelper.tryLoosenBlock(stack, world, x, y, z, blockID);
            if (loosened) {
                cir.setReturnValue(true); // skip vanilla destruction/drops
                return;
            }
        }

        // Normal durability handling
        Block block = Block.blocksList[blockID];
        ToolItem self = (ToolItem) (Object) this;
        boolean effective = self.isEfficientVsBlock(stack, world, block, x, y, z);
        float speed = self.getStrVsBlock(stack, world, block, x, y, z);

        if (effective || speed > 1.0F) {
            stack.damageItem(1, entity);
        }

        cir.setReturnValue(true); // always return true to cancel vanilla handling
    }
}
