package net.fabricmc.abbyread.mixin;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import btw.item.BTWItems;
import btw.item.items.ChiselItemStone;
import btw.item.items.ChiselItemWood;
import btw.item.items.ShovelItemStone;
import btw.item.util.ItemUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockGrass.class)
public abstract class BlockGrassMixin {
    @Shadow public abstract boolean isSparse(int metadata);

    @Inject(
        method = "onNeighborDirtDugWithImproperTool",
        at = @At("HEAD"),
        cancellable = true
    )
    private void abby$reimplementHardcoreGrassDrop(World world, int x, int y, int z, int toFacing, CallbackInfo ci) {
        if (toFacing == 0) {
            world.setBlockWithNotify(x, y, z, BTWBlocks.looseDirt.blockID);
        }
        ci.cancel();
    }

    @Inject(method = "onBlockDestroyedWithImproperTool", at = @At("HEAD"), cancellable = true)
    private void abby$overrideDisturbanceFromStoneShovel(World world, EntityPlayer player, int x, int y, int z, int metadata, CallbackInfo ci) {
        ItemStack stack = player.inventory.getCurrentItem();
        if (stack == null) return;

        if (stack.getItem() instanceof ShovelItemStone) {

            // Unrolled: super.onBlockDestroyedWithImproperTool(world, player, x, y, z, metadata);
            world.playAuxSFX( BTWEffectManager.BLOCK_DESTROYED_WITH_IMPROPER_TOOL_EFFECT_ID, x, y, z, world.getBlockId(x, y, z) + ( metadata << 12 ) );
            ((Block)(Object)this).dropComponentItemsOnBadBreak(world, x, y, z, metadata, 1F);

            ci.cancel();
            // Skips the call to onDirtDugWithImproperTool, which would loosen neighboring blocks.
        }
    }

    @Inject(
        method = "canConvertBlock", at = @At("HEAD"), cancellable = true
    )
    private void abbyread$canConvertBlock(ItemStack stack, World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir){
        if (stack != null && stack.getItem() instanceof ChiselItemWood && this.isSparse(world.getBlockMetadata(x, y, z))) {
            cir.setReturnValue(true);
        } else if (stack != null && stack.getItem() instanceof ChiselItemStone && !this.isSparse(world.getBlockMetadata(x, y, z))) {
            cir.setReturnValue(true);
        }
    }

    @Inject(
            method = "convertBlock", at = @At("HEAD"), cancellable = true
    )
    private void abbyread$convertBlock(ItemStack stack, World world, int x, int y, int z, int fromSide, CallbackInfoReturnable<Boolean> cir){
        // --- Pointy stick converts sparse grass ---
        if (stack != null && stack.getItem() instanceof ChiselItemWood && this.isSparse(world.getBlockMetadata(x, y, z))) {
            if (!world.isRemote) {
                world.setBlockWithNotify(x, y, z, BTWBlocks.looseDirt.blockID);
                world.playAuxSFX(BTWEffectManager.DIRT_TILLING_EFFECT_ID, x, y, z, 0);
            }
            cir.setReturnValue(true);
        }

        // --- Sharp stone converts full grass ---
        if (stack != null && stack.getItem() instanceof ChiselItemStone && !(this.isSparse(world.getBlockMetadata(x, y, z)))) {
            final int SPARSE = 1;
            final int VERY_LOW_HEMP_SEED_CHANCE = 1000;
            if (!world.isRemote) {
                world.setBlockMetadataWithNotify(x, y, z, SPARSE);
                world.playAuxSFX(BTWEffectManager.DIRT_TILLING_EFFECT_ID, x, y, z, 0);
                if (world.rand.nextInt(VERY_LOW_HEMP_SEED_CHANCE) == 0) {
                    ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, new ItemStack(BTWItems.hempSeeds), fromSide);
                }
            }
            cir.setReturnValue(true);
        }
    }
}