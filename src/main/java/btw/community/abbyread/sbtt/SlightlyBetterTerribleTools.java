package btw.community.abbyread.sbtt;

import btw.AddonHandler;
import btw.BTWAddon;
import btw.block.BTWBlocks;
import btw.community.abbyread.sbtt.util.Globals;
import btw.crafting.recipe.RecipeManager;
import btw.item.BTWItems;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class SlightlyBetterTerribleTools extends BTWAddon {
    private final float effMod = Globals.modifier;
    private final String percentage = String.format("%.0f%%", effMod * 100 - 100);
    public SlightlyBetterTerribleTools() {
        super();
    }

    @Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " with " + percentage + " Boost Initializing...");

        // Loose Dirt + 2 slime balls -> 1 Dirt
        RecipeManager.addShapelessRecipe(
                new ItemStack(Block.dirt, 1),
                new Object[]{
                        new ItemStack(BTWBlocks.looseDirt),
                        new ItemStack(Item.slimeBall),
                        new ItemStack(Item.slimeBall)
                }
        );

        // Loose Dirt Slabs x2 + 2 slime balls -> 1 Dirt
        RecipeManager.addShapelessRecipe(
                new ItemStack(Block.dirt, 1),
                new Object[]{
                        new ItemStack(BTWBlocks.looseDirtSlab),
                        new ItemStack(BTWBlocks.looseDirtSlab),
                        new ItemStack(Item.slimeBall),
                        new ItemStack(Item.slimeBall)
                }
        );

        // Dirt Piles x8 + 1 slime ball -> 1 Dirt (high efficiency recipe)
        RecipeManager.addShapelessRecipe(
                new ItemStack(Block.dirt, 1),
                new Object[]{
                        new ItemStack(BTWItems.dirtPile),
                        new ItemStack(BTWItems.dirtPile),
                        new ItemStack(BTWItems.dirtPile),
                        new ItemStack(BTWItems.dirtPile),
                        new ItemStack(BTWItems.dirtPile),
                        new ItemStack(BTWItems.dirtPile),
                        new ItemStack(BTWItems.dirtPile),
                        new ItemStack(BTWItems.dirtPile),
                        new ItemStack(Item.slimeBall)
                }
        );
    }

    @Override
    public String getModID() {
        return "sbtt"; // or whatever ID you want for your addon
    }
}