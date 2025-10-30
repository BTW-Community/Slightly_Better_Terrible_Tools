package btw.community.abbyread.sbtt.mixin.behavior;

import btw.block.BTWBlocks;
import btw.block.blocks.AestheticOpaqueEarthBlock;
import btw.client.fx.BTWEffectManager;
import btw.community.abbyread.categories.*;
import btw.community.abbyread.sbtt.util.SwapContext;
import btw.world.util.WorldUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(ItemInWorldManager.class)
public abstract class ClubItem_ItemInWorldManagerMixin {
    @Shadow
    public EntityPlayerMP thisPlayerMP;

    @Unique
    private static final int TRY_PACKING = 0; // a blockID sentinel value

    @Unique
    private static final int PACKED_EARTH = 6; // the metadata value for it

    @Unique
    private static final int FIRMING_COST = 2;

    @Unique
    private static final int PACKING_COST = 4;

    @Unique
    private static Map<QualifiedBlock, QualifiedBlock> WOOD_FROM_TO;

    @Unique
    private static Map<QualifiedBlock, QualifiedBlock> BONE_FROM_TO;

    @Unique
    private static Map<QualifiedBlock, QualifiedBlock> getWoodClubMap() {
        if (WOOD_FROM_TO == null) {
            WOOD_FROM_TO = new HashMap<>();
            // Wood clubs can only firm loose blocks
            WOOD_FROM_TO.put(
                    new QualifiedBlock(BTWBlocks.looseDirt.blockID, 0),
                    new QualifiedBlock(Block.dirt.blockID, 0)
            );
            WOOD_FROM_TO.put(
                    new QualifiedBlock(BTWBlocks.looseDirtSlab.blockID, 0),
                    new QualifiedBlock(BTWBlocks.dirtSlab.blockID, 0)
            );
            WOOD_FROM_TO.put(
                    new QualifiedBlock(BTWBlocks.looseSparseGrass.blockID, 0),
                    new QualifiedBlock(Block.grass.blockID, 1)
            );
            WOOD_FROM_TO.put(
                    new QualifiedBlock(BTWBlocks.looseSparseGrassSlab.blockID, 0),
                    new QualifiedBlock(BTWBlocks.dirtSlab.blockID, 2)
            );
        }
        return WOOD_FROM_TO;
    }

    @Unique
    private static Map<QualifiedBlock, QualifiedBlock> getBoneClubMap() {
        if (BONE_FROM_TO == null) {
            BONE_FROM_TO = new HashMap<>();
            // Bone clubs can firm loose blocks AND pack firm blocks
            BONE_FROM_TO.put(
                    new QualifiedBlock(BTWBlocks.looseDirt.blockID, 0),
                    new QualifiedBlock(Block.dirt.blockID, 0)
            );
            BONE_FROM_TO.put(
                    new QualifiedBlock(BTWBlocks.looseDirtSlab.blockID, 0),
                    new QualifiedBlock(BTWBlocks.dirtSlab.blockID, 0)
            );
            BONE_FROM_TO.put(
                    new QualifiedBlock(BTWBlocks.looseSparseGrass.blockID, 0),
                    new QualifiedBlock(Block.grass.blockID, 1)
            );
            BONE_FROM_TO.put(
                    new QualifiedBlock(BTWBlocks.looseSparseGrassSlab.blockID, 0),
                    new QualifiedBlock(BTWBlocks.dirtSlab.blockID, 2)
            );
            // Packing conversions for bone clubs
            BONE_FROM_TO.put(
                    new QualifiedBlock(Block.dirt.blockID, 0),
                    new QualifiedBlock(BTWBlocks.dirtSlab.blockID, PACKED_EARTH)
            );
            BONE_FROM_TO.put(
                    new QualifiedBlock(Block.grass.blockID, 0),
                    new QualifiedBlock(BTWBlocks.dirtSlab.blockID, PACKED_EARTH)
            );
            BONE_FROM_TO.put(
                    new QualifiedBlock(Block.grass.blockID, 1),
                    new QualifiedBlock(BTWBlocks.dirtSlab.blockID, PACKED_EARTH)
            );
            BONE_FROM_TO.put(
                    new QualifiedBlock(BTWBlocks.dirtSlab.blockID, PACKED_EARTH),
                    new QualifiedBlock(TRY_PACKING, 0)
            );
        }
        return BONE_FROM_TO;
    }

    @Inject(method = "survivalTryHarvestBlock", at = @At("HEAD"), cancellable = true)
    private void clubLeftClickConversion(int x, int y, int z, int iFromSide,
                                         CallbackInfoReturnable<Boolean> cir) {
        World world = ((ItemInWorldManager) (Object) this).theWorld;
        EntityPlayerMP player = thisPlayerMP;

        int blockID = world.getBlockId(x, y, z);
        Block block = Block.blocksList[blockID];

        if (block == null) return;

        int metadata = world.getBlockMetadata(x, y, z);
        ItemStack heldItem = player.getCurrentEquippedItem();

        if (heldItem == null || ThisItem.isNot(ItemType.CLUB, heldItem)) return;

        boolean isBoneClub = ThisItem.is(ItemType.BONE, heldItem);
        Map<QualifiedBlock, QualifiedBlock> conversionMap = isBoneClub ? getBoneClubMap() : getWoodClubMap();

        QualifiedBlock from = new QualifiedBlock(blockID, metadata);
        QualifiedBlock to = conversionMap.get(from);

        if (to == null) return;

        // Require air above the clicked-on block in order to pack firm dirtlike into slab
        if (isBoneClub && ThisBlock.is(BlockType.FIRM_DIRTLIKE, block, metadata)) {
            if (WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, x, y + 1, z, 0)) return;
        }

        if (to.blockID == TRY_PACKING) { // triggered if clicked block was a packed earth slab
            int lowerNeighborID = world.getBlockId(x, y - 1, z);
            int lowerNeighborMetadata = world.getBlockMetadata(x, y - 1, z);
            Block lowerNeighbor = Block.blocksList[lowerNeighborID];

            if (lowerNeighbor == null) return;

            // Pack downward if the lower neighbor is a firm dirtlike cube
            if (ThisBlock.isAll(lowerNeighbor, lowerNeighborMetadata, BlockType.FIRM_DIRTLIKE, BlockType.CUBE)) {
                SwapContext lowerCtx = new SwapContext(heldItem, player, world, x, y - 1, z);
                world.setBlockToAir(x, y, z);
                convertBlock(
                        BTWBlocks.aestheticEarth.blockID,
                        AestheticOpaqueEarthBlock.SUBTYPE_PACKED_EARTH,
                        lowerCtx, PACKING_COST);
                cir.setReturnValue(true);
            }
            // Firm the loose dirtlike block below
            else if (ThisBlock.isAll(lowerNeighbor, lowerNeighborMetadata, BlockType.LOOSE_DIRTLIKE, BlockType.CUBE)) {
                SwapContext lowerCtx = new SwapContext(heldItem, player, world, x, y - 1, z);
                convertBlock(Block.dirt.blockID, 0, lowerCtx, FIRMING_COST);
                cir.setReturnValue(true);
            }
        }
        else { // Firm or pack the dirtlike block that was clicked
            SwapContext ctx = new SwapContext(heldItem, player, world, x, y, z);
            if (to.metadata == PACKED_EARTH) {
                convertBlock(to.blockID, to.metadata, ctx, PACKING_COST);
                cir.setReturnValue(true);
            }
            else {
                convertBlock(to.blockID, to.metadata, ctx, FIRMING_COST);
                cir.setReturnValue(true);
            }
        }
    }

    @Unique
    private static void convertBlock(int toBlockID, int toMetadata, SwapContext ctx, int damageToItem) {
        ctx.world.setBlockAndMetadataWithNotify(ctx.x, ctx.y, ctx.z, toBlockID, toMetadata);
        ctx.stack.damageItem(damageToItem, ctx.player);

        // Play tilled dirt effect client-side
        if (ctx.world.isRemote) {
            ctx.world.playAuxSFX(BTWEffectManager.DIRT_TILLING_EFFECT_ID, ctx.x, ctx.y, ctx.z, 0);
        }
    }
}