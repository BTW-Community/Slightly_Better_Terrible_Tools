package btw.community.abbyread.sbtt.mixin;

import btw.client.fx.BTWEffectManager;
import btw.community.abbyread.categories.ItemTags;
import btw.community.abbyread.categories.ItemTag;
import btw.community.abbyread.categories.BlockSide;
import btw.community.abbyread.sbtt.helper.InteractionHandler;
import btw.community.abbyread.sbtt.helper.InteractionHandler.InteractionType;
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

    @Inject(method = "onBlockDestroyedWithImproperTool", at = @At("HEAD"), cancellable = true)
    private void abbyread$overrideDisturbanceFromStoneShovel(World world, EntityPlayer player, int x, int y, int z, int metadata, CallbackInfo ci) {
        ItemStack stack = player.inventory.getCurrentItem();
        if (stack == null) return;

        // Probably only comes up for stone shovels, but target all anyway
        if (ItemTags.isAll(stack, ItemTag.SHOVEL, ItemTag.STONE)) {

            // Unrolled: super.onBlockDestroyedWithImproperTool(world, player, x, y, z, metadata);
            world.playAuxSFX( BTWEffectManager.BLOCK_DESTROYED_WITH_IMPROPER_TOOL_EFFECT_ID, x, y, z, world.getBlockId(x, y, z) + ( metadata << 12 ) );
            self.dropComponentItemsOnBadBreak(world, x, y, z, metadata, 1F);

            ci.cancel();
            // Skips the call to onDirtDugWithImproperTool, which would loosen neighboring blocks.
        }
    }

    @Inject(method = "canConvertBlock", at = @At("HEAD"), cancellable = true)
    private void abbyread$unifiedCanConvertBlock(ItemStack stack, World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null) return;
        Block block = (Block) (Object) this;
        int meta = world.getBlockMetadata(x, y, z);

        if (InteractionHandler.canInteract(stack, block, meta, InteractionType.PRIMARY_LEFT_CLICK)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "convertBlock", at = @At("HEAD"), cancellable = true)
    private void abbyread$unifiedConvertBlock(ItemStack stack, World world, int x, int y, int z, int fromSide, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null) return;
        Block block = (Block) (Object) this;
        int meta = world.getBlockMetadata(x, y, z);
        EntityPlayer player = null; // fromSide doesn't give us the player; passing null is safe for conversions

        BlockSide side = BlockSide.fromId(fromSide);
        if (InteractionHandler.interact(stack, player, block, meta, world, x, y, z, side, InteractionType.PRIMARY_LEFT_CLICK)) {
            cir.setReturnValue(true);
        }
    }
}