package btw.community.abbyread.sbtt;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import btw.community.abbyread.categories.BlockTags;
import btw.community.abbyread.categories.BlockTag;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import net.minecraft.src.Block;
import net.minecraft.src.BlockGrass;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class Helper {

    public static Block loosen(Block block) {
        if (BlockTags.isButNot(block, 0, BlockTag.DIRTLIKE, BlockTag.LOOSE)) {
            if (block == Block.dirt) return BTWBlocks.looseDirt;
            if (block == BTWBlocks.dirtSlab) return BTWBlocks.looseDirtSlab;
            if (block == Block.grass) return BTWBlocks.looseSparseGrass;
            if (block == BTWBlocks.grassSlab) return BTWBlocks.looseSparseGrassSlab;
        }
        return block;
    }

    public static Block firm(Block block) {
        if (BlockTags.isAll(block, 0, BlockTag.DIRTLIKE, BlockTag.LOOSE)) {
            if (block == BTWBlocks.looseDirt) return Block.dirt;
            if (block == BTWBlocks.looseDirtSlab) return BTWBlocks.dirtSlab;
            if (block == BTWBlocks.looseSparseGrass) return Block.grass;
            if (block == BTWBlocks.looseSparseGrassSlab) return BTWBlocks.grassSlab;
        }
        return block;
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

        if (BlockTags.is(block, meta, BlockTag.GRASS)) {
            if (BlockTags.isNot(block, meta, BlockTag.SLAB)) {
                // firm lush → firm sparse
                if (BlockTags.isNot(block, meta, BlockTag.SPARSE, BlockTag.LOOSE)) {
                    ((BlockGrass) block).setSparse(world, x, y, z);
                    newMeta = world.getBlockMetadata(x, y, z); // re-fetch updated meta
                    newBlock = Block.grass;
                }
                // firm sparse → firm dirt
                else if (BlockTags.isButNot(block, meta, BlockTag.SPARSE, BlockTag.LOOSE)) {
                    newBlock = Block.dirt;
                    newMeta = 0; // dirt has no sparse variant
                }
                // loose sparse grass → loose dirt
                else if (BlockTags.isAll(block, meta, BlockTag.SPARSE, BlockTag.LOOSE)) {
                    newBlock = BTWBlocks.looseDirt;
                }
            } else if (BlockTags.is(block, meta, BlockTag.SLAB)) {
                // firm lush slab → firm sparse slab
                if (BlockTags.isNot(block, meta, BlockTag.SPARSE, BlockTag.LOOSE)) {
                    ((BlockGrass) block).setSparse(world, x, y, z);
                    newMeta = world.getBlockMetadata(x, y, z); // re-fetch updated meta
                    newBlock = BTWBlocks.grassSlab;
                }
                // firm sparse slab → firm dirt slab
                else if (BlockTags.isButNot(block, meta, BlockTag.SPARSE, BlockTag.LOOSE)) {
                    newBlock = BTWBlocks.dirtSlab;
                }
                // loose sparse slab → loose dirt slab
                else if (BlockTags.isAll(block, meta, BlockTag.SPARSE, BlockTag.LOOSE)) {
                    newBlock = BTWBlocks.looseDirtSlab;
                }
            }
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
