package btw.community.abbyread.sbtt.mixin.behavior;

import btw.block.BTWBlocks;
import btw.block.blocks.AestheticOpaqueEarthBlock;
import btw.item.items.ChiselItemWood;
import btw.world.util.WorldUtils;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AestheticOpaqueEarthBlock.class)
public class AestheticOpaqueEarthBlockMixin {

    @Unique
    private static final int FIRM_DIRT = 0;
    @Unique private static final int PACKED_EARTH = 6;

    // Unpack earth using pointy stick
    @Inject(method = "canConvertBlock", at = @At("HEAD"), cancellable = true)
    private void canUnpackWithPointyStick(ItemStack stack, World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null || !(stack.getItem() instanceof ChiselItemWood)) return;

        int metadata = world.getBlockMetadata(x, y, z);
        if (metadata != PACKED_EARTH) return;

        // Return early if there is a solid block above
        if (!WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, x, y + 1, z, 0)) cir.setReturnValue(true);

        // Continue with the rest of the method's logic otherwise
    }
    @Inject(method = "convertBlock", at = @At("HEAD"), cancellable = true)
    private void unpackWithPointyStick(ItemStack stack, World world, int x, int y, int z, int side, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null || !(stack.getItem() instanceof ChiselItemWood)) return;

        int metadata = world.getBlockMetadata(x, y, z);
        if (metadata != PACKED_EARTH) return;

        world.setBlockAndMetadataWithNotify(x, y, z, Block.dirt.blockID, FIRM_DIRT);
        world.setBlockAndMetadataWithNotify(x, y + 1, z, BTWBlocks.dirtSlab.blockID, PACKED_EARTH);
        cir.setReturnValue(true);

    }
}
