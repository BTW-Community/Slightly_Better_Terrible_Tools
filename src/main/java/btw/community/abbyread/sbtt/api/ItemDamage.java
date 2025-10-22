package btw.community.abbyread.sbtt.api;

import net.minecraft.src.*;

/**
 * Handles logic for damaging tools based on use.
 * Integrates with SBTTPlayerExtension to respect variable damage
 * from custom interactions.
 */
public class ItemDamage {
    public static int amount = 1; // default amount if damaging item

    /**
     * Checks if it's valid to damage the item, applies amount if so,
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

        // --- New section: handle special interaction-driven damage ---
        SBTTPlayerExtension ext = (SBTTPlayerExtension) player;
        boolean conversionByTool = ext.sbtt_consumeItemUsedFlag();
        int extraDamage = ext.sbtt_consumeItemUsedDamage();

        // Check if there's a special interaction defined for this combo
        int blockMeta = world.getBlockMetadata(x, y, z);
        boolean hasSpecialInteraction = InteractionHandler.canInteract(
                stack, block, blockMeta, InteractionHandler.InteractionType.PRIMARY_LEFT_CLICK
        );

        boolean betterThanNothing = conversionByTool || hasSpecialInteraction;

        if (betterThanNothing) {
            // Use custom damage if available; otherwise default
            int appliedDamage = damageByAmount(stack, player, extraDamage > 0 ? extraDamage : amount);
            amount = 1; // reset to default for next call
            return appliedDamage;
        }

        return 0; // no damage applied
    }

    public static int damageByAmount(ItemStack stack, EntityPlayer player, int amountOfDamage) {
        player.addStat(StatList.objectUseStats[stack.itemID], 1);
        stack.damageItem(amountOfDamage, player);
        return amountOfDamage;
    }
}
