package btw.community.abbyread;

import net.minecraft.src.ItemStack;

public class ToolState {
    private static ItemStack currentTool;

    public static void setCurrentTool(ItemStack stack) {
        currentTool = stack;
    }

    public static ItemStack getCurrentTool() {
        return currentTool;
    }
}
