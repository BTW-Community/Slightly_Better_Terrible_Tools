package btw.community.abbyread.rules;

import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;

public class RuleContext {
    ItemStack tool;
    Block block;
    int meta;
    int x, y, z;
    int sideClicked;
    float playerYaw;
    float playerPitch;
    // other info like world, player, etc.
}