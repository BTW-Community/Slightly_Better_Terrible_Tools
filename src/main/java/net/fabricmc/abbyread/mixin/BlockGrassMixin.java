package net.fabricmc.abbyread.mixin;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import btw.community.abbyread.categories.BlockTag;
import btw.community.abbyread.categories.BlockTags;
import btw.community.abbyread.categories.ItemTag;
import btw.community.abbyread.categories.ItemTags;
import btw.community.abbyread.sbtt.Convert;
import btw.item.items.ChiselItemStone;
import btw.item.items.ChiselItemWood;
import btw.item.items.ShovelItemStone;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockGrass.class)
public abstract class BlockGrassMixin {

    @Shadow public abstract boolean isSparse(int metadata);

    @Inject(
        method = "onNeighborDirtDugWithImproperTool",
        at = @At("HEAD"),
        cancellable = true
    )
    private void abby$reimplementHardcoreGrassDrop(World world, int x, int y, int z, int toFacing, CallbackInfo ci) {
        if (toFacing == 0) {
            world.setBlockWithNotify(x, y, z, BTWBlocks.looseDirt.blockID);
        }
        ci.cancel();
    }

    @Inject(method = "onBlockDestroyedWithImproperTool", at = @At("HEAD"), cancellable = true)
    private void abby$overrideDisturbanceFromStoneShovel(World world, EntityPlayer player, int x, int y, int z, int metadata, CallbackInfo ci) {
        ItemStack stack = player.inventory.getCurrentItem();
        if (stack == null) return;

        if (stack.getItem() instanceof ShovelItemStone) {

            // Unrolled: super.onBlockDestroyedWithImproperTool(world, player, x, y, z, metadata);
            world.playAuxSFX( BTWEffectManager.BLOCK_DESTROYED_WITH_IMPROPER_TOOL_EFFECT_ID, x, y, z, world.getBlockId(x, y, z) + ( metadata << 12 ) );
            ((Block)(Object)this).dropComponentItemsOnBadBreak(world, x, y, z, metadata, 1F);

            ci.cancel();
            // Skips the call to onDirtDugWithImproperTool, which would loosen neighboring blocks.
        }
    }

    @Inject(
        method = "canConvertBlock", at = @At("HEAD"), cancellable = true
    )
    private void abbyread$canConvertBlock(ItemStack stack, World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir){
        if (stack == null) return;

        Block block = (Block)(Object) this;
        int meta = world.getBlockMetadata(x, y, z);

        // Loosen with Pointy Stick (not fully-grown grass though)
        if (ItemTags.isAll(stack, ItemTag.WOOD, ItemTag.CHISEL)) {
                if (BlockTags.is(block, meta, BlockTag.SPARSE)) cir.setReturnValue(true);
        }

        // Sparsen with Sharp Stone
        if (ItemTags.isAll(stack, ItemTag.STONE, ItemTag.CHISEL)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "convertBlock", at = @At("HEAD"), cancellable = true)
    private void abbyread$convertBlock(ItemStack stack, World world, int x, int y, int z, int fromSide, CallbackInfoReturnable<Boolean> cir){
        if (stack == null) return;
        Block block = (BlockGrass)(Object) this;
        int meta = world.getBlockMetadata(x, y, z);
        boolean swapped = false;

        // If sharp stone is used on a grass block, sparsen in stages
        if (!world.isRemote && ItemTags.isAll(stack, ItemTag.STONE, ItemTag.CHISEL)) {
            swapped = Convert.sparsen(stack, block, meta, world, x, y, z, fromSide);
        }
        if (swapped) cir.setReturnValue(true);
    }

}