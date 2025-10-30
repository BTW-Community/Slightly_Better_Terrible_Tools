package btw.community.abbyread.sbtt.mixin.behavior;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import btw.community.abbyread.categories.ThisItem;
import btw.community.abbyread.categories.ItemType;
import btw.world.util.WorldUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockDirt.class)
public class BlockDirtMixin {
    @Unique Block self = (Block)(Object)this;

    @Unique private static final int PACKED_EARTH = 6; // metadata value

    // Remove dirt disturbance from stone shovel use
    @Inject(method = "onBlockDestroyedWithImproperTool", at = @At("HEAD"), cancellable = true)
    private void abbyread$overrideDisturbanceFromStoneShovel(World world, EntityPlayer player, int x, int y, int z, int metadata, CallbackInfo ci) {
        ItemStack stack = player.inventory.getCurrentItem();
        if (stack == null) return;

        if (ThisItem.isAll(stack, ItemType.SHOVEL, ItemType.STONE)) {

            // Copied from original call to super.onBlockDestroyedWithImproperTool(world, player, x, y, z, metadata);
            world.playAuxSFX( BTWEffectManager.BLOCK_DESTROYED_WITH_IMPROPER_TOOL_EFFECT_ID, x, y, z, world.getBlockId(x, y, z) + ( metadata << 12 ) );
            self.dropComponentItemsOnBadBreak(world, x, y, z, metadata, 1F);

            ci.cancel();
            // Skips the call to onDirtDugWithImproperTool, which would loosen neighboring blocks.
        }
    }

    // Pack dirt using bone club
    @Inject(method = "canConvertBlock", at = @At("HEAD"), cancellable = true)
    private void canClubConvert(ItemStack stack, World world, int x, int y, int z,
                                CallbackInfoReturnable<Boolean> cir) {
        // Bone clubs can convert dirt to packed earth slab
        if (stack != null && ThisItem.isAnd(ItemType.CLUB, ItemType.BONE, stack)) {
            // Can only pack if there isn't a solid block above
            if (!WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, x, y + 1, z, 0)) {
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
            world.playAuxSFX(BTWEffectManager.DIRT_TILLING_EFFECT_ID, x, y, z, 0);
        }

        cir.setReturnValue(true);
    }

}
