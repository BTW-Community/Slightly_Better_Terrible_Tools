package btw.community.abbyread.sbtt.mixin.speed;

import btw.item.items.ToolItem;
import net.minecraft.src.Block;
import net.minecraft.src.BlockClay;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ToolItem.class)
public class ChiselItemWood_ToolItemMixin {

    // Since efficiency on is determined by ability to convert, it ended up being
    // as fast as loosening dirt blocks, which was not intended.  Felt cheaty.
    @Inject(method = "getStrVsBlock", at = @At("RETURN"), cancellable = true)
    private void nerfClayHarvestEfficiency(ItemStack stack, World world, Block block, int x, int y, int z, CallbackInfoReturnable<Float> cir) {
        if (block instanceof BlockClay) {
            float value = cir.getReturnValue();
            cir.setReturnValue(value * 0.75F);
        }
    }

}
