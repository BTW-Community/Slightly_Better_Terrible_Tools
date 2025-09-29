package net.fabricmc.abbyread.mixin;

import btw.block.BTWBlocks;
import btw.block.blocks.DirtSlabBlock;
import btw.block.blocks.GrassSlabBlock;
import btw.client.fx.BTWEffectManager;
import btw.community.abbyread.categories.BlockCategories;
import btw.community.abbyread.categories.BlockCategory;
import btw.community.abbyread.sbtt.Efficiency;
import btw.item.items.ChiselItemStone;
import btw.item.items.ChiselItemWood;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(Block.class)
public class BlockMixin {
    @Shadow @Final public int blockID;

    @Inject(
            method = "canConvertBlock", at = @At("HEAD"), cancellable = true
    )
    private void abbyread$canConvertBlock(ItemStack stack, World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir){
        if (stack == null) {
            cir.setReturnValue(false);
            return;
        }
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
        if (stack == null) {
            cir.setReturnValue(false);
            return;
        }
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
        if (stack.getItem() instanceof ChiselItemStone &&
                this.blockID == BTWBlocks.dirtSlab.blockID) {
            @SuppressWarnings("ConstantConditions")
            DirtSlabBlock slab = (DirtSlabBlock) (Object) this;
            int meta = slab.getSubtype(world, x, y, z);
            if (meta == 1) {
                world.setBlockAndMetadataWithNotify(x, y, z, BTWBlocks.dirtSlab.blockID, 0);
                if (!world.isRemote) {
                    world.playAuxSFX(BTWEffectManager.DIRT_TILLING_EFFECT_ID, x, y, z, 0);
                }
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(
            method = "getPlayerRelativeBlockHardness",
            at = @At("RETURN"),
            cancellable = true
    )
    private void abbyread$boostLooseBlocks(EntityPlayer player, World world, int x, int y, int z, CallbackInfoReturnable<Float> cir) {
        Block self = (Block)(Object)this;
        int meta = world.getBlockMetadata(x, y, z);

        Set<BlockCategory> cats = BlockCategories.of(self, meta);
        if (cats.contains(BlockCategory.LOOSE)) {
            float base = cir.getReturnValue();
            cir.setReturnValue(base * Efficiency.modifier * 2);
        }

        if (cats.contains(BlockCategory.LOG)) {
            float base = cir.getReturnValue();
            cir.setReturnValue(base * Efficiency.modifier * 2);
        }
    }
}
