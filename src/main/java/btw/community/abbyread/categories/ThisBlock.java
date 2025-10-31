package btw.community.abbyread.categories;

import btw.block.blocks.*;
import net.minecraft.src.Block;
import btw.block.BTWBlocks;
import btw.block.blocks.GrassSlabBlock;
import net.minecraft.src.BlockGrass;

import java.util.HashSet;
import java.util.Set;

// Handles static and metadata-based block tags
@SuppressWarnings("ALL")
public class ThisBlock {

    // ===== Static block sets =====

    private static final Set<Block> LOOSE_DIRTLIKE_BLOCKS = Set.of(
            BTWBlocks.looseDirt,
            BTWBlocks.looseDirtSlab,
            BTWBlocks.looseSparseGrass,
            BTWBlocks.looseSparseGrassSlab
    );

    private static final Set<Block> DIRT_BLOCKS = Set.of(
            Block.dirt,
            BTWBlocks.dirtSlab,
            BTWBlocks.looseDirt,
            BTWBlocks.looseDirtSlab
    );

    private static final Set<Block> GRASS_BLOCKS = Set.of(
            Block.grass,
            BTWBlocks.grassSlab,
            BTWBlocks.looseSparseGrass,
            BTWBlocks.looseSparseGrassSlab
    );

    private static final Set<Block> DIRTLIKE_BLOCKS;
    static {
        Set<Block> temp = new HashSet<>();
        temp.addAll(DIRT_BLOCKS);
        temp.addAll(GRASS_BLOCKS);
        DIRTLIKE_BLOCKS = Set.copyOf(temp);
    }

    private static final Set<Block> FIRM_DIRTLIKE_BLOCKS;
    static {
        Set<Block> temp = new HashSet<>(DIRTLIKE_BLOCKS);
        temp.removeAll(LOOSE_DIRTLIKE_BLOCKS);
        FIRM_DIRTLIKE_BLOCKS = Set.copyOf(temp);
    }

    private static final Set<Block> CUBE_BLOCKS = Set.of(
            Block.cobblestone,
            Block.dirt,
            Block.grass,
            Block.gravel,
            Block.mycelium,
            Block.sand,
            Block.stone,
            BTWBlocks.aestheticEarth,
            BTWBlocks.creeperOysterBlock,
            BTWBlocks.looseBrick,
            BTWBlocks.looseCobblestone,
            BTWBlocks.looseDirt,
            BTWBlocks.looseNetherBrick,
            BTWBlocks.looseSnow,
            BTWBlocks.looseSparseGrass,
            BTWBlocks.looseStoneBrick,
            BTWBlocks.rottenFleshBlock,
            BTWBlocks.stoneBrickDoubleSlab,
            BTWBlocks.stoneDoubleSlab,
            BTWBlocks.solidSnow,
            BTWBlocks.spiderEyeBlock,
            BTWBlocks.wickerBlock
    );

    private static final Set<Block> SLAB_BLOCKS = Set.of(
            BTWBlocks.boneSlab,
            BTWBlocks.cobblestoneSlab,
            BTWBlocks.creeperOysterSlab,
            BTWBlocks.dirtSlab,
            BTWBlocks.grassSlab,
            BTWBlocks.looseBrickSlab,
            BTWBlocks.looseCobblestoneSlab,
            BTWBlocks.looseDirtSlab,
            BTWBlocks.looseNetherBrickSlab,
            BTWBlocks.looseSnowSlab,
            BTWBlocks.looseSparseGrassSlab,
            BTWBlocks.looseStoneBrickSlab,
            BTWBlocks.myceliumSlab,
            BTWBlocks.rottenFleshSlab,
            BTWBlocks.sandAndGravelSlab,
            BTWBlocks.solidSnowSlab,
            BTWBlocks.spiderEyeSlab,
            BTWBlocks.stoneBrickSlab,
            BTWBlocks.stoneSlab,
            BTWBlocks.wickerSlab,
            BTWBlocks.woolSlab,
            BTWBlocks.woolSlabTop
    );

    private static final Set<Block> SPARSE_BLOCKS = Set.of(
            BTWBlocks.looseSparseGrass,
            BTWBlocks.looseSparseGrassSlab
    );

    private static final Set<Block> LOG_BLOCKS = Set.of(
            BTWBlocks.oakChewedLog,
            BTWBlocks.oakLogSpike,

            BTWBlocks.birchChewedLog,
            BTWBlocks.birchLogSpike,

            BTWBlocks.spruceChewedLog,
            BTWBlocks.spruceLogSpike,

            BTWBlocks.jungleChewedLog,
            BTWBlocks.jungleLogSpike,

            BTWBlocks.bloodWoodLog,
            BTWBlocks.smolderingLog,
            Block.wood
    );

    private static final Set<Block> SHATTERABLE_BLOCKS = Set.of(
            Block.glass,
            Block.thinGlass,
            Block.glowStone,
            Block.ice,
            Block.redstoneLampActive,
            Block.redstoneLampIdle,
            BTWBlocks.lightBlockOn,
            BTWBlocks.lightBlockOff
    );

    private static final Set<Block> LOOSE_STONELIKE_BLOCKS = Set.of(
            BTWBlocks.looseBrick,
            BTWBlocks.looseBrickSlab,
            BTWBlocks.looseBrickStairs,
            BTWBlocks.looseCobblestone,
            BTWBlocks.looseCobblestoneSlab,
            BTWBlocks.looseCobblestoneStairs,
            BTWBlocks.looseStoneBrick,
            BTWBlocks.looseStoneBrickSlab,
            BTWBlocks.looseStoneBrickStairs,
            BTWBlocks.looseNetherBrick,
            BTWBlocks.looseNetherBrickSlab,
            BTWBlocks.looseNetherBrickStairs,
            BTWBlocks.looseCobbledDeepslateStairs,
            BTWBlocks.looseCobbledBlackstoneStairs,
            BTWBlocks.looseDeepslateBrickStairs,
            BTWBlocks.looseBlackstoneBrickStairs
    );

    private static final Set<Block> EASY_SOLID_STONELIKE_BLOCKS = Set.of(
            Block.stone,
            Block.cobblestone,
            Block.bedrock,
            Block.sandStone,
            Block.brick,
            Block.cobblestoneMossy,
            Block.obsidian,
            Block.stoneBrick,
            Block.whiteStone,
            Block.netherBrick,
            Block.stairsStoneBrick,
            Block.stairsCobblestone,
            Block.stairsBrick,
            Block.stairsSandStone,
            Block.stairsNetherBrick,
            Block.stairsNetherQuartz,
            Block.blockLapis,
            Block.blockGold,
            Block.blockNetherQuartz,
            Block.stoneDoubleSlab,
            Block.stoneSingleSlab,
            Block.netherFence,
            Block.cobblestoneWall,
            BTWBlocks.quartzSidingAndCorner,
            BTWBlocks.quartzMouldingAndDecorative,
            BTWBlocks.sandstoneSidingAndCorner,
            BTWBlocks.sandstoneMouldingAndDecorative,
            BTWBlocks.stoneSidingAndCorner,
            BTWBlocks.brickSidingAndCorner,
            BTWBlocks.brickMouldingAndDecorative,
            BTWBlocks.netherBrickSidingAndCorner,
            BTWBlocks.netherBrickMouldingAndDecorative,
            BTWBlocks.whiteStoneStairs,
            BTWBlocks.whiteStoneSidingAndCorner,
            BTWBlocks.whiteStoneMouldingAndDecroative,
            BTWBlocks.stoneBrickSidingAndCorner,
            BTWBlocks.stoneBrickMouldingAndDecorative,
            BTWBlocks.stoneMouldingAndDecorative,
            BTWBlocks.infestedStone,
            BTWBlocks.infestedCobblestone,
            BTWBlocks.infestedStoneBrick,
            BTWBlocks.infestedMossyStoneBrick,
            BTWBlocks.infestedCrackedStoneBrick,
            BTWBlocks.infestedChiseledStoneBrick
    );

    // ===== Public method =====

    public static Set<BlockType> of(Block block, int metadata) {
        Set<BlockType> tags = new HashSet<>();

        // --- Static tags ---
        if (CUBE_BLOCKS.contains(block)) tags.add(BlockType.CUBE);
        if (SLAB_BLOCKS.contains(block)) tags.add(BlockType.SLAB);
        if (LOG_BLOCKS.contains(block)) tags.add(BlockType.LOG);
        if (DIRT_BLOCKS.contains(block)) tags.add(BlockType.DIRT);
        if (DIRTLIKE_BLOCKS.contains(block)) tags.add(BlockType.DIRTLIKE);
        if (LOOSE_DIRTLIKE_BLOCKS.contains(block)) tags.add(BlockType.LOOSE_DIRTLIKE);
        if (FIRM_DIRTLIKE_BLOCKS.contains(block)) tags.add(BlockType.FIRM_DIRTLIKE);
        if (LOOSE_STONELIKE_BLOCKS.contains(block)) tags.add(BlockType.LOOSE_STONELIKE);
        if (EASY_SOLID_STONELIKE_BLOCKS.contains(block)) tags.add(BlockType.EASY_SOLID_STONELIKE);
        if (SHATTERABLE_BLOCKS.contains(block)) tags.add(BlockType.SHATTERABLE);

        if (block == Block.web || block == BTWBlocks.web) tags.add(BlockType.WEB);
        if (block == Block.sand ||
                (block == BTWBlocks.sandAndGravelSlab && (((SandAndGravelSlabBlock)block).getSubtypeFromMetadata(metadata)
                        == SandAndGravelSlabBlock.SUBTYPE_SAND))) tags.add(BlockType.SAND);
        if (block == Block.gravel ||
                (block == BTWBlocks.sandAndGravelSlab && (((SandAndGravelSlabBlock)block).getSubtypeFromMetadata(metadata)
                        == SandAndGravelSlabBlock.SUBTYPE_GRAVEL))) tags.add(BlockType.GRAVEL);
        if (block == Block.tallGrass) tags.add(BlockType.TALL_GRASS);

        // --- Metadata-dependent tags ---
        if (isDirt(block, metadata)) tags.add(BlockType.DIRT);
        if (isGrass(block, metadata)) tags.add(BlockType.GRASS);
        if (isSparse(block, metadata)) tags.add(BlockType.SPARSE);
        if (isFullyGrown(block, metadata)) tags.add(BlockType.FULLY_GROWN);
        if (isPackedEarth(block, metadata)) {
            tags.add(BlockType.PACKED_EARTH);
            tags.remove(BlockType.FIRM_DIRTLIKE);
        }

        return tags;
    }

    // ===== Metadata-dependent helper methods =====

    private static boolean isGrass(Block block, int metadata) {
        if (GRASS_BLOCKS.contains(block)) return true;
        if (block instanceof DirtSlabBlock) return metadata == DirtSlabBlock.SUBTYPE_GRASS;
        return false;
    }

    private static boolean isDirt(Block block, int metadata) {
        if (DIRT_BLOCKS.contains(block)) return true;
        if (block instanceof DirtSlabBlock) return metadata == DirtSlabBlock.SUBTYPE_DIRT;
        return false;
    }

    private static boolean isSparse(Block block, int metadata) {
        if (SPARSE_BLOCKS.contains(block)) return true;
        if (block instanceof BlockGrass) return ((BlockGrass) block).isSparse(metadata);
        if (block instanceof GrassSlabBlock) return ((GrassSlabBlock) block).isSparse(metadata);
        return false;
    }

    private static boolean isFullyGrown(Block block, int metadata) {
        if (isGrass(block, metadata)) {
            if (block instanceof BlockGrass) return !isSparse(block, metadata);
            if (block instanceof GrassSlabBlock) return !isSparse(block, metadata);
        }
        return false;
    }

    private static boolean isPackedEarth(Block block, int metadata) {
        if (block instanceof AestheticOpaqueEarthBlock)
            return metadata == AestheticOpaqueEarthBlock.SUBTYPE_PACKED_EARTH;
        // DirtSlabBlock.SUBTYPE_PACKED_EARTH gives 3, which is actually wrong.
        if (block instanceof DirtSlabBlock && metadata == 6) {
            return true;
        }
        return false;
    }

    // ===== Public API =====

    public static Set<BlockType> getTags(Block block, int metadata) {
        return of(block, metadata); // reuse your existing of() method
    }

    public static boolean is(BlockType tag, Block block, int metadata) {
        Set<BlockType> blockTags = getTags(block, metadata);
        return blockTags.contains(tag);
    }

    public static boolean isAnd(BlockType tag1, BlockType tag2, Block block, int metadata) {
        Set<BlockType> blockTags = getTags(block, metadata);
        return blockTags.contains(tag1) && blockTags.contains(tag2);
    }

    public static boolean isNot(BlockType tag, Block block, int metadata) {
        Set<BlockType> blockTags = getTags(block, metadata);
        return !blockTags.contains(tag);
    }

    public static boolean isNotAll(Block block, int metadata, BlockType... tags) {
        return  !isAll(block, metadata, tags);
    }

    public static boolean isNotAny(Block block, int metadata, BlockType... tags) {
        return  !isAny(block, metadata, tags);
    }

    public static boolean isAny(Block block, int metadata, BlockType... tags) {
        Set<BlockType> blockTags = getTags(block, metadata);
        for (BlockType tag : tags) {
            if (blockTags.contains(tag)) return true;
        }
        return false;
    }

    public static boolean isAll(Block block, int metadata, BlockType... tags) {
        Set<BlockType> blockTags = getTags(block, metadata);
        for (BlockType tag : tags) {
            if (!blockTags.contains(tag)) return false;
        }
        return true;
    }

    public static boolean isButNot(BlockType isType, BlockType notType, Block block, int metadata) {
        return is(isType, block, metadata) && !isAny(block, metadata, notType);
    }
}