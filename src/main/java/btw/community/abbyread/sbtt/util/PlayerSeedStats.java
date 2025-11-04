package btw.community.abbyread.sbtt.util;

import btw.community.abbyread.sbtt.mixin.access.EntityPlayerAccessor;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NBTTagCompound;

public class PlayerSeedStats {

    private static final String KEY = "SeedAttempts";

    /**
     * Gets the current seed attempt count for a player.
     * Returns 0 if the player has no previous attempts stored.
     */
    public static int get(EntityPlayer player) {
        // Create a temporary tag to read the player's mod data into
        NBTTagCompound tag = new NBTTagCompound();
        ((EntityPlayerAccessor) player).invokeReadModDataFromNBT(tag);

        // Return the stored attempts, or 0 if not present
        return tag.hasKey(KEY) ? tag.getInteger(KEY) : 0;
    }

    /**
     * Sets the seed attempt count for a player.
     * This writes back to the player's mod data NBT safely.
     */
    public static void set(EntityPlayer player, int attempts) {
        // Read the current mod data into a temporary tag
        NBTTagCompound tag = new NBTTagCompound();
        ((EntityPlayerAccessor) player).invokeReadModDataFromNBT(tag);

        // Update the attempts count
        tag.setInteger(KEY, attempts);

        // Write the updated tag back to the player
        ((EntityPlayerAccessor) player).invokeWriteModDataToNBT(tag);
    }
}
