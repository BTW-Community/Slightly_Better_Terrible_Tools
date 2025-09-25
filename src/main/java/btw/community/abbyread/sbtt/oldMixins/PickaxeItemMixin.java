package btw.community.abbyread.sbtt.oldMixins;

import btw.community.abbyread.EfficiencyHelper;
import btw.item.items.PickaxeItem;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PickaxeItem.class)
public class PickaxeItemMixin {

    // Ensure damage is calculated correctly
    @Inject(method = "isToolTypeEfficientVsBlockType", at = @At("RETURN"), remap = false)
    private void abbyread$tellHelper(Block block, CallbackInfoReturnable<Boolean> cir) {
        EfficiencyHelper.setLastEffective(cir.getReturnValue());
    }
    @Inject(method = "canHarvestBlock", at = @At("RETURN"), remap = false)
    private void abbyread$tellHelper2(ItemStack stack, World world, Block block, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem().itemID == PickaxeItem.pickaxeStone.itemID ||
            stack.getItem().itemID == PickaxeItem.pickaxeGold.itemID) {
            EfficiencyHelper.setLastEffective(true);
        } else {
            EfficiencyHelper.setLastEffective(cir.getReturnValue());
        }
    }
}
