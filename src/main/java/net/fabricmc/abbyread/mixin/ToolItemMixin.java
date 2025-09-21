package net.fabricmc.abbyread.mixin;

import btw.block.blocks.*;
import btw.community.abbyread.EfficiencyHelper;
import btw.community.abbyread.UniformEfficiencyModifier;
import btw.item.BTWItems;
import btw.item.items.ChiselItemStone;
import btw.item.items.ChiselItemWood;
import btw.item.items.ToolItem;

import net.minecraft.src.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ToolItem.class)
public abstract class ToolItemMixin {
    @Unique
    float effMod = UniformEfficiencyModifier.VALUE;

    @Inject(method = "onBlockDestroyed", at = @At("HEAD"), cancellable = true)
    private void abbyread$preventWastedUses(ItemStack stack, World world, int blockID, int x, int y, int z,
                                        EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir) {
        // Normal durability handling
        boolean effective = false;
        Block block = Block.blocksList[blockID];
        if (stack.getItem() instanceof ChiselItemWood ||
            stack.getItem() instanceof ChiselItemStone) {
            effective = EfficiencyHelper.isToolItemEfficientVsBlock(stack, world, block, x, y, z);
        } else  {
            effective = stack.getItem().isEfficientVsBlock(stack, world, block, x, y, z);
        }
        if (effective) stack.damageItem(1, entity);
        cir.setReturnValue(true); // always return true to cancel vanilla handling
    }

    // Because neither ChiselItem nor ChiselItemWood override this method
    @Inject(method = "getStrVsBlock", at = @At("RETURN"), cancellable = true)
    private void abbyread$pointyStickEffModBoost(ItemStack stack, World world, Block block, int x, int y, int z, CallbackInfoReturnable<Float> cir) {
        if (stack.getItem() instanceof ChiselItemWood) {
            final int GRASS_SPARSE = 1;
            final int DIRTSLAB_DIRT = 0;
            final int PACKED_EARTH = 6;
            float efficiency = cir.getReturnValue() * effMod;
            if (block instanceof DirtSlabBlock && world.getBlockMetadata(x, y, z) == DIRTSLAB_DIRT)
                cir.setReturnValue(efficiency);
            if (block instanceof BlockGrass && world.getBlockMetadata(x, y, z) == GRASS_SPARSE)
                cir.setReturnValue(efficiency);
            if (block instanceof GrassSlabBlock && ((GrassSlabBlock) block).isSparse(world, x, y, z))
                cir.setReturnValue(efficiency);
            if (block instanceof AestheticOpaqueEarthBlock && world.getBlockMetadata(x, y, z) == PACKED_EARTH)
                cir.setReturnValue(efficiency);
            if (block instanceof BlockDirt)
                cir.setReturnValue(efficiency);
        }
    }

    // Because ChiselItemStone defers to the superclass definition
    //   (this one) before boosting web harvest speed.
    @Inject(
            method = "getStrVsBlock",
            at = @At("RETURN"),
            cancellable = true
    )
    public void abbyread$sharpStoneEffModBoost(ItemStack stack, World world, Block block, int x, int y, int z, CallbackInfoReturnable<Float> cir) {
        if (block instanceof BlockGrass grass) {;
            float efficiency = cir.getReturnValue();
            if (!grass.isSparse(world, x, y, z)) {
                System.out.println("full grass");
                System.out.println("cir.getReturnValue(): " + cir.getReturnValue());
                System.out.println("efficiency * " + effMod + ": " + efficiency * effMod);
                cir.setReturnValue(efficiency * effMod);
            }
            if (grass.isSparse(world, x, y, z)) {
                System.out.println("sparse grass");
                System.out.println("cir.getReturnValue(): " + cir.getReturnValue());
                System.out.println("efficiency: " + efficiency);
                cir.setReturnValue(efficiency);
            }
        }
        if (
                block instanceof BlockGlass ||
                block instanceof BlockPane ||
                block instanceof BlockGlowStone ||
                block instanceof BlockIce ||
                block instanceof BlockRedstoneLight ||
                block instanceof BlockStone ||
                (block instanceof RoughStoneBlock &&
                        ((RoughStoneBlock) block).strataLevel == 0)
        ) {
            float efficiency = cir.getReturnValue() * effMod;
            cir.setReturnValue(efficiency);
        }
    }

    // Put last as a catch-all for things that aren't efficient
    @Inject(method = "getStrVsBlock", at = @At("RETURN"), cancellable = true)
    private void abbyread$baselineEffModBoost(ItemStack stack, World world, Block block, int x, int y, int z, CallbackInfoReturnable<Float> cir) {
        boolean effectiveOn = tool.isEfficientVsBlock(stack, world, block, x, y, z);
        if (!effectiveOn) {
            float efficiency = cir.getReturnValue();
            if (
                    block instanceof BlockSand // small universal boost
            ) {
                efficiency *= effMod;
                cir.setReturnValue(efficiency);
            } else if (
                    block instanceof LooseDirtBlock ||
                            block instanceof LooseSparseGrassBlock ||
                            block instanceof LooseSparseGrassSlabBlock ||
                            block instanceof LooseDirtSlabBlock ||
                            block instanceof ChewedLogBlock ||
                            block instanceof BlockLog
            ) {
                efficiency *= effMod * 1.5F;
                cir.setReturnValue(efficiency);
            }
        }
    }
}