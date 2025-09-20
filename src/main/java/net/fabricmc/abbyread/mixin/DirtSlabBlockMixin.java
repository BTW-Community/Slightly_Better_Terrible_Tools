package net.fabricmc.abbyread.mixin;

import btw.block.blocks.DirtSlabBlock;
import btw.community.abbyread.ToolState;
import btw.item.BTWItems;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DirtSlabBlock.class)
public class DirtSlabBlockMixin {
    @Inject(
            method = "onBlockDestroyedWithImproperTool",
            at = @At("HEAD"),
            cancellable = true
    )
    private void abby$preventDropsFromLoosening(
            World world, EntityPlayer player, int x, int y, int z,
            int metadata, CallbackInfo ci
    ){
        ItemStack tool = ToolState.getCurrentTool();
        if (tool != null &&
                tool.getItem().itemID == BTWItems.pointyStick.itemID
        ) { ci.cancel(); }
    }
}
