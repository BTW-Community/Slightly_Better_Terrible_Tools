package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.LooseningHelper;
import btw.item.BTWItems;
import btw.item.items.ToolItem;

import net.minecraft.src.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(btw.item.items.ToolItem.class)
public abstract class ToolItemMixin {

    @Inject(method = "onBlockDestroyed", at = @At("HEAD"), cancellable = true)
    private void handleLoosenBlocks(ItemStack stack, World world, int blockID, int x, int y, int z,
                                    EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir) {

        // Track current tool globally
        btw.community.abbyread.ToolState.setCurrentTool(stack);

        // Attempt to loosen the block
        if (LooseningHelper.tryLoosenBlock(stack, world, x, y, z)) {
            cir.setReturnValue(true); // skip vanilla destruction/drops
        }
    }

    @Inject(method = "onBlockDestroyed", at = @At("HEAD"), require = 1)
    private void abby$tryLoosen(ItemStack stack, World world, int blockID, int x, int y, int z,
                                  EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() != null &&
            stack.getItem().itemID == BTWItems.pointyStick.itemID) {
            LooseningHelper.tryLoosenBlock(stack, world, x, y, z);

        }
    }

    @Inject(method = "onBlockDestroyed", at = @At("HEAD"), cancellable = true)
    private void handleDurability(ItemStack stack, World world, int blockID, int x, int y, int z,
                                  EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir) {
        Block block = Block.blocksList[blockID];
        ToolItem self = (ToolItem) (Object) this;

        boolean effective = self.isEfficientVsBlock(stack, world, block, x, y, z);
        float speed = self.getStrVsBlock(stack, world, block, x, y, z);

        if (effective || speed > 1.0F) {
            stack.damageItem(1, entity);
        }
        cir.setReturnValue(true);
    }
}
