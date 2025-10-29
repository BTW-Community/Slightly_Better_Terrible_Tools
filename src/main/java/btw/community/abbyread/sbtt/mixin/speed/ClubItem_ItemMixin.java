package btw.community.abbyread.sbtt.mixin.speed;

import btw.community.abbyread.categories.ThisBlock;
import btw.community.abbyread.categories.BlockType;
import btw.community.abbyread.categories.ItemType;
import btw.community.abbyread.categories.ThisItem;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ClubItem_ItemMixin {
    @Inject(method = "getStrVsBlock", at = @At("RETURN"), cancellable = true)
    private void abbyread$getStrVsBlock(ItemStack stack, World world, Block block, int x, int y, int z, CallbackInfoReturnable<Float> cir) {

        if (ThisItem.isNot(ItemType.CLUB, stack)) return;

        int meta = world.getBlockMetadata(x, y, z);

        // Negate speed boost toward loose dirtlikes when firming with club
        if (ThisBlock.is(BlockType.LOOSE_DIRTLIKE, block, meta)) {
            float normal = cir.getReturnValue();
            cir.setReturnValue(normal * 0.5F);
        }
    }
}
