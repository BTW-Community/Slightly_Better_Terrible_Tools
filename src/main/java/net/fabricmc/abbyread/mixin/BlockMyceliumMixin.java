package net.fabricmc.abbyread.mixin;

import btw.client.fx.BTWEffectManager;
import btw.community.abbyread.sbtt.Convert;
import net.minecraft.src.*;
import btw.item.items.ShovelItemStone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockMycelium.class)
public class BlockMyceliumMixin {

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
    private void abbyread$canConvertBlock(ItemStack stack, World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null) return;
        Block block = (Block)(Object)this;
        int meta = world.getBlockMetadata(x, y, z);
        if (Convert.canConvert(stack, block, meta)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "convertBlock", at = @At("HEAD"), cancellable = true)
    private void abbyread$convertBlock(ItemStack stack, World world, int x, int y, int z, int fromSide, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null || world.isRemote) return;
        Block block = (Block)(Object)this;
        int meta = world.getBlockMetadata(x, y, z);
        if (Convert.convert(stack, block, meta, world, x, y, z, fromSide)) {
            cir.setReturnValue(true);
        }
    }
}
