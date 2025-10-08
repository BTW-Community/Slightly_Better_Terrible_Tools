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
import org.lwjgl.Sys;

public class Convert {

    private static final boolean DEBUG = false;
    private static final int VERY_LOW_HEMP_SEED_CHANCE = 1000;

    public static boolean justConverted = false;
    public static int itemDamageAmount = 1;

    // ---------- Public Methods ----------

    public static boolean canConvert(ItemStack stack, Block block, int meta) {
        if (stack == null || block == null) return false;

        if (ItemTags.isAll(stack, ItemTag.WOOD, ItemTag.CHISEL)) {
            System.out.println("via canConvert... Block tags: " + BlockTags.getTags(block, meta));
            boolean result = BlockTags.is(block, meta, BlockTag.FIRM) &&
                    (BlockTags.is(block, meta, BlockTag.DIRT) || BlockTags.isAll(block, meta, BlockTag.GRASS, BlockTag.SPARSE));
            debug("Checking WOOD+CHISEL: " + result);
            return result;
        }

        if (ItemTags.isAll(stack, ItemTag.STONE, ItemTag.CHISEL)) {
            boolean result = BlockTags.is(block, meta, BlockTag.GRASS);
            debug("Checking STONE+CHISEL: " + result);
            return result;
        }

        if (ItemTags.is(stack, ItemTag.CLUB)) {
            boolean result = BlockTags.isAll(block, meta, BlockTag.DIRTLIKE, BlockTag.LOOSE_DIRTLIKE);
            debug("Checking CLUB: " + result);
            return result;
        }

        if (ItemTags.is(stack, ItemTag.SHOVEL)) {
            boolean result = BlockTags.isAll(block, meta, BlockTag.DIRTLIKE, BlockTag.LOOSE_DIRTLIKE);
            debug("Checking SHOVEL: " + result);
            return result;
        }

        return false;
    }

    public static boolean convert(ItemStack stack, Block block, int meta, World world, int x, int y, int z, int fromSide) {
        if (stack == null || block == null) return false;

        debug("convert called with stack=" + stack + ", block=" + block + ", meta=" + meta + ", coords=(" + x + "," + y + "," + z + ")");

        if (ItemTags.isAll(stack, ItemTag.WOOD, ItemTag.CHISEL) && canConvert(stack, block, meta)) {
            debug("Using loosen conversion");
            return loosen(stack, block, meta, world, x, y, z, fromSide);
        }

        if (ItemTags.isAll(stack, ItemTag.STONE, ItemTag.CHISEL) && BlockTags.is(block, meta, BlockTag.GRASS)) {
            debug("Using sparsen conversion");
            return sparsen(stack, block, meta, world, x, y, z, fromSide);
        }

        if ((ItemTags.is(stack, ItemTag.CLUB) || ItemTags.is(stack, ItemTag.SHOVEL))
                && BlockTags.isAll(block, meta, BlockTag.DIRTLIKE, BlockTag.LOOSE_DIRTLIKE)) {
            debug("Using firm conversion");
            return firm(stack, block, meta, world, x, y, z, fromSide);
        }

        return false;
    }

    // ---------- Conversion Methods ----------

    public static boolean loosen(ItemStack stack, Block block, int meta, World world, int x, int y, int z, int fromSide) {
        debug("loosen called for block=" + block + ", meta=" + meta + " at (" + x + "," + y + "," + z + ")");
        if (BlockTags.isNotAll(block, meta, BlockTag.DIRTLIKE, BlockTag.FIRM)) return false;

        Block newBlock = null;
        int newMeta = meta;

        if (BlockTags.is(block, meta, BlockTag.DIRT)) {
            if (BlockTags.is(block, meta, BlockTag.CUBE)) newBlock = BTWBlocks.looseDirt;
            else if (BlockTags.is(block, meta, BlockTag.SLAB)) newBlock = BTWBlocks.looseDirtSlab;
        } else if (BlockTags.isAll(block, meta, BlockTag.SPARSE, BlockTag.GRASS)) {
            if (BlockTags.is(block, meta, BlockTag.CUBE)) {
                newBlock = BTWBlocks.looseSparseGrass;
                newMeta = 0;
            } else if (BlockTags.is(block, meta, BlockTag.SLAB)) {
                newBlock = BTWBlocks.looseSparseGrassSlab;
                newMeta = 0;
            }
        }

        return swapBlock(world, x, y, z, block, meta, newBlock, newMeta);
    }

    public static boolean firm(ItemStack stack, Block block, int meta, World world, int x, int y, int z, int fromSide) {
        debug("firm called for block=" + block + ", meta=" + meta + " at (" + x + "," + y + "," + z + ")");
        if (BlockTags.isNotAll(block, meta, BlockTag.DIRTLIKE, BlockTag.LOOSE_DIRTLIKE)) return false;

        Block newBlock = null;
        int newMeta = meta;
        boolean toSwap = false;

        if (block == BTWBlocks.looseDirt) {
            newBlock = Block.dirt;
            toSwap = true;
        }
        else if (block == BTWBlocks.looseDirtSlab) {
            newBlock = BTWBlocks.dirtSlab;
            toSwap = true;
        }
        else if (block == BTWBlocks.looseSparseGrass) {
            newBlock = Block.grass;
            newMeta = 1;
            toSwap = true;
        } else if (block == BTWBlocks.looseSparseGrassSlab) {
            newBlock = BTWBlocks.grassSlab;
            newMeta = 2;
            toSwap = true;
        }

        if (toSwap && ItemTags.is(stack, ItemTag.CLUB)) itemDamageAmount = 2;

        return swapBlock(world, x, y, z, block, meta, newBlock, newMeta);
    }

    public static boolean sparsen(ItemStack stack, Block block, int meta, World world, int x, int y, int z, int fromSide) {
        debug("sparsen called for block=" + block + ", meta=" + meta + " at (" + x + "," + y + "," + z + ")");
        if (!BlockTags.is(block, meta, BlockTag.GRASS)) return false;

        Block newBlock = null;
        int newMeta = meta;

        if (BlockTags.is(block, meta, BlockTag.CUBE)) {
            if (BlockTags.isAll(block, meta, BlockTag.FIRM, BlockTag.FULLY_GROWN)) {
                newBlock = block;
                newMeta = 1;
            } else if (BlockTags.isAll(block, meta, BlockTag.FIRM, BlockTag.SPARSE)) {
                newBlock = Block.dirt;
                newMeta = 0;
            } else if (BlockTags.isAll(block, meta, BlockTag.LOOSE_DIRTLIKE, BlockTag.SPARSE)) {
                newBlock = BTWBlocks.looseDirt;
            }
        } else if (BlockTags.is(block, meta, BlockTag.SLAB)) {
            if (BlockTags.isAll(block, meta, BlockTag.FIRM, BlockTag.FULLY_GROWN)) {
                newBlock = block;
                newMeta = 2;
            } else if (BlockTags.isAll(block, meta, BlockTag.FIRM, BlockTag.SPARSE)) {
                newBlock = BTWBlocks.dirtSlab;
                newMeta = 0;
            } else if (BlockTags.isAll(block, meta, BlockTag.LOOSE_DIRTLIKE, BlockTag.SPARSE)) {
                newBlock = BTWBlocks.looseDirtSlab;
            }
        }

        // Eject hemp seeds randomly
        if (!world.isRemote && world.rand.nextInt(VERY_LOW_HEMP_SEED_CHANCE) == 0) {
            debug("Ejecting hemp seeds at (" + x + "," + y + "," + z + ")");
            ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, new ItemStack(BTWItems.hempSeeds), fromSide);
        }

        return swapBlock(world, x, y, z, block, meta, newBlock, newMeta);
    }

    // ---------- Helper Methods ----------

    private static boolean swapBlock(World world, int x, int y, int z, Block oldBlock, int oldMeta, Block newBlock, int newMeta) {
        if (newBlock == null) return false;

        boolean swapped = false;

        if (newBlock != oldBlock) {
            world.setBlockWithNotify(x, y, z, newBlock.blockID);
            swapped = true;
            debug("Block swapped at (" + x + "," + y + "," + z + ")");
        }

        if (newMeta != oldMeta) {
            world.setBlockMetadataWithNotify(x, y, z, newMeta);
            swapped = true;
            debug("Metadata updated at (" + x + "," + y + "," + z + ")");
        }

        if (!world.isRemote && swapped) {
            justConverted = true;
            world.playAuxSFX(BTWEffectManager.DIRT_TILLING_EFFECT_ID, x, y, z, 0);
        }

        return swapped;
    }

    private static void debug(String message) {
        if (DEBUG) System.out.println(message);
    }
}
