package net.fabricmc.abbyread.mixin;

import btw.block.BTWBlocks;
import btw.block.blocks.DirtSlabBlock;
import btw.crafting.util.FurnaceBurnTime;
import btw.item.BTWItems;
import btw.item.items.ToolItem;

import net.minecraft.src.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;


@Mixin(btw.item.items.ToolItem.class)
public abstract class ToolItemMixin {

    @Unique
    private static Map<Integer, Integer> loosenBlockMap;

    @Unique
    private static Map<Integer, Integer> getLoosenBlockMap() {
        if (loosenBlockMap == null) {
            loosenBlockMap = new HashMap<>();
            loosenBlockMap.put(Block.dirt.blockID, BTWBlocks.looseDirt.blockID);
            loosenBlockMap.put(BTWBlocks.dirtSlab.blockID, BTWBlocks.looseDirtSlab.blockID);
            loosenBlockMap.put(Block.grass.blockID, BTWBlocks.looseSparseGrass.blockID);
            loosenBlockMap.put(BTWBlocks.grassSlab.blockID, BTWBlocks.looseSparseGrassSlab.blockID);
        }
        return loosenBlockMap;
    }

    @Unique
    private boolean loosenBlock(ItemStack stack, World world, int fromBlockID, int x, int y, int z, EntityLivingBase entity) {
        Integer toBlockID = getLoosenBlockMap().get(fromBlockID);
        if (fromBlockID == BTWBlocks.dirtSlab.blockID &&
                BTWBlocks.dirtSlab.getSubtype(world.getBlockMetadata(x, y, z)) ==
                DirtSlabBlock.SUBTYPE_PACKED_EARTH
        ) return false;
        if (toBlockID != null) {
            world.setBlockWithNotify(x, y, z, toBlockID);
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
    }

    @Inject(method = "onBlockDestroyed", at = @At("HEAD"), cancellable = true)
    private void handleLoosenBlocks(ItemStack stack, World world, int blockID, int x, int y, int z, EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem().itemID == BTWItems.pointyStick.itemID
            && getLoosenBlockMap().containsKey(blockID)) {
            cir.setReturnValue(loosenBlock(stack, world, blockID, x, y, z, entity));
        }
    }

    @Inject(
            method = "getStrVsBlock",
            at = @At("RETURN"),
            cancellable = true
    )
    private void abbyread$modifyWoodBurnTime(ItemStack stack, World world, Block block, int i, int j, int k, CallbackInfoReturnable<Float> cir) {

    }

    /**
     * Add extra behavior *after* ToolItem’s constructor finishes.
     * Only applies to wood tools.
     */
    @Shadow public EnumToolMaterial toolMaterial;

    @Shadow public abstract boolean hitEntity(ItemStack stack, EntityLivingBase defendingEntity, EntityLivingBase attackingEntity);

}