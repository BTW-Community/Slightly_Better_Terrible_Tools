package btw.community.abbyread.sbtt;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import btw.community.abbyread.categories.*;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

@SuppressWarnings("UnnecessaryLocalVariable")
public class Convert {

    private static final boolean DEBUG = false;
    private static final int VERY_LOW_HEMP_SEED_CHANCE = 1000;

    public static boolean justConverted = false;

    // ---------- Public Methods ----------

    // Left-click-held conversions
    public static boolean tryConvert(ItemStack stack, EntityPlayer player, Block block, int meta, World world, int x, int y, int z, int side) {
        if (canConvert(stack, block, meta)) {
            if (!world.isRemote) debug("canConvert returned true.");
            return convert(stack, player, block, meta, world, x, y, z, side);
        }
        if (!world.isRemote) debug("canConvert returned false.");
        return false;
    }

    public static boolean canConvert(ItemStack stack, Block block, int meta) {
        if (stack == null || block == null) return false;

        if (ItemTags.isAll(stack, ItemTag.WOOD, ItemTag.CHISEL) &&
                BlockTags.is(block, meta, BlockTag.FIRM) &&
                (BlockTags.is(block, meta, BlockTag.DIRT) ||
                        BlockTags.isAll(block, meta, BlockTag.GRASS, BlockTag.SPARSE))) {
            return true;
        }

        if (ItemTags.isAll(stack, ItemTag.STONE, ItemTag.CHISEL) && BlockTags.is(block, meta, BlockTag.GRASS)) {
            return true;
        }

        if (ItemTags.is(stack, ItemTag.CLUB) && BlockTags.isAll(block, meta, BlockTag.DIRTLIKE, BlockTag.LOOSE_DIRTLIKE)) {
            return true;
        }
        return false;
    }

    // Block-invoked convert does not have access to usingEntity
    public static boolean convert(ItemStack stack, Block block, int meta, World world, int x, int y, int z, int side) {
        if (stack == null || block == null) return false;

        if (ItemTags.isAll(stack, ItemTag.WOOD, ItemTag.CHISEL) && canConvert(stack, block, meta)) {
            debug("Using loosen conversion");
            justConverted = loosen(stack, block, meta, world, x, y, z, side);
        }

        if (ItemTags.isAll(stack, ItemTag.STONE, ItemTag.CHISEL) && BlockTags.is(block, meta, BlockTag.GRASS)) {
            debug("Using sparsen conversion");
            justConverted = sparsen(stack, block, meta, world, x, y, z, side);
        }

        if ((ItemTags.is(stack, ItemTag.CLUB))
                && BlockTags.isAll(block, meta, BlockTag.DIRTLIKE, BlockTag.LOOSE_DIRTLIKE)) {
            debug("Using firm conversion");
            justConverted = firm(stack, block, meta, world, x, y, z, side);
        }

        return justConverted;
    }


    // Item-invoked convert has access to usingEntity
    public static boolean convert(ItemStack stack, EntityPlayer player, Block block, int meta, World world, int x, int y, int z, int side) {
        if (stack == null || block == null) return false;

        if (ItemTags.isAll(stack, ItemTag.WOOD, ItemTag.CHISEL) && canConvert(stack, block, meta)) {
            debug("Using loosen conversion");
            justConverted = loosen(stack, block, meta, world, x, y, z, side);
        }

        if (ItemTags.isAll(stack, ItemTag.STONE, ItemTag.CHISEL) && BlockTags.is(block, meta, BlockTag.GRASS)) {
            debug("Using sparsen conversion");
            justConverted = sparsen(stack, block, meta, world, x, y, z, side);
        }

        if ((ItemTags.is(stack, ItemTag.CLUB))
                && BlockTags.isAll(block, meta, BlockTag.DIRTLIKE, BlockTag.LOOSE_DIRTLIKE)) {
            debug("Using firm conversion");
            justConverted = firm(stack, block, meta, world, x, y, z, side);
        }

        return justConverted;
    }

    // Right-click-usage conversions
    public static boolean trySecondaryConvert(ItemStack stack, EntityPlayer player, Block block, int meta, World world, int x, int y, int z, int side) {
        if (secondaryCanConvert(stack, block, meta, world, x, y, z, side)) {
            if (!world.isRemote) debug("secondaryCanConvert returned true.");
            return secondaryConvert(stack, player, block, meta, world, x, y, z, side);
        }
        if (!world.isRemote) debug("secondaryCanConvert returned false.");
        return false;
    }

    public static boolean secondaryCanConvert(ItemStack stack, Block block, int meta, World world, int x, int y, int z, int side) {
        if (stack == null || block == null) return false;

        // firming
        if (ItemTags.is(stack, ItemTag.SHOVEL) &&
                BlockTags.is(block, meta, BlockTag.LOOSE_DIRTLIKE)) {
            return true;
        }

        // packing
        if (ItemTags.isButNot(stack, ItemTag.SHOVEL, ItemTag.STONE) &&
                BlockTags.isAll(block, meta,
                        BlockTag.DIRTLIKE, BlockTag.FIRM, BlockTag.CUBE)) {
            return true;
        }
        if (!world.isRemote) debug(BlockTags.getTags(block, meta).toString());
        if (ItemTags.isButNot(stack, ItemTag.SHOVEL, ItemTag.STONE) &&
                BlockTags.isAll(block, meta,
                        BlockTag.PACKED_EARTH, BlockTag.SLAB)) {
            return true; // Still must check if neighbor below is dirt
        }

        return false;
    }

    public static boolean secondaryConvert(ItemStack stack, EntityPlayer player, Block block, int meta, World world, int x, int y, int z, int side) {
        if (stack == null || block == null) return false;

        if ((ItemTags.is(stack, ItemTag.SHOVEL))
                && BlockTags.is(block, meta, BlockTag.LOOSE_DIRTLIKE)) {
            debug("Using firm conversion");
            if (firm(stack, block, meta, world, x, y, z, side)) {
                stack.damageItem(2, player);
            }
            return true;
        }

        if ((ItemTags.isButNot(stack, ItemTag.SHOVEL, ItemTag.STONE))
                && BlockTags.isAll(block, meta, BlockTag.DIRTLIKE, BlockTag.FIRM) && side == 1) {
            debug("Converting dirt block to packed-earth slab");
            if (pack(stack, block, meta, world, x, y, z, side)) {
                stack.damageItem(2, player);
            }
            return true;
        }

        if ((ItemTags.isButNot(stack, ItemTag.SHOVEL, ItemTag.STONE))
                && BlockTags.isAll(block, meta, BlockTag.PACKED_EARTH, BlockTag.SLAB) && side == 1) {
            debug("Attempting pack downward.");
            if (pack(stack, block, meta, world, x, y, z, side)) {
                stack.damageItem(2, player);
            }
            return true;
        }

        return false;
    }

    // ---------- Conversion Methods ----------


    public static boolean pack(ItemStack stack, Block block, int meta, World world, int x, int y, int z, int side) {
        if (block == null) return false;

        debug(block.getUnlocalizedName() + " with meta " + meta);
        Block newBlock = null;
        int newMeta = meta;

        // packing step 1
        if (BlockTags.isAll(block, meta, BlockTag.DIRTLIKE, BlockTag.FIRM) && side == 1) {
            newBlock = BTWBlocks.dirtSlab;
            // DirtSlabBlock.SUBTYPE_PACKED_EARTH gives 3, which is actually wrong.
            newMeta = 6; // matches full-block metadata value of AestheticOpaqueEarthBlock for packed earth
            swapBlock(world, x, y, z, block, meta, newBlock, newMeta);
            return true;
        }

        // packing step 2
        if (block == BTWBlocks.dirtSlab && meta == 6 && side == 1) {
            // check neighboring block below for potential two-block conversion
            debug(String.format("%d\n", world.getBlockId(x, y - 1, z)));
            if (world.getBlockId(x, y - 1, z) == Block.dirt.blockID) {

                newBlock = BTWBlocks.aestheticEarth;
                swapBlock(world, x, y - 1, z, block, meta, newBlock, 6);
                world.setBlockToAir(x, y, z);
            }
            return true;
        }


        return false;
    }

    public static boolean loosen(ItemStack stack, Block block, int meta, World world, int x, int y, int z, int fromSide) {
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
        if (BlockTags.isNotAll(block, meta, BlockTag.DIRTLIKE, BlockTag.LOOSE_DIRTLIKE)) return false;

        Block newBlock = null;
        int newMeta = meta;

        if (block == BTWBlocks.looseDirt) {
            newBlock = Block.dirt;
        }
        else if (block == BTWBlocks.looseDirtSlab) {
            newBlock = BTWBlocks.dirtSlab;
        }
        else if (block == BTWBlocks.looseSparseGrass) {
            newBlock = Block.grass;
            newMeta = 1;
        } else if (block == BTWBlocks.looseSparseGrassSlab) {
            newBlock = BTWBlocks.grassSlab;
            newMeta = 2;
        }

        return swapBlock(world, x, y, z, block, meta, newBlock, newMeta);
    }

    public static boolean sparsen(ItemStack stack, Block block, int meta, World world, int x, int y, int z, int fromSide) {
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

        if (newBlock != oldBlock || newMeta != oldMeta) {
            world.setBlockAndMetadataWithNotify(x, y, z, newBlock.blockID, newMeta);
            swapped = true;
            debug("Block swapped at (" + x + "," + y + "," + z + ")");
        }

        if (!world.isRemote && swapped) {
            world.playAuxSFX(BTWEffectManager.DIRT_TILLING_EFFECT_ID, x, y, z, 0);
        }

        return swapped;
    }

    private static void debug(String message) {
        if (DEBUG) System.out.println(message);
    }
}
