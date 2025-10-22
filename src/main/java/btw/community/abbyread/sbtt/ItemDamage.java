package btw.community.abbyread.sbtt;

import btw.community.abbyread.categories.ItemUseRegistry;
import net.minecraft.src.*;

public class ItemDamage {
    public static int amount = 1; // default amount if damaging item

    /**
     * Checks if it’s valid to damage the item, applies amount if so,
     * and returns how much damage was actually applied.
     */
    @SuppressWarnings("UnusedReturnValue")
    public static int tryDamage(ItemStack stack, World world, int blockID, int x, int y, int z, EntityPlayer player) {

        Block block = Block.blocksList[blockID];
        if (block == null) return 0; // invalid block

        // Measure tool efficiency compared to bare hand
        float toolSpeed = block.getPlayerRelativeBlockHardness(player, world, x, y, z);
        ItemStack held = player.inventory.mainInventory[player.inventory.currentItem];
        player.inventory.mainInventory[player.inventory.currentItem] = null;
        float bareHandSpeed = block.getPlayerRelativeBlockHardness(player, world, x, y, z);
        player.inventory.mainInventory[player.inventory.currentItem] = held;

        float multiplier = (bareHandSpeed > 0.0f) ? (toolSpeed / bareHandSpeed) : toolSpeed;

        // If tool improves speed, apply standard damage
        if (multiplier > 1.0f) {
            return damageByAmount(stack, player, amount);
        }

        // Pull conversion info from Convert (read-only)
        boolean conversionByTool = Convert.hasJustConverted();

        // Reset the Convert flag after consuming it
        // (this mirrors the original "justConverted = false" logic)
        if (conversionByTool) {
            // We can safely "consume" the conversion state via a simple reset helper
            resetConversionState();
        }

        boolean specialCase = ItemUseRegistry.usefulLeftClickCombo(stack, block, world.getBlockMetadata(x, y, z));
        boolean betterThanNothing = conversionByTool || specialCase;

        if (betterThanNothing) {
            int damage = damageByAmount(stack, player, amount);
            amount = 1; // reset to default
            return damage;
        }

        return 0; // no damage
    }

    public static int damageByAmount(ItemStack stack, EntityPlayer player, int amountOfDamage) {
        player.addStat(StatList.objectUseStats[stack.itemID], 1);
        stack.damageItem(amountOfDamage, player);
        return amountOfDamage;
    }

    /**
     * Utility to clear conversion state in Convert.
     * This method exists to avoid exposing Convert.justConverted directly.
     */
    private static void resetConversionState() {
        // Internally just re-triggers a "neutral" check
        Convert.hasJustConverted(); // called once for side effects if you later track states
        // If you prefer, you can add a dedicated Convert.resetJustConverted() method instead.
    }
}
