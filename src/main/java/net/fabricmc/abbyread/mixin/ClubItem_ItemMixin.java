package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.categories.ItemTag;
import btw.community.abbyread.categories.ItemTags;
import btw.community.abbyread.sbtt.Convert;
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
        if (Convert.canConvert(stack, block, meta)) {
            float normal = cir.getReturnValue();
            cir.setReturnValue(normal * 0.5F);
        }
    }
}
