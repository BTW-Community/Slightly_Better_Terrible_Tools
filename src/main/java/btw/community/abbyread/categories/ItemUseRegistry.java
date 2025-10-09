package btw.community.abbyread.categories;

import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;

import java.util.HashSet;
import java.util.Set;

/**
 * Registry of special item-use-on-block interactions.
 */
public class ItemUseRegistry {

    private static final Set<ItemUseCombo> ITEM_USE_COMBOS = new HashSet<>();

    static {
        // Special case: SHEARS on tall grass
        ITEM_USE_COMBOS.add(new ItemUseCombo(
                Set.of(ItemTag.SHEARS),
                Set.of(BlockTag.TALL_GRASS)
        ));

        // Special case: HOE on GRASS-tagged blocks
        ITEM_USE_COMBOS.add(new ItemUseCombo(
                Set.of(ItemTag.HOE),
                Set.of(BlockTag.GRASS)
        ));

        // Special case: HOE on DIRT-tagged blocks
        ITEM_USE_COMBOS.add(new ItemUseCombo(
                Set.of(ItemTag.HOE),
                Set.of(BlockTag.DIRT)
        ));
    }

    /**
     * Checks if the given item stack can be used on the given block for a special case.
     */
    public static boolean uniquelyUsefulCombo(ItemStack stack, Block block, int metadata) {
        for (ItemUseCombo combo : ITEM_USE_COMBOS) {
            if (combo.matches(stack, block, metadata)) return true;
        }
        return false;
    }
}
