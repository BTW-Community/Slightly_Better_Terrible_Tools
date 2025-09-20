package net.fabricmc.abbyread.mixin;

import btw.block.BTWBlocks;
import btw.community.abbyread.LooseningHelper;
import btw.community.abbyread.ToolState;
import btw.item.BTWItems;
import net.minecraft.src.BlockGrass;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockGrass.class)
public abstract class BlockGrassMixin {
    @Inject(
        method = "onNeighborDirtDugWithImproperTool",
        at = @At("HEAD"),
        cancellable = true
    )
    private void abby$reimplementGrassDrop(World world, int x, int y, int z, int toFacing, CallbackInfo ci) {
        if (toFacing == 0) {
            world.setBlockWithNotify(x, y, z, BTWBlocks.looseDirt.blockID);
        }
        ci.cancel();
    }
    @Inject( method = "onBlockDestroyedWithImproperTool", at = @At("HEAD"), cancellable = true ) private void abby$preventDropsFromLoosening(World world, EntityPlayer player, int x, int y, int z, int metadata, CallbackInfo ci ){
        ItemStack tool = ToolState.getCurrentTool();
        if (tool != null && tool.getItem().itemID == BTWItems.pointyStick.itemID ) {
            LooseningHelper.tryLoosenBlock(tool, world, x, y, z);
            ci.cancel();
        }
    }
}