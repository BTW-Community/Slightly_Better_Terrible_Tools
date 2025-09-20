package net.fabricmc.abbyread.mixin;

import btw.block.BTWBlocks;
import btw.block.blocks.DirtSlabBlock;
import btw.community.abbyread.ToolState;
import btw.item.BTWItems;
import btw.item.items.ToolItem;

import net.minecraft.src.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(btw.item.items.ToolItem.class)
public abstract class ToolItemMixin {

    @Unique
    private int getLoosenedBlockId(World world, int x, int y, int z, int fromBlockID) {
        // Special case: packed earth slabs should never loosen
        if (fromBlockID == BTWBlocks.dirtSlab.blockID &&
                BTWBlocks.dirtSlab.getSubtype(world.getBlockMetadata(x, y, z)) ==
                        DirtSlabBlock.SUBTYPE_PACKED_EARTH) {
            return -1;
        }

        if (fromBlockID == Block.dirt.blockID) {
            return BTWBlocks.looseDirt.blockID;
        } else if (fromBlockID == BTWBlocks.dirtSlab.blockID) {
            return BTWBlocks.looseDirtSlab.blockID;
        } else if (fromBlockID == Block.grass.blockID) {
            return BTWBlocks.looseSparseGrass.blockID;
        } else if (fromBlockID == BTWBlocks.grassSlab.blockID) {
            return BTWBlocks.looseSparseGrassSlab.blockID;
        }

        return -1; // no match
    }

    @Unique
    private boolean loosenBlock(ItemStack stack, World world, int fromBlockID, int x, int y, int z, EntityLivingBase entity) {
        int toBlockID = getLoosenedBlockId(world, x, y, z, fromBlockID);
        if (toBlockID != -1) {
            world.setBlockWithNotify(x, y, z, toBlockID); // ensure durability loss when used
            return true;
        }
        return false;
    }

    @Inject(method = "onBlockDestroyed", at = @At("HEAD"), cancellable = true)
    private void onlyDamageWhenEffective(ItemStack stack, World world, int blockID, int x, int y, int z, EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir) {
        Block block = Block.blocksList[blockID];
        ToolItem self = (ToolItem) (Object) this;

        // Check effectiveness
        boolean effective = self.isEfficientVsBlock(stack, world, block, x, y, z);
        float speed = self.getStrVsBlock(stack, world, block, x, y, z);

        // Only apply durability if effective OR has speed > bare hands
        if (effective || speed > 1.0F) {
            stack.damageItem(1, entity);
        }
        cir.setReturnValue(true);
    }
    @Inject(method = "onBlockDestroyed", at = @At("HEAD"))
    private void captureCurrentTool(ItemStack stack, World world, int blockId, int x, int y, int z, EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir) {
        ToolState.setCurrentTool(stack);
    }

    @Inject(method = "onBlockDestroyed", at = @At("HEAD"), cancellable = true)
    private void handleLoosenBlocks(ItemStack stack, World world, int blockID, int x, int y, int z, EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem().itemID == BTWItems.pointyStick.itemID) {
            if (loosenBlock(stack, world, blockID, x, y, z, entity)) {
                cir.setReturnValue(true); // tool acted, block was loosened
            }
        }
    }
}
