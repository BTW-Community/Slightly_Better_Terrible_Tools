package btw.community.abbyread.sbtt.oldMixins;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import btw.item.items.ChiselItemWood;
import btw.item.items.ShovelItemStone;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockDirt.class)
public class BlockDirtMixin {

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

    @Inject(method = "canConvertBlock", at = @At("HEAD"), cancellable = true)
    private void abby$addCanConvertInclusions(ItemStack stack, World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir){
        if (stack != null && stack.getItem() instanceof ChiselItemWood) {
            cir.setReturnValue(true);
        }
    }

    @Inject(
            method = "convertBlock", at = @At("HEAD"), cancellable = true
    )
    private void abby$addConvertByPointyStick(ItemStack stack, World world, int x, int y, int z, int fromSide, CallbackInfoReturnable<Boolean> cir){
        if (stack != null && stack.getItem() instanceof ChiselItemWood) {
            if (!world.isRemote) {
                world.setBlockWithNotify(x, y, z, BTWBlocks.looseDirt.blockID);
                world.playAuxSFX(BTWEffectManager.DIRT_TILLING_EFFECT_ID, x, y, z, 0);
            }
            cir.setReturnValue(true);
        }
    }
}
