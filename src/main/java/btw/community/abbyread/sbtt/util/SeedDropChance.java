package btw.community.abbyread.sbtt.util;

import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import net.minecraft.src.ItemStack;

public class SeedDropChance {

    private static final boolean DEBUG = false;

    // Chance parameters
    private static final int MAX_CHANCE_DENOMINATOR = 200;
    private static final int MIN_CHANCE_DENOMINATOR = 2;

    // --- Basic accessors ---

    public static int get(EntityPlayer player) {
        PlayerDataExtension data = (PlayerDataExtension) player;
        return data.getSeedAttempts();
    }

    public static void set(EntityPlayer player, int attempts) {
        PlayerDataExtension data = (PlayerDataExtension) player;
        data.setSeedAttempts(attempts);
    }

    public static void increment(EntityPlayer player) {
        set(player, get(player) + 1);
    }

    public static void reset(EntityPlayer player) {
        set(player, 0);
    }

    // --- Seed roll helper ---

    /**
     * Rolls for a seed drop for the given player.
     * Updates attempt count automatically.
     * @param player The player harvesting the block
     * @param world  The world the block is in (for RNG)
     * @return true if a seed should drop, false otherwise
     */
    public static boolean rollSeed(EntityPlayer player, World world) {
        int attemptsMadeAlready = get(player); // now via interface
        int chance = Math.max(MAX_CHANCE_DENOMINATOR - attemptsMadeAlready, MIN_CHANCE_DENOMINATOR);
        int roll = world.rand.nextInt(chance);
        boolean success = (roll == 0);

        if (success) reset(player);
        else increment(player);

        if (DEBUG) {
            System.out.println("[SEED DROP DEBUG] Player: " + player.username +
                    " | Attempt: " + (attemptsMadeAlready + 1) +
                    " | Chance: 1/" + chance +
                    " | Roll: " + roll +
                    " | Success: " + success);
        }

        return success;
    }

    /**
     * Convenience method to handle a seed drop if roll succeeds.
     * @param player The player harvesting
     * @param world  The world
     * @param x      Block x
     * @param y      Block y
     * @param z      Block z
     * @param side   Facing to eject seeds toward
     */
    public static void maybeDropSeed(EntityPlayer player, World world, int x, int y, int z, int side) {
        if (rollSeed(player, world)) {
            ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, new ItemStack(BTWItems.hempSeeds), side);
            if (DEBUG) {
                System.out.println("[SEED DROP DEBUG] SEED DROPPED!");
            }
        }
    }
}
