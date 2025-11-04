package btw.community.abbyread.sbtt.util;

import net.minecraft.src.EntityPlayer;

/**
 * Utility wrapper around the BTW data system for seed attempt tracking.
 */
public class PlayerSeedStats {

    public static int get(EntityPlayer player) {
        return player.getData(ModPlayerDataEntries.SEED_ATTEMPTS);
    }

    public static void set(EntityPlayer player, int attempts) {
        player.setData(ModPlayerDataEntries.SEED_ATTEMPTS, attempts);
    }

    public static void increment(EntityPlayer player) {
        set(player, get(player) + 1);
    }

    public static void reset(EntityPlayer player) {
        set(player, 0);
    }
}
