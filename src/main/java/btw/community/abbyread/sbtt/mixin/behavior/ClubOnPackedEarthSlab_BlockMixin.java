package btw.community.abbyread.sbtt.mixin.behavior;

import btw.block.BTWBlocks;
import btw.community.abbyread.categories.BlockType;
import btw.community.abbyread.categories.ThisBlock;
import btw.item.BTWItems;
import btw.world.util.WorldUtils;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class ClubOnPackedEarthSlab_BlockMixin {

    @Unique
    private static final int PACKED_EARTH = 6;

    @Unique
    private static final int FIRM_DIRT = 0;

    // Pack using bone club
    @Inject(method = "canConvertBlock", at = @At("HEAD"), cancellable = true)
    private void canFirmOrPackDownward(ItemStack stack, World world, int x, int y, int z,
                                CallbackInfoReturnable<Boolean> cir) {
        Block self = (Block) (Object) this;
        int metadata = world.getBlockMetadata(x, y, z);

        // Return early if queried block is not a packed earth slab
        if (self.blockID != BTWBlocks.dirtSlab.blockID ||
            metadata != PACKED_EARTH) return;

        // Return early if held item is not a bone club
        if (stack == null || stack.getItem().itemID != BTWItems.boneClub.itemID) return;

        // Return early if there is a solid block above or there isn't one below
        if (WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, x, y + 1, z, 0) &&
                !WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, x, y - 1, z, 0)) {
            return;
        }

        // Return early if there isn't a solid block below the neighbor below
        if (!WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, x, y - 2, z, 0)) {
            return;
        }

        Block neighborBelow = Block.blocksList[world.getBlockId(x, y - 1, z)];
        int metadataBelow = world.getBlockMetadata(x, y - 1, z);

        // Firm the block below if it's loose (damages as if packing still though)
        if (ThisBlock.is(BlockType.LOOSE_DIRTLIKE, neighborBelow, metadataBelow)) {
            cir.setReturnValue(true);
        }
        // Pack downward if neighborBelow is a firm dirtlike block
        else if (ThisBlock.is(BlockType.FIRM_DIRTLIKE, neighborBelow, metadataBelow)) {
            cir.setReturnValue(true);
        }

        // Continue with regular canConvert code otherwise
    }

    @Inject(method = "convertBlock", at = @At("HEAD"), cancellable = true)
    private void clubConvert(ItemStack stack, World world, int x, int y, int z, int iFromSide,
                             CallbackInfoReturnable<Boolean> cir) {
        Block self = (Block) (Object) this;
        int metadata = world.getBlockMetadata(x, y, z);

        // Return early if queried block is not a packed earth slab
        if (self.blockID != BTWBlocks.dirtSlab.blockID ||
                metadata != PACKED_EARTH) return;

        // Return early if held item is not a bone club
        if (stack == null || stack.getItem().itemID != BTWItems.boneClub.itemID) return;

        // We're assuming canConvert was already checked and returned true

        Block neighborBelow = Block.blocksList[world.getBlockId(x, y - 1, z)];
        int metadataBelow = world.getBlockMetadata(x, y - 1, z);

        // Firm the block below if it's loose (damages as if packing still though)
        if (ThisBlock.is(BlockType.LOOSE_DIRTLIKE, neighborBelow, metadataBelow)) {
            world.setBlockAndMetadataWithNotify(x, y - 1, z, Block.dirt.blockID, FIRM_DIRT);
        }
        // Pack downward if neighborBelow is a firm dirtlike block
        else if (ThisBlock.is(BlockType.FIRM_DIRTLIKE, neighborBelow, metadataBelow)) {
            world.setBlockToAir(x, y, z);
            world.setBlockAndMetadataWithNotify(x, y - 1, z, BTWBlocks.aestheticEarth.blockID, PACKED_EARTH);

        }

        // Prevent the rest of the method from running
        cir.setReturnValue(true);
    }

}
