package btw.community.abbyread.sbtt.mixin.behavior;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import btw.community.abbyread.categories.ItemType;
import btw.community.abbyread.categories.ThisItem;
import btw.item.BTWItems;
import btw.item.items.ChiselItemStone;
import btw.item.items.ChiselItemWood;
import btw.item.items.ShovelItemStone;
import btw.item.util.ItemUtils;
import btw.world.util.WorldUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static btw.community.abbyread.sbtt.util.Globals.OUT_OF_CHANCE;

@Mixin(BlockGrass.class)
public abstract class BlockGrassMixin {

    // Metadata values
    @Unique private static final int PACKED_EARTH = 6;
    @Unique private static final int FIRM_DIRT = 0;
    @Unique private static final int SPARSE = 1;

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

    // Pack dirt using bone club
    @Inject(method = "canConvertBlock", at = @At("HEAD"), cancellable = true)
    private void canClubConvert(ItemStack stack, World world, int x, int y, int z,
                                CallbackInfoReturnable<Boolean> cir) {
        // Bone clubs can convert grass to packed earth slab
        if (stack != null && ThisItem.isAnd(ItemType.CLUB, ItemType.BONE, stack)) {
            // Can only pack if there is a solid block below and not above
            if (!WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, x, y + 1, z, 0) &&
                WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, x, y - 1, z, 0)) {
                cir.setReturnValue(true);
            }

        }
    }
    @Inject(method = "convertBlock", at = @At("HEAD"), cancellable = true)
    private void clubConvert(ItemStack stack, World world, int x, int y, int z, int iFromSide,
                             CallbackInfoReturnable<Boolean> cir) {
        // Only handle bone club conversion to packed earth slab
        if (stack == null || !ThisItem.isAnd(ItemType.CLUB, ItemType.BONE, stack)) {
            return;
        }

        // Convert loose dirt to firm dirt if there isn't a solid block above
        world.setBlockAndMetadataWithNotify(x, y, z, BTWBlocks.dirtSlab.blockID, PACKED_EARTH);

        // Play the tilling effect
        if (!world.isRemote) {
            world.playAuxSFX(BTWEffectManager.BLOCK_DESTROYED_WITH_IMPROPER_TOOL_EFFECT_ID, x, y, z, 0);
            Block block = Block.dirt;

            world.playSoundEffect((float)x + 0.5f, (float)y + 0.5f, (float)z + 0.5f, block.getStepSound(world, x, y, z).getBreakSound(), block.getStepSound(world, x, y, z).getPlaceVolume() + 2.0f, block.getStepSound(world, x, y, z).getPlacePitch() * 0.7f);
        }

        cir.setReturnValue(true);
    }

    // Loosen with pointy stick
    @Inject(method = "canConvertBlock", at = @At("HEAD"), cancellable = true)
    private void canLoosenWithPointyStick(ItemStack stack, World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null || !(stack.getItem() instanceof ChiselItemWood)) return;

        BlockGrass block = (BlockGrass) (Object) this;
        int metadata = world.getBlockMetadata(x, y, z);

        // Only allow loosening on sparse grass
        if (block.isSparse(metadata)) cir.setReturnValue(true);
    }
    @Inject(method = "convertBlock", at = @At("HEAD"), cancellable = true)
    private void loosenWithPointyStick(ItemStack stack, World world, int x, int y, int z, int side, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null || !(stack.getItem() instanceof ChiselItemWood)) return;

        // Assuming canConvert returned true after verifying the grass is sparse
        world.setBlockWithNotify(x, y, z, BTWBlocks.looseSparseGrass.blockID);

        cir.setReturnValue(true);
    }

    // Sparsen with sharp stone
    @Inject(method = "canConvertBlock", at = @At("HEAD"), cancellable = true)
    private void canSparsenWithSharpStone(ItemStack stack, World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null || !(stack.getItem() instanceof ChiselItemStone)) return;

        cir.setReturnValue(true);
    }
    @Inject(method = "convertBlock", at = @At("HEAD"), cancellable = true)
    private void sparsenWithSharpStone(ItemStack stack, World world, int x, int y, int z, int side, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null || !(stack.getItem() instanceof ChiselItemStone)) return;

        BlockGrass block = (BlockGrass) (Object) this;
        int metadata = world.getBlockMetadata(x, y, z);

        if (block.isSparse(metadata)) {
            world.setBlockAndMetadataWithNotify(x, y, z, Block.dirt.blockID, FIRM_DIRT);
        } else {
            world.setBlockAndMetadataWithNotify(x, y, z, Block.grass.blockID, SPARSE);
        }

        // Process seed chance once (just on server)
        if (!world.isRemote) maybeGetSeeds(world, x, y, z, side);

        world.playSoundEffect((float)x + 0.5f, (float)y + 0.5f, (float)z + 0.5f, block.getStepSound(world, x, y, z).getBreakSound(), block.getStepSound(world, x, y, z).getPlaceVolume() + 2.0f, block.getStepSound(world, x, y, z).getPlacePitch() * 0.7f);
        cir.setReturnValue(true);

    }

    @Unique
    private void maybeGetSeeds(World world, int x, int y, int z, int side) {
        if (world.rand.nextInt(OUT_OF_CHANCE) == 0) {
            ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, new ItemStack(BTWItems.hempSeeds), side);
        }
    }

}