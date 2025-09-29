package btw.community.abbyread.sbtt;

import btw.block.BTWBlocks;
import btw.community.abbyread.categories.BlockCategories;
import btw.community.abbyread.categories.BlockCategory;
import btw.community.abbyread.categories.ItemCategories;
import btw.community.abbyread.categories.ItemCategory;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

import java.util.Set;

public class InteractionHandler {

    public static boolean handleBlockDestroyed(ItemStack stack, Block block, int meta, World world, int x, int y, int z) {
        Set<BlockCategory> blockCats = BlockCategories.of(block, meta);
        Set<ItemCategory> itemCats = ItemCategories.getCategories(stack);

        // Example: pointy stick loosens dirtlike blocks
        if (itemCats.contains(ItemCategory.CHISEL) && itemCats.contains(ItemCategory.WOOD)
                && blockCats.contains(BlockCategory.DIRTLIKE)
                && !blockCats.contains(BlockCategory.LOOSE)) {

            world.setBlockWithNotify(x, y, z, BTWBlocks.looseDirt.blockID);
            return true;
        }

        return false; // not handled
    }
}
