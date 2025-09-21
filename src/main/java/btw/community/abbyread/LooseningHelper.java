package btw.community.abbyread;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import btw.block.blocks.DirtSlabBlock;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class LooseningHelper {

    /** Returns true if this block can be loosened */
    public static boolean canLoosenBlock(int blockID) {
        Block block = Block.blocksList[blockID];
        if (block == null) return false;

        return block == Block.dirt
                || block == Block.grass
                || block == BTWBlocks.dirtSlab
                || block == BTWBlocks.grassSlab;
    }

    /** Returns the loosened block ID, or -1 if not applicable */
    public static int getLoosenedBlockID(int fromBlockID, World world, int x, int y, int z) {
        System.out.println("[DEBUG] getLoosenedBlockID called for blockID: " + fromBlockID);

        // Special case: packed earth slabs should never loosen
        if (fromBlockID == BTWBlocks.dirtSlab.blockID &&
                BTWBlocks.dirtSlab.getSubtype(world.getBlockMetadata(x, y, z)) ==
                        DirtSlabBlock.SUBTYPE_PACKED_EARTH) {
            System.out.println("[DEBUG] Packed earth slab detected, not loosening.");
            return -1;
        }

        // Vanilla dirt -> loose dirt
        if (fromBlockID == Block.dirt.blockID) {
            return BTWBlocks.looseDirt.blockID;
        }

        // Vanilla grass -> loose sparse grass
        if (fromBlockID == Block.grass.blockID) {
            return BTWBlocks.looseSparseGrass.blockID;
        }

        // BTW dirt slab -> loose dirt slab
        if (fromBlockID == BTWBlocks.dirtSlab.blockID) {
            return BTWBlocks.looseDirtSlab.blockID;
        }

        // BTW grass slab -> loose sparse grass slab
        if (fromBlockID == BTWBlocks.grassSlab.blockID) {
            return BTWBlocks.looseSparseGrassSlab.blockID;
        }

        // TODO: Add more cases for other loosenable blocks
        System.out.println("[DEBUG] No loosened form for blockID: " + fromBlockID);
        return -1;
    }

    /** Loosen the block if applicable, returns true if the block was replaced */
    public static boolean tryLoosenBlock(ItemStack stack, World world, int x, int y, int z, int blockID) {
        System.out.println("[DEBUG] tryLoosenBlock called for blockID: " + blockID);

        if (stack == null) {
            System.out.println("[DEBUG] stack was null");
            return false;
        }
        if (stack.getItem() != BTWItems.pointyStick) {
            System.out.println("[DEBUG] stack.getItem() was not pointyStick");
            return false;
        }

        int toBlockID = getLoosenedBlockID(blockID, world, x, y, z);
        System.out.println("[DEBUG] toBlockID: " + toBlockID);

        if (toBlockID != -1) {
            world.setBlockWithNotify(x, y, z, toBlockID);
            System.out.println("[DEBUG] Block replaced with: " + toBlockID);
            return true;
        }

        return false;
    }
}
