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
    private final Set<BlockSide> validSides; // optional: which sides are valid

    public ItemUseCombo(Set<ItemTag> validItemTags, Set<BlockTag> validBlockTags) {
        this(validItemTags, validBlockTags, null);
    }

    public ItemUseCombo(Set<ItemTag> validItemTags, Set<BlockTag> validBlockTags, Set<BlockSide> validSides) {
        this.validItemTags = validItemTags;
        this.validBlockTags = validBlockTags;
        this.specificItem = null;
        this.specificBlock = null;
        this.validSides = validSides;
    }

    public ItemUseCombo(Set<ItemTag> validItemTags, Block specificBlock) {
        this(validItemTags, specificBlock, null);
    }

    public ItemUseCombo(Set<ItemTag> validItemTags, Block specificBlock, Set<BlockSide> validSides) {
        this.validItemTags = validItemTags;
        this.validBlockTags = null;
        this.specificItem = null;
        this.specificBlock = specificBlock;
        this.validSides = validSides;
    }

    public ItemUseCombo(Item specificItem, Set<BlockTag> validBlockTags) {
        this(specificItem, validBlockTags, null);
    }

    public ItemUseCombo(Item specificItem, Set<BlockTag> validBlockTags, Set<BlockSide> validSides) {
        this.validItemTags = null;
        this.validBlockTags = validBlockTags;
        this.specificItem = specificItem;
        this.specificBlock = null;
        this.validSides = validSides;
    }

    public ItemUseCombo(Item specificItem, Block specificBlock) {
        this(specificItem, specificBlock, null);
    }

    public ItemUseCombo(Item specificItem, Block specificBlock, Set<BlockSide> validSides) {
        this.validItemTags = null;
        this.validBlockTags = null;
        this.specificItem = specificItem;
        this.specificBlock = specificBlock;
        this.validSides = validSides;
    }

    public boolean matches(ItemStack stack, Block block, int metadata) {
        return matches(stack, block, metadata, null); // null means any side
    }

    public boolean matches(ItemStack stack, Block block, int metadata, BlockSide side) {
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

        boolean sideMatches = validSides == null || side == null || validSides.contains(side);

        return itemMatches && blockMatches && sideMatches;
    }
}