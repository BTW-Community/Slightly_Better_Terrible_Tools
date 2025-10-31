package btw.community.abbyread.sbtt.mixin.behavior;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import btw.community.abbyread.categories.ItemType;
import btw.community.abbyread.categories.ThisItem;
import btw.world.util.WorldUtils;
import net.minecraft.src.*;
import btw.item.items.ShovelItemStone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockMycelium.class)
public class BlockMyceliumMixin {

    @Unique
    private static final int PACKED_EARTH = 6; // metadata value

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

    // Pack using bone club
    @Inject(method = "canConvertBlock", at = @At("HEAD"), cancellable = true)
    private void canClubConvert(ItemStack stack, World world, int x, int y, int z,
                                CallbackInfoReturnable<Boolean> cir) {
        // Bone clubs can convert to packed earth slab
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

        // Convert assuming canConvert already came back true
        world.setBlockAndMetadataWithNotify(x, y, z, BTWBlocks.dirtSlab.blockID, PACKED_EARTH);

        // Play sound effect
        if (!world.isRemote) {
            world.playAuxSFX(BTWEffectManager.BLOCK_DESTROYED_WITH_IMPROPER_TOOL_EFFECT_ID, x, y, z, 0);
            Block block = Block.dirt;

            world.playSoundEffect((float)x + 0.5f, (float)y + 0.5f, (float)z + 0.5f, block.getStepSound(world, x, y, z).getBreakSound(), block.getStepSound(world, x, y, z).getPlaceVolume() + 2.0f, block.getStepSound(world, x, y, z).getPlacePitch() * 0.7f);
        }

        cir.setReturnValue(true);
    }

}
