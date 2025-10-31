package btw.community.abbyread.sbtt.mixin.behavior;

import btw.block.BTWBlocks;
import btw.block.blocks.DirtSlabBlock;
import btw.community.abbyread.categories.BlockType;
import btw.community.abbyread.categories.ThisBlock;
import btw.item.BTWItems;
import btw.item.items.ChiselItemWood;
import btw.world.util.WorldUtils;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class DirtSlabBlock_BlockMixin {

    @Shadow @Final public int blockID;
    @Unique private static final int FIRM_DIRT = 0;
    @Unique private static final int PACKED_EARTH = 6;

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

    // Loosen/unpack using pointy stick
    @Inject(method = "canConvertBlock", at = @At("HEAD"), cancellable = true)
    private void canLoosenWithPointyStick(ItemStack stack, World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null || !(stack.getItem() instanceof ChiselItemWood)) return;

        Block block = (Block) (Object) this;
        if (!(block instanceof DirtSlabBlock)) return;
        int metadata = world.getBlockMetadata(x, y, z);

        boolean canConvert = switch (metadata) {
            case PACKED_EARTH, FIRM_DIRT -> true;
            default -> false;
        };
        if (canConvert) cir.setReturnValue(true);

        // Continue with the rest of the method's logic otherwise
    }
    @Inject(method = "convertBlock", at = @At("HEAD"), cancellable = true)
    private void loosenWithPointyStick(ItemStack stack, World world, int x, int y, int z, int side, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null || !(stack.getItem() instanceof ChiselItemWood)) return;

        Block block = (Block) (Object) this;
        if (!(block instanceof DirtSlabBlock)) return;
        int metadata = world.getBlockMetadata(x, y, z);

        int toBlockID = switch (metadata) {
            case PACKED_EARTH -> Block.dirt.blockID;
            case FIRM_DIRT -> BTWBlocks.looseDirtSlab.blockID;
            default -> 0;
        };
        if (toBlockID != 0) {
            world.setBlockWithNotify(x, y, z, toBlockID);
            cir.setReturnValue(true);
        }
    }

}
