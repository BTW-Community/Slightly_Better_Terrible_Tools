package btw.community.abbyread.sbtt.helper;

import net.minecraft.src.*;
import btw.community.abbyread.sbtt.helper.InteractionHandler.InteractionType;

/**
 * Handles logic for damaging tools based on use.
 * Integrates with SBTTPlayerExtension and InteractionHandler to respect
 * variable damage from custom block interactions.
 */
public class ItemDamage {

    /**
     * Default fallback damage amount if none was set by an interaction.
     */
    public static int amount = 1;

    /**
     * Tries to damage the item in the player’s hand based on what happened in the world.
     * Uses InteractionHandler and SBTTPlayerExtension to determine how much to apply.
     *
     * @return how much damage was actually applied
     */
    @SuppressWarnings("UnusedReturnValue")
    public static int tryDamage(ItemStack stack, World world, int blockID,
                                int x, int y, int z, EntityPlayer player) {

        if (stack == null || player == null) return 0;
        Block block = Block.blocksList[blockID];
        if (block == null) return 0;

        // --- STEP 1: Check for custom SBTT interaction state ---
        SBTTPlayerExtension ext = (SBTTPlayerExtension) player;
        boolean usedByInteraction = ext.sbtt_consumeItemUsedFlag();
        int customDamage = ext.sbtt_consumeItemUsedDamage();

        if (usedByInteraction) {
            // A registered SBTT interaction occurred (InteractionHandler already flagged it)
            int appliedDamage = damageByAmount(stack, player,
                    customDamage > 0 ? customDamage : amount);
            amount = 1; // reset global static
            return appliedDamage;
        }

        // --- STEP 2: Fallback to dynamic detection if not explicitly flagged ---
        int meta = world.getBlockMetadata(x, y, z);

        boolean hasPrimaryInteraction = InteractionHandler.canInteract(
                stack, block, meta, InteractionType.PRIMARY_LEFT_CLICK
        );
        boolean hasSecondaryInteraction = InteractionHandler.canInteract(
                stack, block, meta, InteractionType.SECONDARY_RIGHT_CLICK
        );

        if (hasPrimaryInteraction || hasSecondaryInteraction) {
            // There's a valid custom conversion path even if it didn't get flagged yet
            int appliedDamage = damageByAmount(stack, player, amount);
            amount = 1;
            return appliedDamage;
        }

        // --- STEP 3: Tool efficiency fallback (vanilla-style behavior) ---
        float toolSpeed = block.getPlayerRelativeBlockHardness(player, world, x, y, z);
        ItemStack held = player.inventory.mainInventory[player.inventory.currentItem];
        player.inventory.mainInventory[player.inventory.currentItem] = null;
        float bareHandSpeed = block.getPlayerRelativeBlockHardness(player, world, x, y, z);
        player.inventory.mainInventory[player.inventory.currentItem] = held;

        float multiplier = (bareHandSpeed > 0.0f) ? (toolSpeed / bareHandSpeed) : toolSpeed;

        if (multiplier > 1.0f) {
            return damageByAmount(stack, player, amount);
        }

        return 0;
    }

    /**
     * Damages the given stack by a specified amount and triggers vanilla stat tracking.
     */
    public static int damageByAmount(ItemStack stack, EntityPlayer player, int amountOfDamage) {
        if (stack == null || player == null) return 0;
        player.addStat(StatList.objectUseStats[stack.itemID], 1);
        stack.damageItem(amountOfDamage, player);
        return amountOfDamage;
    }
}
