package btw.community.abbyread.sbtt.mixin;

import btw.community.abbyread.sbtt.Efficiency;
import btw.item.items.ChiselItemWood;
import btw.item.items.ToolItem;
import net.minecraft.src.Block;
import net.minecraft.src.BlockStone;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ToolItem.class)
public class ChiselItemWood_ToolItemMixin {
    @Inject(method = "getStrVsBlock", at = @At("RETURN"), cancellable = true)
    private void abbyread$getStrVsBlock(ItemStack stack, World world, Block block, int x, int y, int z, CallbackInfoReturnable<Float> cir) {
        if (stack.getItem() instanceof ChiselItemWood) {
            float strength = cir.getReturnValue();
            if (block instanceof BlockStone) cir.setReturnValue(strength * Efficiency.modifier);
        }
    }
}
