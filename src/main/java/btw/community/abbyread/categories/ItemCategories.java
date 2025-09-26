package btw.community.abbyread.categories;

import btw.item.BTWItems;
import btw.item.items.*;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ItemCategories {

    // Map itemID -> set of categories
    private static final Map<Integer, Set<ItemCategory>> ITEM_CATEGORY_MAP = new HashMap<>();

    static {
        // Pointy Stick (a.k.a. Wood Chisel)
        addCategory(BTWItems.pointyStick.itemID,
                ItemCategory.TOOL,
                ItemCategory.CHISEL,
                ItemCategory.WOOD,
                ItemCategory.POINTY
        );

        // Sharp Stone (a.k.a. Stone Chisel)
        addCategory(BTWItems.sharpStone.itemID,
                ItemCategory.TOOL,
                ItemCategory.CHISEL,
                ItemCategory.STONE,
                ItemCategory.SHARP
        );

        // Stone Shovel
        addCategory(Item.shovelStone.itemID,
                ItemCategory.TOOL,
                ItemCategory.SHOVEL,
                ItemCategory.STONE
        );

        // Steel Battleaxe
        addCategory(BTWItems.battleaxe.itemID,
                ItemCategory.TOOL,
                ItemCategory.BATTLEAXE,
                ItemCategory.STEEL,
                ItemCategory.WEAPON
        );

        // Iron Sword
        addCategory(Item.swordIron.itemID,
                ItemCategory.WEAPON,
                ItemCategory.SWORD,
                ItemCategory.WOOD
        );

        // Fuel example
        addCategory(Item.coal.itemID,
                ItemCategory.FUEL
        );

        // Consumable example
        addCategory(Item.appleRed.itemID,
                ItemCategory.FOOD
        );

        // Add additional items similarly...
    }

    /**
     * Convenience method for adding a new item mapping.
     */
    private static void addCategory(int itemID, ItemCategory... categories) {
        Set<ItemCategory> categorySet = new HashSet<>();
        Collections.addAll(categorySet, categories);
        ITEM_CATEGORY_MAP.put(itemID, Collections.unmodifiableSet(categorySet));
    }

    /**
     * Get the categories of a specific ItemStack.
     */
    public static Set<ItemCategory> getCategories(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return Collections.emptySet();
        Set<ItemCategory> cats = ITEM_CATEGORY_MAP.get(stack.getItem().itemID);
        return cats != null ? cats : Collections.emptySet();
    }

    /**
     * Quick check if an item belongs to a category.
     */
    public static boolean hasCategory(ItemStack stack, ItemCategory category) {
        return getCategories(stack).contains(category);
    }
}
