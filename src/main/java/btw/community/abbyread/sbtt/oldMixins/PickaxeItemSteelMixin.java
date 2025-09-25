package btw.community.abbyread.sbtt.oldMixins;

import btw.community.abbyread.EfficiencyHelper;
import btw.item.items.PickaxeItemSteel;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PickaxeItemSteel.class)
public class PickaxeItemSteelMixin {

    @Inject(method = "canHarvestBlock", at = @At("RETURN"), remap = false)
    private void abbyread$tellHelper2(ItemStack stack, World world, Block block, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        EfficiencyHelper.setLastEffective(cir.getReturnValue());
    }
}
