package btw.community.abbyread.sbtt.mixin.speed;

import btw.community.abbyread.categories.BlockType;
import btw.community.abbyread.categories.ThisBlock;
import btw.item.items.ChiselItem;
import btw.item.items.ChiselItemStone;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChiselItem.class)
public class ChiselItemStone_ChiselItemMixin {

    @Inject(method = "isEfficientVsBlock", at = @At("HEAD"), cancellable = true)
    private void addEfficienciesToSharpStone(ItemStack stack, World world, Block block, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        // Only handle sharp stones in this inject
        if (!(stack.getItem() instanceof ChiselItemStone)) return;

        int metadata = world.getBlockMetadata(x, y, z);

        if (ThisBlock.is(BlockType.GRASS, block, metadata)) {
            cir.setReturnValue(true);
        }

    }

}
