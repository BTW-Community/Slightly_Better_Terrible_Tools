package net.fabricmc.abbyread.mixin;

import btw.community.abbyread.categories.BlockTag;
import btw.community.abbyread.categories.BlockTags;
import btw.community.abbyread.sbtt.Efficiency;
import btw.item.items.ChiselItemStone;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChiselItemStone.class)
public class ChiselItemStoneMixin {
    @Inject(method = "getStrVsBlock", at = @At("RETURN"), cancellable = true)
    private void abbyread$getStrVsBlock(ItemStack stack, World world, Block block, int x, int y, int z, CallbackInfoReturnable<Float> cir) {
        int meta = world.getBlockMetadata(x, y, z);
        if (BlockTags.is(block, meta, BlockTag.GRASS)) {
            float strength = cir.getReturnValue();
            cir.setReturnValue(strength * Efficiency.modifier);
        }
        if (BlockTags.is(block, meta, BlockTag.WEB)) {
            float strength = cir.getReturnValue();
            cir.setReturnValue(strength * Efficiency.modifier);
        }
    }
}
