package btw.community.abbyread.sbtt.mixin;

import btw.community.abbyread.categories.ItemTag;
import btw.community.abbyread.categories.ItemTags;
import btw.community.abbyread.sbtt.api.InteractionHandler;
import btw.community.abbyread.sbtt.api.InteractionHandler.InteractionType;
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

        if (ItemTags.isNot(stack, ItemTag.CLUB)) return;

        int meta = world.getBlockMetadata(x, y, z);
        if (InteractionHandler.canInteract(stack, block, meta, InteractionType.PRIMARY_LEFT_CLICK)) {
            float normal = cir.getReturnValue();
            cir.setReturnValue(normal * 0.5F);
        }
    }
}