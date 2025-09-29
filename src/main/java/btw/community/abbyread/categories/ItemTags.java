package btw.community.abbyread.categories;

import btw.item.BTWItems;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.*;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class ItemTags {

    // ===== Explicitly mapped items =====

    private static final Map<Integer, Set<ItemTag>> ITEM_TAG_MAP = new HashMap<>();

    static {
        // Pointy Stick (Wood Chisel)
        addTags(BTWItems.pointyStick.itemID,
                ItemTag.TOOL,
                ItemTag.CHISEL,
                ItemTag.WOOD,
                ItemTag.POINTY
        );

        // Sharp Stone (Stone Chisel)
        addTags(BTWItems.sharpStone.itemID,
                ItemTag.TOOL,
                ItemTag.CHISEL,
                ItemTag.STONE,
                ItemTag.SHARP
        );

        // Stone Shovel
        addTags(Item.shovelStone.itemID,
                ItemTag.TOOL,
                ItemTag.SHOVEL,
                ItemTag.STONE
        );

        // Steel Battleaxe
        addTags(BTWItems.battleaxe.itemID,
                ItemTag.TOOL,
                ItemTag.BATTLEAXE,
                ItemTag.STEEL,
                ItemTag.WEAPON
        );

        // Iron Sword
        addTags(Item.swordIron.itemID,
                ItemTag.WEAPON,
                ItemTag.SWORD,
                ItemTag.IRON
        );

        // Fuel example
        addTags(Item.coal.itemID, ItemTag.FUEL);

        // Consumable example
        addTags(Item.appleRed.itemID, ItemTag.FOOD);
    }

    private static void addTags(int itemID, ItemTag... tags) {
        Set<ItemTag> tagSet = new HashSet<>();
        Collections.addAll(tagSet, tags);
        ITEM_TAG_MAP.put(itemID, Collections.unmodifiableSet(tagSet));
    }

    // ===== Public API =====

    public static Set<ItemTag> getTags(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return Collections.emptySet();
        return ITEM_TAG_MAP.getOrDefault(stack.getItem().itemID, Collections.emptySet());
    }

    public static boolean is(ItemStack stack, ItemTag tag) {
        Set<ItemTag> stackTags = getTags(stack);
        return stackTags.contains(tag);
    }

    public static boolean isNot(ItemStack stack, ItemTag tag) {
        Set<ItemTag> stackTags = getTags(stack);
        return !stackTags.contains(tag);
    }

    public static boolean isNotAny(ItemStack stack, ItemTag... tags) {
        return  !isAny(stack, tags);
    }

    public static boolean isNotAll(ItemStack stack, ItemTag... tags) {
        return  !isAll(stack, tags);
    }

    public static boolean isAny(ItemStack stack, ItemTag... tags) {
        Set<ItemTag> stackTags = getTags(stack);
        for (ItemTag tag : tags) {
            if (stackTags.contains(tag)) return true;
        }
        return false;
    }

    public static boolean isAll(ItemStack stack, ItemTag... tags) {
        Set<ItemTag> stackTags = getTags(stack);
        for (ItemTag tag : tags) {
            if (!stackTags.contains(tag)) return false;
        }
        return true;
    }

    public static boolean isButNot(ItemStack stack, ItemTag isTag, ItemTag... notTags) {
        return is(stack, isTag) && !isAny(stack, notTags);
    }

    // ===== Automatic tagging using reflection =====

    private static final Logger LOGGER = LogManager.getLogger("ItemTags");

    private static final Set<Item> STONE_ITEMS;
    static {
        Set<Item> temp = new HashSet<>();
        Field[] fields = BTWItems.class.getFields(); // all public static fields

        for (Field field : fields) {
            try {
                Object value = field.get(null); // static field
                if (value instanceof Item && field.getName().toLowerCase().contains("stone")) {
                    temp.add((Item) value);
                }
            } catch (IllegalAccessException e) {
                LOGGER.error("Failed to access BTWItems field: {}", field.getName(), e);
            }
        }
        STONE_ITEMS = Set.copyOf(temp);
    }
}
