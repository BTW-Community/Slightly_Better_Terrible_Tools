package net.fabricmc.abbyread.mixin;

import btw.block.blocks.*;
import btw.item.items.ChiselItem;
import btw.item.items.ChiselItemStone;
import btw.item.items.ChiselItemWood;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChiselItem.class)
public class ChiselItemMixin {

    // ChiselItemWood inherits isEfficientVsBlock directly from ChiselItem, so...
    @Inject(method = "isEfficientVsBlock", at = @At("HEAD"), cancellable = true)
    public void abby$pointyStickEfficiencies(ItemStack stack, World world, Block block, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() instanceof ChiselItemWood)
        {
            // Add efficiency toward non-loose, and non-full grass soil types
            final int GRASS_SPARSE = 1;
            final int DIRTSLAB_DIRT = 0;
            final int PACKED_EARTH = 6;
            if (block instanceof BlockDirt) {
                cir.setReturnValue(true);
                return;
            }
            if (block instanceof DirtSlabBlock && world.getBlockMetadata(x, y, z) == DIRTSLAB_DIRT) {
                cir.setReturnValue(true);
                return;
            }
            if (block instanceof BlockGrass && world.getBlockMetadata(x, y, z) == GRASS_SPARSE) {
                cir.setReturnValue(true);
                return;
            }
            if (block instanceof GrassSlabBlock && ((GrassSlabBlock) block).isSparse(world, x, y, z)) {
                cir.setReturnValue(true);
                return;
            }
            if (block instanceof AestheticOpaqueEarthBlock && world.getBlockMetadata(x, y, z) == PACKED_EARTH) {
                cir.setReturnValue(true);
                return;
            }

            // Prevent efficiency toward full-grass blocks and glass-likes, wood, and rough stone
            final int GRASS_FULL = 0;
            final int DIRTSLAB_GRASS = 1;
            if (block instanceof DirtSlabBlock && world.getBlockMetadata(x, y, z) == DIRTSLAB_GRASS) {
                cir.setReturnValue(false);
                return;
            }
            if (block instanceof BlockGrass && world.getBlockMetadata(x, y, z) == GRASS_FULL) {
                cir.setReturnValue(false);
                return;
            }
            if (block instanceof RoughStoneBlock /* && ((RoughStoneBlock) block).strataLevel != 0 */) {
                cir.setReturnValue(false);
                return;
            }
            if (block instanceof BlockGlass) {
                cir.setReturnValue(false);
                return;
            }
            if (block instanceof BlockGlowStone) {
                cir.setReturnValue(false);
                return;
            }
            if (block instanceof BlockIce) {
                cir.setReturnValue(false);
                return;
            }
            if (block instanceof BlockPane) {
                cir.setReturnValue(false);
                return;
            }
            if (block instanceof BlockRedstoneLight) {
                cir.setReturnValue(false);
                return;
            }
            if (block instanceof LightBlock) {
                cir.setReturnValue(false);
                return;
            }
            if (block instanceof BlockLog) {
                cir.setReturnValue(false);
                return;
            }
            if (block instanceof ChewedLogBlock) {
                cir.setReturnValue(false);
                return;
            }
            if (block instanceof LogSpikeBlock) {
                cir.setReturnValue(false);
                return;
            }
        }
    }

    // ChiselItemStone inherits isEfficientVsBlock directly from ChiselItem, so...
    @Inject(method = "isEfficientVsBlock", at = @At("HEAD"), cancellable = true)
    public void abby$sharpStoneEfficiencies(ItemStack stack, World world, Block block, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() instanceof ChiselItemStone)
        {
            // Add efficiency toward full-grass blocks and glass-likes
            final int GRASS_FULL = 0;
            final int DIRTSLAB_GRASS = 1;
            if (block instanceof DirtSlabBlock && world.getBlockMetadata(x, y, z) == DIRTSLAB_GRASS) {
                cir.setReturnValue(true);
                return;
            }
            if (block instanceof BlockGrass && world.getBlockMetadata(x, y, z) == GRASS_FULL) {
                cir.setReturnValue(true);
                return;
            }
            if (block instanceof RoughStoneBlock && ((RoughStoneBlock) block).strataLevel == 0) {
                cir.setReturnValue(true);
                return;
            }
            if (block instanceof BlockGlass) {
                cir.setReturnValue(true);
                return;
            }
            if (block instanceof BlockGlowStone) {
                cir.setReturnValue(true);
                return;
            }
            if (block instanceof BlockIce) {
                cir.setReturnValue(true);
                return;
            }
            if (block instanceof BlockPane) {
                cir.setReturnValue(true);
                return;
            }
            if (block instanceof BlockRedstoneLight) {
                cir.setReturnValue(true);
                return;
            }
            if (block instanceof LightBlock) {
                cir.setReturnValue(true);
                return;
            }

            // Prevent efficiency toward non-loose, and non-full grass soil types
            final int GRASS_SPARSE = 1;
            final int DIRTSLAB_DIRT = 0;
            final int PACKED_EARTH = 6;
            if (block instanceof BlockDirt) {
                cir.setReturnValue(false);
                return;
            }
            if (block instanceof BlockGrass && world.getBlockMetadata(x, y, z) == GRASS_SPARSE) {
                cir.setReturnValue(false);
                return;
            }
            if (block instanceof DirtSlabBlock && world.getBlockMetadata(x, y, z) == DIRTSLAB_DIRT) {
                cir.setReturnValue(false);
                return;
            }
            if (block instanceof GrassSlabBlock && ((GrassSlabBlock) block).isSparse(world, x, y, z)) {
                cir.setReturnValue(false);
                return;
            }
            if (block instanceof AestheticOpaqueEarthBlock && world.getBlockMetadata(x, y, z) == PACKED_EARTH) {
                cir.setReturnValue(false);
                return;
            }
        }
    }
}