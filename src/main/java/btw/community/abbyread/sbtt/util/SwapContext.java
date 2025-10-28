package btw.community.abbyread.sbtt.util;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class SwapContext {
    public final ItemStack stack;
    public final EntityPlayer player;
    public final World world;
    public final int x, y, z;

    public SwapContext(ItemStack stack, EntityPlayer player, World world, int x, int y, int z) {
        this.stack = stack;
        this.player = player;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
