package btw.community.abbyread.sbtt;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import btw.community.abbyread.categories.BlockTags;
import btw.community.abbyread.categories.BlockTag;
import btw.community.abbyread.categories.ItemTag;
import btw.community.abbyread.categories.ItemTags;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class Convert {

    public static boolean canConvert(ItemStack stack, Block block, int meta) {
        if (stack == null || block == null) return false;

        // Allow loosening dirt and sparse grass using pointy stick
        if (ItemTags.isAll(stack, ItemTag.WOOD, ItemTag.CHISEL)) {
            return BlockTags.is(block, meta, BlockTag.FIRM) &&
                    (BlockTags.is(block, meta, BlockTag.DIRT) || BlockTags.isAll(block, meta, BlockTag.GRASS, BlockTag.SPARSE));
        }

        // Allow sparsening grass using sharp stone
        if (ItemTags.isAll(stack, ItemTag.STONE, ItemTag.CHISEL)) {
            return BlockTags.is(block, meta, BlockTag.GRASS);
        }

        // Allow firming-up loose dirtlikes using shovel right-click
        /*
        // I think canConvert and convert are for left-click block harvesting only.
        // Using convert alone might prevent left-click but allow converting.
        if (ItemTags.is(stack, ItemTag.SHOVEL)) {
            return BlockTags.is(block, meta, BlockTag.LOOSE) &&
                    (BlockTags.is(block, meta, BlockTag.DIRT) || BlockTags.isAll(block, meta, BlockTag.GRASS, BlockTag.SPARSE));
        }
        */

        return false;
    }

    public static boolean convert(ItemStack stack, Block block, int meta, World world, int x, int y, int z, int fromSide) {
        if (stack == null || block == null) return false;

        // Loosen dirt and sparse grass using pointy stick
        if (ItemTags.isAll(stack, ItemTag.WOOD, ItemTag.CHISEL) &&
                BlockTags.is(block, meta, BlockTag.FIRM) &&
                (BlockTags.is(block, meta, BlockTag.DIRT) || BlockTags.isAll(block, meta, BlockTag.GRASS, BlockTag.SPARSE))) {
            return loosen(stack, block, meta, world, x, y, z, fromSide);
        }

        // Sparsen grass using sharp stone
        if (ItemTags.isAll(stack, ItemTag.STONE, ItemTag.CHISEL) &&
                BlockTags.is(block, meta, BlockTag.GRASS)) {
            return sparsen(stack, block, meta, world, x, y, z, fromSide);
        }

        // Firm-up loose dirtlikes using shovel right-click
        if (ItemTags.is(stack, ItemTag.SHOVEL)) {
            if (BlockTags.isAll(block, meta, BlockTag.LOOSE, BlockTag.DIRTLIKE)) {
                return firm(stack, block, meta, world, x, y, z, fromSide);
            }
        }

        return false;
    }

    public static boolean loosen(ItemStack stack, Block block, int meta, World world, int x, int y, int z, int fromSide) {
        // Must be a firm dirtlike block. Otherwise, return early, indicating it was not loosened.
        if (BlockTags.isNotAll(block, meta, BlockTag.DIRTLIKE, BlockTag.FIRM)) return false;

        boolean swapped = false;
        Block newBlock = null;
        int newMeta = meta;

        // Able to loosen:
        // dirt,
        // dirt slab,
        // sparse grass,
        // sparse grass slab

        if (BlockTags.is(block, meta, BlockTag.CUBE)) {
            if (block == Block.dirt) newBlock = BTWBlocks.looseDirt;
            else if (BlockTags.isAll(block, meta, BlockTag.GRASS, BlockTag.SPARSE)) {
                newBlock = BTWBlocks.looseSparseGrass;
                newMeta = 0; // looseSparseGrass already sparse by default
            }
        }
        else if (BlockTags.isAll(block, meta, BlockTag.SLAB)) {
            if (block == BTWBlocks.dirtSlab) newBlock = BTWBlocks.looseDirtSlab;
            else if (BlockTags.isAll(block, meta, BlockTag.GRASS, BlockTag.SPARSE)) {
                newBlock = BTWBlocks.looseSparseGrassSlab;
                newMeta = 0; // looseSparseGrassSlab already sparse by default
            }
        }

        if (newBlock == null) return false;

        // Apply block or metadata changes
        if (newBlock != block) {
            world.setBlockWithNotify(x, y, z, newBlock.blockID);
            swapped = true;
        }
        if (newMeta != meta) {
            world.setBlockMetadataWithNotify(x, y, z, newMeta);
            swapped = true;
        }

        if (!world.isRemote && swapped) {
            world.playAuxSFX(BTWEffectManager.DIRT_TILLING_EFFECT_ID, x, y, z, 0);
        }

        return swapped;
    }

    public static boolean firm(ItemStack stack, Block block, int meta, World world, int x, int y, int z, int fromSide) {
        // Must be a loose dirtlike block. Otherwise, return early, indicating it was not made firm.
        if (BlockTags.isNotAll(block, meta, BlockTag.DIRTLIKE, BlockTag.LOOSE)) return false;

        boolean swapped = false;
        Block newBlock = null;
        int newMeta = meta;

        // Able to firm up:
        // loose dirt,
        // loose dirt slab,
        // loose sparse grass,
        // loose sparse grass slab

        if (block == BTWBlocks.looseDirt) newBlock = Block.dirt;
        if (block == BTWBlocks.looseDirtSlab) newBlock = BTWBlocks.dirtSlab;
        if (block == BTWBlocks.looseSparseGrass) {
            newBlock = Block.grass;
            newMeta = 1; // 1: Sparse
        }
        if (block == BTWBlocks.looseSparseGrassSlab) {
            newBlock = BTWBlocks.grassSlab;
            newMeta = 2; // 2: Sparse
        }

        if (newBlock != null) {

            // Apply block or metadata changes
            if (newBlock != block) {
                world.setBlockWithNotify(x, y, z, newBlock.blockID);
                swapped = true;
            }
            if (newMeta != meta) {
                world.setBlockMetadataWithNotify(x, y, z, newMeta);
                swapped = true;
            }

            if (!world.isRemote && swapped) {
                world.playAuxSFX(BTWEffectManager.BLOCK_CONVERT_EFFECT_ID, x, y, z, 0);
            }
        }

        return swapped;
    }

    /**
     * Sparse conversion:
     * - Grass → sparse grass
     * - Sparse grass → dirt
     */
    public static boolean sparsen(ItemStack stack, Block block, int meta, World world, int x, int y, int z, int fromSide) {
        if (BlockTags.isNot(block, meta, BlockTag.GRASS)) return false;

        boolean swapped = false;
        Block newBlock = null;
        int newMeta = meta;

        if (BlockTags.is(block, meta, BlockTag.CUBE)) {
            // firm fully-grown grass block → firm sparse grass block
            if (BlockTags.isAll(block, meta, BlockTag.FIRM, BlockTag.FULLY_GROWN)) {
                newBlock = block;
                newMeta = 1; // 1: Sparse
            }
            // firm sparse → firm dirt
            else if (BlockTags.isAll(block, meta, BlockTag.FIRM, BlockTag.SPARSE)) {
                newBlock = Block.dirt;
                newMeta = 0; // dirt has no sparse variant
            }
            // loose sparse grass → loose dirt
            else if (BlockTags.isAll(block, meta, BlockTag.LOOSE, BlockTag.SPARSE)) {
                newBlock = BTWBlocks.looseDirt;
            }
            // LOOSE and FULLY_GROWN is not possible

        } else if (BlockTags.is(block, meta, BlockTag.SLAB)) {
            // firm fully-grown grass slab → firm sparse grass slab
            if (BlockTags.isAll(block, meta, BlockTag.FIRM, BlockTag.FULLY_GROWN)) {
                newBlock = block;
                newMeta = 2; // 2: Sparse
            }
            // firm sparse slab → firm dirt slab
            else if (BlockTags.isAll(block, meta, BlockTag.FIRM, BlockTag.SPARSE)) {
                newBlock = BTWBlocks.dirtSlab;
                newMeta = 0; // no longer needs sparseness metadata value
            }
            // loose sparse slab → loose dirt slab
            else if (BlockTags.isAll(block, meta, BlockTag.LOOSE, BlockTag.SPARSE)) {
                newBlock = BTWBlocks.looseDirtSlab;
            }
            // LOOSE and FULLY_GROWN is not possible

        }

        if (newBlock != null) {
            final int VERY_LOW_HEMP_SEED_CHANCE = 1000;
            if (!world.isRemote && world.rand.nextInt(VERY_LOW_HEMP_SEED_CHANCE) == 0) {
                ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z,
                        new ItemStack(BTWItems.hempSeeds), fromSide);
            }

            // Apply block or metadata changes
            if (newBlock != block) {
                world.setBlockWithNotify(x, y, z, newBlock.blockID);
                swapped = true;
            }
            if (newMeta != meta) {
                world.setBlockMetadataWithNotify(x, y, z, newMeta);
                swapped = true;
            }

            if (!world.isRemote && swapped) {
                world.playAuxSFX(BTWEffectManager.DIRT_TILLING_EFFECT_ID, x, y, z, 0);
            }
        }

        return swapped;
    }
}
