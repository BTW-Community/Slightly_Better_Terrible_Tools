package net.fabricmc.abbyread.mixin;

import btw.item.items.PickaxeItem;
import btw.item.items.ToolItem;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PickaxeItem.class)
public class PickaxeItemMixin {
    @Inject(method = "isEfficientVsBlock", at = @At("RETURN"), remap = false)
    public void abby$conditionalEfficiency(ItemStack stack, World world, Block block, int i, int j, int k, CallbackInfoReturnable<Boolean> cir){
        String itemName = stack.getDisplayName();
        String blockName = block.getLocalizedName();
        int abby$iToolLevel = ((ToolItem)stack.getItem()).toolMaterial.getHarvestLevel();
        int abby$iBlockToolLevel = block.getEfficientToolLevel(world, i, j, k);
        System.out.println(itemName + "'s iToolLevel: " + abby$iToolLevel +
                " vs. " + blockName + "'s iBlockToolLevel: " + abby$iBlockToolLevel);
    }
}
