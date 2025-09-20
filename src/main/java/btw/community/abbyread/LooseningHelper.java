package btw.community.abbyread;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class LooseningHelper {
    public static boolean canLoosenBlock(int blockID){
        if (blockID == Block.dirt.blockID)          return true;
        if (blockID == BTWBlocks.dirtSlab.blockID)  return true;
        if (blockID == Block.grass.blockID)         return true;
        if (blockID == BTWBlocks.grassSlab.blockID) return true;
        return false;
    }

    /** Returns the loosened block ID, or -1 if not applicable */
    public static int getLoosenedBlockID(World world, int x, int y, int z, int fromBlockID) {
        System.out.println("getLoosenedBlockID called.");
        // Special case: packed earth slabs should never loosen
        if (fromBlockID == BTWBlocks.dirtSlab.blockID &&
                BTWBlocks.dirtSlab.getSubtype(world.getBlockMetadata(x, y, z)) ==
                        btw.block.blocks.DirtSlabBlock.SUBTYPE_PACKED_EARTH) {
            return -1;
        }

        if (fromBlockID == Block.dirt.blockID) return BTWBlocks.looseDirt.blockID;
        if (fromBlockID == BTWBlocks.dirtSlab.blockID) return BTWBlocks.looseDirtSlab.blockID;
        if (fromBlockID == Block.grass.blockID){
            System.out.println("Grass block detected.");
            return BTWBlocks.looseSparseGrass.blockID;
        }
        if (fromBlockID == BTWBlocks.grassSlab.blockID) return BTWBlocks.looseSparseGrassSlab.blockID;

        return -1;
    }

    /** Loosen the block if applicable, returns true if the block was replaced */
    public static boolean tryLoosenBlock(ItemStack stack, World world, int x, int y, int z) {
        if (stack == null) {
            System.out.println("stack was null.");
            return false;
        }
        if (stack.getItem() != BTWItems.pointyStick) {
            System.out.println("stack.getItem() was not pointyStick.");
            return false;
        }

        int fromBlockID = world.getBlockId(x, y, z);
        int toBlockID = getLoosenedBlockID(world, x, y, z, fromBlockID);
        System.out.println("blockID at target: " + toBlockID);

        System.out.println("toBlockID: " + toBlockID);

        if (toBlockID != -1) {
            world.setBlockWithNotify(x, y, z, toBlockID);
            System.out.println("Block replaced with: " + toBlockID);
            return true;
        }

        return false;
    }
}
