package btw.community.abbyread.categories;

import btw.item.BTWItems;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

import java.util.*;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class ThisItem {

    // ===== Material sets =====
    // Some of these will list invalid items like wooden shovels, but doesn't matter.

    private static final Set<Item> WOOD_ITEMS = Set.of(
            Item.swordWood,
            Item.shovelWood,
            Item.pickaxeWood,
            Item.axeWood,
            Item.hoeWood,
            BTWItems.pointyStick,
            BTWItems.woodenBlade,
            BTWItems.woodenClub
    );

    private static final Set<Item> STONE_ITEMS = Set.of(
            Item.swordStone,
            Item.shovelStone,
            Item.pickaxeStone,
            Item.axeStone,
            Item.hoeStone,
            BTWItems.sharpStone
    );

    private static final Set<Item> IRON_ITEMS = Set.of(
            Item.swordIron,
            Item.shovelIron,
            Item.pickaxeIron,
            Item.axeIron,
            Item.hoeIron,
            Item.shears,
            BTWItems.ironChisel
    );

    private static final Set<Item> DIAMOND_ITEMS = Set.of(
            Item.swordDiamond,
            Item.shovelDiamond,
            Item.pickaxeDiamond,
            Item.axeDiamond,
            Item.hoeDiamond,
            BTWItems.diamondChisel
    );

    private static final Set<Item> GOLD_ITEMS = Set.of(
            Item.swordGold,
            Item.shovelGold,
            Item.pickaxeGold,
            Item.axeGold,
            Item.hoeGold
    );

    private static final Set<Item> STEEL_ITEMS = Set.of(
            // Tools and weapons
            BTWItems.battleaxe,
            BTWItems.mattock,
            BTWItems.steelAxe,
            BTWItems.steelHoe,
            BTWItems.steelPickaxe,
            BTWItems.steelShovel,
            BTWItems.steelSword,

            // Misc.
            BTWItems.steelArmorPlate,
            BTWItems.steelNugget,
            BTWItems.soulforgedSteelIngot
    );

    private static final Set<Item> BONE_ITEMS = Set.of(
            Item.bone,
            BTWItems.boneClub,
            BTWItems.boneCarving,
            BTWItems.boneFishHook
    );


    // ===== Tool sets =====

    private static final Set<Item> CHISELS = Set.of(
            BTWItems.pointyStick,   // wood chisel
            BTWItems.sharpStone,     // stone chisel
            BTWItems.ironChisel,
            BTWItems.diamondChisel
    );

    private static final Set<Item> SHOVELS = Set.of(
            Item.shovelWood,
            Item.shovelStone,
            Item.shovelIron,
            Item.shovelDiamond,
            Item.shovelGold,
            BTWItems.steelShovel
    );

    private static final Set<Item> PICKAXES = Set.of(
            Item.pickaxeWood,
            Item.pickaxeStone,
            Item.pickaxeIron,
            Item.pickaxeDiamond,
            Item.pickaxeGold,
            BTWItems.steelPickaxe
    );

    private static final Set<Item> AXES = Set.of(
            Item.axeWood,
            Item.axeStone,
            Item.axeIron,
            Item.axeDiamond,
            Item.axeGold,
            BTWItems.steelAxe,
            BTWItems.battleaxe
    );

    private static final Set<Item> HOES = Set.of(
            Item.hoeWood,
            Item.hoeStone,
            Item.hoeIron,
            Item.hoeDiamond,
            Item.hoeGold,
            BTWItems.steelHoe
    );

    private static final Set<Item> SHEARS = Set.of(
            Item.shears,
            BTWItems.diamondShears
    );

    private static final Set<Item> BATTLEAXES = Set.of(
            BTWItems.battleaxe
    );


    // ===== Weapon sets =====

    private static final Set<Item> SWORDS = Set.of(
            Item.swordWood,
            Item.swordStone,
            Item.swordIron,
            Item.swordDiamond,
            Item.swordGold,
            BTWItems.steelSword
    );

    private static final Set<Item> CLUBS = Set.of(
            BTWItems.woodenClub,
            BTWItems.boneClub
    );

    private static final Set<Item> BOWS = Set.of(
            Item.bow,
            BTWItems.compositeBow
    );

    private static final Set<Item> AMMO = Set.of(
            Item.arrow,
            BTWItems.rottenArrow,
            BTWItems.broadheadArrow
    );

    // TODO: Add armor set


    // ===== Consumables / misc =====

    private static final Set<Item> FOODS = Set.of(
            Item.appleRed,
            Item.bread,
            Item.porkCooked,
            Item.porkRaw
            // expand with all food items
    );

    private static final Set<Item> FUELS = Set.of(
            Item.coal,
            Item.stick,
            Item.blazeRod
    );

    // ===== Composite sets =====

    @SuppressWarnings("unused")
    private static final Set<Item> TOOL_ITEMS;

    @SuppressWarnings("unused")
    private static final Set<Item> WEAPON_ITEMS;

    static {
        Set<Item> tempTools = new HashSet<>();
        tempTools.addAll(CHISELS);
        tempTools.addAll(SHOVELS);
        tempTools.addAll(PICKAXES);
        tempTools.addAll(AXES);
        tempTools.addAll(HOES);
        tempTools.addAll(SHEARS);
        tempTools.addAll(BATTLEAXES);
        TOOL_ITEMS = Set.copyOf(tempTools);

        Set<Item> tempWeapons = new HashSet<>();
        tempWeapons.addAll(SWORDS);
        tempWeapons.addAll(CLUBS);
        tempWeapons.addAll(BOWS);
        tempWeapons.addAll(AMMO);
        WEAPON_ITEMS = Set.copyOf(tempWeapons);
    }

    // ===== Tag resolution =====

    public static Set<ItemType> of(Item item) {
        Set<ItemType> tags = new HashSet<>();

        // --- Materials ---
        if (WOOD_ITEMS.contains(item)) tags.add(ItemType.WOOD);
        if (STONE_ITEMS.contains(item)) tags.add(ItemType.STONE);
        if (IRON_ITEMS.contains(item)) tags.add(ItemType.IRON);
        if (DIAMOND_ITEMS.contains(item)) tags.add(ItemType.DIAMOND);
        if (GOLD_ITEMS.contains(item)) tags.add(ItemType.GOLD);
        if (STEEL_ITEMS.contains(item)) tags.add(ItemType.STEEL);
        if (BONE_ITEMS.contains(item)) tags.add(ItemType.BONE);

        // --- Tools ---
        if (CHISELS.contains(item)) { tags.add(ItemType.TOOL); tags.add(ItemType.CHISEL); }
        if (SHOVELS.contains(item)) { tags.add(ItemType.TOOL); tags.add(ItemType.SHOVEL); }
        if (PICKAXES.contains(item)) { tags.add(ItemType.TOOL); tags.add(ItemType.PICKAXE); }
        if (AXES.contains(item)) { tags.add(ItemType.TOOL); tags.add(ItemType.AXE); }
        if (HOES.contains(item)) { tags.add(ItemType.TOOL); tags.add(ItemType.HOE); }
        if (SHEARS.contains(item)) { tags.add(ItemType.TOOL); tags.add(ItemType.SHEARS); }
        if (BATTLEAXES.contains(item)) { tags.add(ItemType.TOOL); tags.add(ItemType.BATTLEAXE); tags.add(ItemType.WEAPON); }

        // --- Weapons ---
        if (SWORDS.contains(item)) { tags.add(ItemType.WEAPON); tags.add(ItemType.SWORD); }
        if (CLUBS.contains(item)) { tags.add(ItemType.WEAPON); tags.add(ItemType.CLUB); }
        if (BOWS.contains(item)) { tags.add(ItemType.WEAPON); tags.add(ItemType.BOW); }
        if (AMMO.contains(item)) { tags.add(ItemType.AMMO); }

        // --- Misc ---
        if (FOODS.contains(item)) tags.add(ItemType.FOOD);
        if (FUELS.contains(item)) tags.add(ItemType.FUEL);

        return tags;
    }

    // ===== Public API =====

    public static Set<ItemType> getTags(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return Collections.emptySet();
        return of(stack.getItem());
    }

    public static boolean is(ItemType tag, ItemStack stack) {
        return getTags(stack).contains(tag);
    }

    public static boolean isAnd(ItemType tag1, ItemType tag2, ItemStack stack) {
        return getTags(stack).contains(tag1) && getTags(stack).contains(tag2);
    }

    public static boolean isNot(ItemType tag, ItemStack stack) {
        return !is(tag, stack);
    }

    public static boolean isAny(ItemStack stack, ItemType... tags) {
        Set<ItemType> stackTags = getTags(stack);
        for (ItemType tag : tags) {
            if (stackTags.contains(tag)) return true;
        }
        return false;
    }

    public static boolean isAll(ItemStack stack, ItemType... tags) {
        Set<ItemType> stackTags = getTags(stack);
        for (ItemType tag : tags) {
            if (!stackTags.contains(tag)) return false;
        }
        return true;
    }

    @SuppressWarnings("unused")
    public static boolean isButNot(ItemStack stack, ItemType isTag, ItemType... notTags) {
        return is(isTag, stack) && !isAny(stack, notTags);
    }
}
