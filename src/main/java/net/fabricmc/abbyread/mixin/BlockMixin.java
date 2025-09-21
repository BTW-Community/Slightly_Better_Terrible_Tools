package net.fabricmc.abbyread.mixin;

import btw.block.BTWBlocks;
import btw.block.blocks.DirtSlabBlock;
import btw.block.blocks.GrassSlabBlock;
import btw.client.fx.BTWEffectManager;
import btw.item.items.ChiselItemStone;
import btw.item.items.ChiselItemWood;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockMixin {
    @Shadow @Final public int blockID;

    @Inject(
            method = "canConvertBlock", at = @At("HEAD"), cancellable = true
    )
    private void abbyread$canConvertBlock(ItemStack stack, World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir){
        if (stack.getItem() instanceof ChiselItemWood  && this.blockID == BTWBlocks.dirtSlab.blockID) {
            @SuppressWarnings("ConstantConditions")
            DirtSlabBlock slab = (DirtSlabBlock) (Object) this;
            int subtype = slab.getSubtype(world, x, y, z);
            if (subtype == DirtSlabBlock.SUBTYPE_DIRT) {
                cir.setReturnValue(true);
            }
        }
        if (stack.getItem() instanceof ChiselItemStone && this.blockID == BTWBlocks.grassSlab.blockID) {
            @SuppressWarnings("ConstantConditions")
            GrassSlabBlock slab = (GrassSlabBlock) (Object) this;
            boolean sparse = slab.isSparse(world, x, y, z);
            if (!sparse) cir.setReturnValue(true);
        }
        if (stack.getItem() instanceof ChiselItemWood && this.blockID == BTWBlocks.grassSlab.blockID) {
            @SuppressWarnings("ConstantConditions")
            GrassSlabBlock slab = (GrassSlabBlock) (Object) this;
            boolean sparse = slab.isSparse(world, x, y, z);
            if (sparse) cir.setReturnValue(true);
        }
    }

    @Inject(
            method = "convertBlock", at = @At("HEAD"), cancellable = true
    )
    private void abbyread$convertBlock(ItemStack stack, World world, int x, int y, int z, int fromSide, CallbackInfoReturnable<Boolean> cir){
        if ((stack.getItem() instanceof ChiselItemWood)
                && this.blockID == BTWBlocks.dirtSlab.blockID) {
            @SuppressWarnings("ConstantConditions")
            DirtSlabBlock slab = (DirtSlabBlock) (Object) this;
            int subtype = slab.getSubtype(world, x, y, z);
            if (subtype == DirtSlabBlock.SUBTYPE_DIRT) {
                world.setBlockWithNotify(x, y, z, BTWBlocks.looseDirtSlab.blockID);
                if (!world.isRemote) {
                    world.playAuxSFX(BTWEffectManager.DIRT_TILLING_EFFECT_ID, x, y, z, 0);
                }
                cir.setReturnValue(true);
            }
        }
        if (stack.getItem() instanceof ChiselItemStone &&
                this.blockID == BTWBlocks.grassSlab.blockID) {
            @SuppressWarnings("ConstantConditions")
            GrassSlabBlock slab = (GrassSlabBlock) (Object) this;
            if (!slab.isSparse(world, x, y, z)){
                slab.setSparse(world, x, y, z);
                if (!world.isRemote) {
                    world.playAuxSFX(BTWEffectManager.DIRT_TILLING_EFFECT_ID, x, y, z, 0);
                }
                cir.setReturnValue(true);
            }
        }
        if (stack.getItem() instanceof ChiselItemWood &&
                this.blockID == BTWBlocks.grassSlab.blockID) {
            @SuppressWarnings("ConstantConditions")
            GrassSlabBlock slab = (GrassSlabBlock) (Object) this;
            boolean sparse = slab.isSparse(world, x, y, z);
            if (sparse) {
                world.setBlockWithNotify(x, y, z, BTWBlocks.looseDirtSlab.blockID);
                if (!world.isRemote) {
                    world.playAuxSFX(BTWEffectManager.DIRT_TILLING_EFFECT_ID, x, y, z, 0);
                }
                cir.setReturnValue(true);
            }
        }
    }
}
