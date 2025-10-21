package btw.community.abbyread.categories;

import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

import java.util.Set;

/**
 * Represents a valid interaction between an item (or set of item tags)
 * and a block (or set of block tags).
 */
public class ItemUseCombo {
    private final Set<ItemTag> validItemTags;
    private final Set<BlockTag> validBlockTags;
    private final Item specificItem;
    private final Block specificBlock; // optional, for exact block matches

    public ItemUseCombo(Set<ItemTag> validItemTags, Set<BlockTag> validBlockTags) {
        this.validItemTags = validItemTags;
        this.validBlockTags = validBlockTags;
        this.specificItem = null;
        this.specificBlock = null;
    }

    public ItemUseCombo(Set<ItemTag> validItemTags, Block specificBlock) {
        this.validItemTags = validItemTags;
        this.validBlockTags = null;
        this.specificItem = null;
        this.specificBlock = specificBlock;
    }

    public ItemUseCombo(Item specificItem, Set<BlockTag> validBlockTags) {
        this.validItemTags = null;
        this.validBlockTags = validBlockTags;
        this.specificItem = specificItem;
        this.specificBlock = null;
    }

    public ItemUseCombo(Item specificItem, Block specificBlock) {
        this.validItemTags = null;
        this.validBlockTags = null;
        this.specificItem = specificItem;
        this.specificBlock = specificBlock;
    }

    /**
     * Checks if the given item stack and block match this combo.
     */
    public boolean matches(ItemStack stack, Block block, int metadata) {
        boolean itemMatches;
        if (specificItem != null) {
            itemMatches = stack != null && stack.getItem() == specificItem;
        } else if (validItemTags != null) {
            itemMatches = validItemTags.stream().anyMatch(tag -> ItemTags.is(stack, tag));
        } else {
            itemMatches = false;
        }

        boolean blockMatches;
        if (specificBlock != null) {
            blockMatches = block == specificBlock;
        } else if (validBlockTags != null) {
            blockMatches = validBlockTags.stream().anyMatch(tag -> BlockTags.is(block, metadata, tag));
        } else {
            blockMatches = false;
        }

        return itemMatches && blockMatches;
    }

}
