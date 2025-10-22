package btw.community.abbyread.categories;

import btw.item.BTWItems;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

import java.util.HashSet;
import java.util.Set;

/**
 * Registry of special item-use-on-block interactions.
 */
public class ItemUseRegistry {

    private static final Set<ItemUseCombo> LEFTCLICK_COMBOS = new HashSet<>();

    static {
        // Special case: SHEARS on tall grass
        LEFTCLICK_COMBOS.add(new ItemUseCombo(
                Set.of(ItemTag.SHEARS),
                Set.of(BlockTag.TALL_GRASS)
        ));

        // Special case: HOE on GRASS-tagged blocks
        LEFTCLICK_COMBOS.add(new ItemUseCombo(
                Set.of(ItemTag.HOE),
                Set.of(BlockTag.GRASS)
        ));

        // Special case: HOE on DIRT-tagged blocks
        LEFTCLICK_COMBOS.add(new ItemUseCombo(
                Set.of(ItemTag.HOE),
                Set.of(BlockTag.DIRT)
        ));
    }

    private static final Set<ItemUseCombo> RIGHTCLICK_COMBOS = new HashSet<>();

    static {
        // Special case: Shovel secondary use on loose dirt/grass (for firming)
        RIGHTCLICK_COMBOS.add(new ItemUseCombo(
                Set.of(ItemTag.SHOVEL),
                Set.of(BlockTag.LOOSE_DIRTLIKE)
        ));

        // Special case: >= iron shovel secondary use on regular dirt blocks (to make packed earth slab)
        RIGHTCLICK_COMBOS.add(new ItemUseCombo(
                Item.shovelIron,
                Block.dirt
        ));
        RIGHTCLICK_COMBOS.add(new ItemUseCombo(
                Item.shovelDiamond,
                Block.dirt
        ));
        RIGHTCLICK_COMBOS.add(new ItemUseCombo(
                BTWItems.steelShovel,
                Block.dirt
        ));
    }

    /**
     * Checks for special item-on-block combo, left-click, held-to-destruction
     */
    public static boolean usefulLeftClickCombo(ItemStack stack, Block block, int metadata) {
        for (ItemUseCombo combo : LEFTCLICK_COMBOS) {
            if (combo.matches(stack, block, metadata)) return true;
        }
        return false;
    }

    /**
     * Checks for special item-on-block combo, right-click
     */
    public static boolean usefulRightClickCombo(ItemStack stack, Block block, int metadata) {
        for (ItemUseCombo combo : RIGHTCLICK_COMBOS) {
            if (combo.matches(stack, block, metadata)) return true;
        }
        return false;
    }

    /**
     * Checks for special item-on-block combo, right-click, with side info
     */
    public static boolean usefulRightClickCombo(ItemStack stack, Block block, int metadata, BlockSide side) {
        for (ItemUseCombo combo : RIGHTCLICK_COMBOS) {
            if (combo.matches(stack, block, metadata, side)) return true;
        }
        return false;
    }
}
