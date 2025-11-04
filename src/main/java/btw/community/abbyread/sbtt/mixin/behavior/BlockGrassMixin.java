package btw.community.abbyread.sbtt.mixin.behavior;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import btw.community.abbyread.categories.ItemType;
import btw.community.abbyread.categories.ThisItem;
import btw.item.items.ChiselItemStone;
import btw.item.items.ChiselItemWood;
import btw.item.items.ShovelItemStone;
import btw.world.util.WorldUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import btw.community.abbyread.sbtt.util.SeedDropChance;

/**
 * Custom behavior mixin for BlockGrass, adding new conversion mechanics
 * (bone club packing, chisel loosening/sparsening) and probability-based seed drops
 * that persist per-player across sessions.
 */
@Mixin(BlockGrass.class)
public abstract class BlockGrassMixin {

    // Metadata constants
    @Unique private static final int PACKED_EARTH = 6;
    @Unique private static final int FIRM_DIRT = 0;
    @Unique private static final int SPARSE = 1;

    /* ------------------------------------------------------------ */
    /*  Override improper tool behavior                             */
    /* ------------------------------------------------------------ */

    @Inject(
            method = "onNeighborDirtDugWithImproperTool",
            at = @At("HEAD"),
            cancellable = true
    )
    private void reimplementHardcoreGrassDrop(World world, int x, int y, int z, int toFacing, CallbackInfo ci) {
        if (toFacing == 0) {
            world.setBlockWithNotify(x, y, z, BTWBlocks.looseDirt.blockID);
        }
        ci.cancel();
    }

    @Inject(method = "onBlockDestroyedWithImproperTool", at = @At("HEAD"), cancellable = true)
    private void overrideDisturbanceFromStoneShovel(World world, EntityPlayer player, int x, int y, int z, int metadata, CallbackInfo ci) {
        ItemStack stack = player.inventory.getCurrentItem();
        if (stack == null) return;

        if (stack.getItem() instanceof ShovelItemStone) {
            // Play improper tool FX, drop components manually
            world.playAuxSFX(BTWEffectManager.BLOCK_DESTROYED_WITH_IMPROPER_TOOL_EFFECT_ID, x, y, z,
                    world.getBlockId(x, y, z) + (metadata << 12));
            ((Block)(Object)this).dropComponentItemsOnBadBreak(world, x, y, z, metadata, 1F);
            ci.cancel();
        }
    }

    /* ------------------------------------------------------------ */
    /*  Block conversions                                           */
    /* ------------------------------------------------------------ */

    // Bone club -> packed earth
    @Inject(method = "canConvertBlock", at = @At("HEAD"), cancellable = true)
    private void canClubConvert(ItemStack stack, World world, int x, int y, int z,
                                CallbackInfoReturnable<Boolean> cir) {
        if (stack != null && ThisItem.isAnd(ItemType.CLUB, ItemType.BONE, stack)) {
            if (!WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, x, y + 1, z, 0) &&
                    WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, x, y - 1, z, 0)) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "convertBlock", at = @At("HEAD"), cancellable = true)
    private void clubConvert(ItemStack stack, World world, int x, int y, int z, int iFromSide,
                             CallbackInfoReturnable<Boolean> cir) {
        if (stack == null || !ThisItem.isAnd(ItemType.CLUB, ItemType.BONE, stack)) return;

        world.setBlockAndMetadataWithNotify(x, y, z, BTWBlocks.dirtSlab.blockID, PACKED_EARTH);

        if (!world.isRemote) {
            world.playAuxSFX(BTWEffectManager.BLOCK_DESTROYED_WITH_IMPROPER_TOOL_EFFECT_ID, x, y, z, 0);
            Block block = Block.dirt;
            world.playSoundEffect((float)x + 0.5f, (float)y + 0.5f, (float)z + 0.5f,
                    block.getStepSound(world, x, y, z).getBreakSound(),
                    block.getStepSound(world, x, y, z).getPlaceVolume() + 2.0f,
                    block.getStepSound(world, x, y, z).getPlacePitch() * 0.7f);
        }
        cir.setReturnValue(true);
    }

    // Loosen with pointy stick
    @Inject(method = "canConvertBlock", at = @At("HEAD"), cancellable = true)
    private void canLoosenWithPointyStick(ItemStack stack, World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null || !(stack.getItem() instanceof ChiselItemWood)) return;
        if (world.getBlockMetadata(x, y, z) == SPARSE) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "convertBlock", at = @At("HEAD"), cancellable = true)
    private void loosenWithPointyStick(ItemStack stack, World world, int x, int y, int z, int side, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null || !(stack.getItem() instanceof ChiselItemWood)) return;

        world.setBlockWithNotify(x, y, z, BTWBlocks.looseSparseGrass.blockID);
        Block block = (Block) (Object) this;
        world.playSoundEffect((float)x + 0.5f, (float)y + 0.5f, (float)z + 0.5f,
                block.getStepSound(world, x, y, z).getBreakSound(),
                block.getStepSound(world, x, y, z).getPlaceVolume() + 2.0f,
                block.getStepSound(world, x, y, z).getPlacePitch() * 0.7f);
        cir.setReturnValue(true);
    }

    // Sparsen with sharp stone
    @Inject(method = "canConvertBlock", at = @At("HEAD"), cancellable = true)
    private void canSparsenWithSharpStone(ItemStack stack, World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (stack != null && stack.getItem() instanceof ChiselItemStone) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "convertBlock", at = @At("HEAD"), cancellable = true)
    private void sparsenWithSharpStone(ItemStack stack, World world, int x, int y, int z, int side, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null || !(stack.getItem() instanceof ChiselItemStone)) return;

        BlockGrass block = (BlockGrass)(Object)this;
        int metadata = world.getBlockMetadata(x, y, z);

        if (block.isSparse(metadata)) {
            world.setBlockAndMetadataWithNotify(x, y, z, Block.dirt.blockID, FIRM_DIRT);
        } else {
            world.setBlockAndMetadataWithNotify(x, y, z, Block.grass.blockID, SPARSE);
        }

        // Use closest player to trigger seed drop
        if (!world.isRemote) {
            EntityPlayer player = world.getClosestPlayer(x + 0.5, y + 0.5, z + 0.5, 5.0);
            if (player != null) {
                SeedDropChance.maybeDropSeed(player, world, x, y, z, side);
            }
        }

        world.playSoundEffect((float)x + 0.5f, (float)y + 0.5f, (float)z + 0.5f,
                block.getStepSound(world, x, y, z).getBreakSound(),
                block.getStepSound(world, x, y, z).getPlaceVolume() + 2.0f,
                block.getStepSound(world, x, y, z).getPlacePitch() * 0.7f);

        cir.setReturnValue(true);
    }

}
