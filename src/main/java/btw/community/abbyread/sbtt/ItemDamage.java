package btw.community.abbyread.sbtt;

import btw.community.abbyread.categories.ItemUseRegistry;
import net.minecraft.src.*;

public class ItemDamage {
    public static int amount = 1; // default amount if damaging item

    // Check if valid to damage, apply amount if so, and return applied amount
    @SuppressWarnings("UnusedReturnValue")
    public static int tryDamage(ItemStack stack, World world, int blockID, int x, int y, int z, EntityPlayer player) {

        Block block = Block.blocksList[blockID];
        if (block == null) return 0; // no valid block found from blockID

        // Get efficiency end-point with held item, with nothing, and compare the two
        float toolSpeed = block.getPlayerRelativeBlockHardness(player, world, x, y, z);
        ItemStack held = player.inventory.mainInventory[player.inventory.currentItem];
        player.inventory.mainInventory[player.inventory.currentItem] = null;
        float bareHandSpeed = block.getPlayerRelativeBlockHardness(player, world, x, y, z);
        player.inventory.mainInventory[player.inventory.currentItem] = held;
        float multiplier = (bareHandSpeed > 0.0f) ? (toolSpeed / bareHandSpeed) : toolSpeed;

        if (multiplier > 1.0f) return damageByAmount(stack, player, amount);

        // Transfer state from Convert globals
        boolean conversionByTool = Convert.justConverted;
        Convert.justConverted = false;

        boolean specialCase = ItemUseRegistry.usefulLeftClickCombo(stack, block, world.getBlockMetadata(x, y, z));
        boolean betterThanNothing = conversionByTool || specialCase;

        if (betterThanNothing) {
            int damage = damageByAmount(stack, player, amount);
            amount = 1; // reset default damage amount to 1
            return damage; // return amount of damage applied to item
        }

        // no damage is the default
        return 0;
    }

    public static int damageByAmount(ItemStack stack, EntityPlayer player, int amountOfDamage) {
        player.addStat(StatList.objectUseStats[stack.itemID], 1);
        stack.damageItem(amountOfDamage, player);
        return amountOfDamage;
    }
}
