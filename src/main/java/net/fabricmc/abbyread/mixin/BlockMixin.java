package net.fabricmc.abbyread.mixin;

import btw.block.BTWBlocks;
import btw.block.blocks.DirtSlabBlock;
import btw.block.blocks.GrassSlabBlock;
import btw.client.fx.BTWEffectManager;
import btw.community.abbyread.categories.BlockTags;
import btw.community.abbyread.categories.BlockTag;
import btw.community.abbyread.categories.ItemTag;
import btw.community.abbyread.categories.ItemTags;
import btw.community.abbyread.sbtt.Efficiency;
import btw.community.abbyread.sbtt.Convert;
import btw.item.items.ChiselItemWood;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(Block.class)
public class BlockMixin {
    @Shadow @Final public int blockID;

    @Inject(
            method = "canConvertBlock", at = @At("HEAD"), cancellable = true
    )
    private void abbyread$canConvertBlock(ItemStack stack, World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir){
        if (stack == null) {
            cir.setReturnValue(false);
            return;
        }

        Block block = (Block)(Object) this;
        int meta = world.getBlockMetadata(x, y, z);

        // Sparsen with Sharp Stone
        if (ItemTags.isAll(stack, ItemTag.STONE, ItemTag.CHISEL)  &&
                BlockTags.is(block, meta, BlockTag.GRASS)) {
            cir.setReturnValue(true);
        }

        // Loosen with Pointy Stick (not fully-grown grass though)
        if (ItemTags.isAll(stack, ItemTag.WOOD, ItemTag.CHISEL)) {
            if (BlockTags.is(block, meta, BlockTag.FIRM)) {
                if (BlockTags.is(block, meta, BlockTag.DIRT)) cir.setReturnValue(true);
                if (BlockTags.isAll(block, meta, BlockTag.GRASS, BlockTag.SPARSE)) cir.setReturnValue(true);
            }
        }

        // TODO: Firm-up with shovel right-click

    }

    @Inject(
            method = "convertBlock", at = @At("HEAD"), cancellable = true
    )
    private void abbyread$convertBlock(ItemStack stack, World world, int x, int y, int z, int fromSide, CallbackInfoReturnable<Boolean> cir){
        if (stack == null) return;

        Block block = (Block)(Object) this;
        int meta = world.getBlockMetadata(x, y, z);
        boolean swapped = false;

        // Loosen with Pointy Stick (not fully-grown grass though)
        if (ItemTags.isAll(stack, ItemTag.WOOD, ItemTag.CHISEL)) {
            if (BlockTags.is(block, meta, BlockTag.FIRM)) {
                if (BlockTags.is(block, meta, BlockTag.DIRT)) {
                    swapped = Convert.loosen(stack, block, meta, world, x, y, z, fromSide);
                }
                if (BlockTags.isAll(block, meta, BlockTag.GRASS, BlockTag.SPARSE)) {
                    swapped = Convert.loosen(stack, block, meta, world, x, y, z, fromSide);
                }
            }
        }

        // If sharp stone is used on a grass block, sparsen in stages
        if (ItemTags.isAll(stack, ItemTag.STONE, ItemTag.CHISEL)  &&
                BlockTags.is(block, meta, BlockTag.GRASS)) {
            swapped = Convert.sparsen(stack, block, meta, world, x, y, z, fromSide);
        }

        if (swapped) cir.setReturnValue(swapped);
        // otherwise allow rest of method to continue
    }

    @Inject(
            method = "getPlayerRelativeBlockHardness",
            at = @At("RETURN"),
            cancellable = true
    )
    private void abbyread$boostLooseBlocks(EntityPlayer player, World world, int x, int y, int z, CallbackInfoReturnable<Float> cir) {
        Block self = (Block)(Object)this;
        int meta = world.getBlockMetadata(x, y, z);

        Set<BlockTag> cats = BlockTags.of(self, meta);
        if (cats.contains(BlockTag.LOOSE)) {
            float base = cir.getReturnValue();
            cir.setReturnValue(base * Efficiency.modifier * 2);
        }

        if (cats.contains(BlockTag.LOG)) {
            float base = cir.getReturnValue();
            cir.setReturnValue(base * Efficiency.modifier * 2);
        }
    }
}
