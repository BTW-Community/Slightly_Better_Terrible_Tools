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
public class BlockTags {

    // ===== Static block sets =====

    private static final Set<Block> LOOSE_BLOCKS = Set.of(
            BTWBlocks.looseDirt,
            BTWBlocks.looseDirtSlab,
            BTWBlocks.looseSparseGrass,
            BTWBlocks.looseSparseGrassSlab,
            BTWBlocks.sandAndGravelSlab,
            Block.sand,
            Block.gravel
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

    private static final Set<Block> CUBE_BLOCKS = Set.of(
            Block.cobblestone,
            Block.dirt,
            Block.grass,
            Block.gravel,
            Block.mycelium,
            Block.sand,
            Block.stone,
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

    // ===== Public method =====

    public static Set<BlockTag> of(Block block, int metadata) {
        Set<BlockTag> tags = new HashSet<>();

        // --- Static tags ---
        if (DIRTLIKE_BLOCKS.contains(block)) tags.add(BlockTag.DIRTLIKE);
        if (LOOSE_BLOCKS.contains(block)) tags.add(BlockTag.LOOSE);
        if (DIRT_BLOCKS.contains(block)) tags.add(BlockTag.DIRT);
        if (CUBE_BLOCKS.contains(block)) tags.add(BlockTag.CUBE);
        if (SLAB_BLOCKS.contains(block)) tags.add(BlockTag.SLAB);
        if (LOG_BLOCKS.contains(block)) tags.add(BlockTag.LOG);
        if (isFirm(block, metadata)) tags.add(BlockTag.FIRM);
        if (block == Block.web || block == BTWBlocks.web) tags.add(BlockTag.WEB);

        // --- Metadata-dependent tags ---
        if (isDirt(block, metadata)) tags.add(BlockTag.DIRT);
        if (isGrass(block, metadata)) tags.add(BlockTag.GRASS);
        if (isSparse(block, metadata)) tags.add(BlockTag.SPARSE);
        if (isFullyGrown(block, metadata)) tags.add(BlockTag.FULLY_GROWN);
        if (isPackedEarth(block, metadata)) tags.add(BlockTag.PACKED_EARTH);

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

    private static boolean isFirm(Block block, int metadata) {
        return DIRTLIKE_BLOCKS.contains(block) && !LOOSE_BLOCKS.contains(block);
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
        if (block instanceof DirtSlabBlock)
            return metadata == DirtSlabBlock.SUBTYPE_PACKED_EARTH;
        return false;
    }

    // ===== Public API =====

    public static Set<BlockTag> getTags(Block block, int metadata) {
        return of(block, metadata); // reuse your existing of() method
    }

    public static boolean is(Block block, int metadata, BlockTag tag) {
        Set<BlockTag> blockTags = getTags(block, metadata);
        return blockTags.contains(tag);
    }

    public static boolean isNot(Block block, int metadata, BlockTag tag) {
        Set<BlockTag> blockTags = getTags(block, metadata);
        return !blockTags.contains(tag);
    }

    public static boolean isNotAll(Block block, int metadata, BlockTag... tags) {
        return  !isAll(block, metadata, tags);
    }

    public static boolean isNotAny(Block block, int metadata, BlockTag... tags) {
        return  !isAny(block, metadata, tags);
    }

    public static boolean isAny(Block block, int metadata, BlockTag... tags) {
        Set<BlockTag> blockTags = getTags(block, metadata);
        for (BlockTag tag : tags) {
            if (blockTags.contains(tag)) return true;
        }
        return false;
    }

    public static boolean isAll(Block block, int metadata, BlockTag... tags) {
        Set<BlockTag> blockTags = getTags(block, metadata);
        for (BlockTag tag : tags) {
            if (!blockTags.contains(tag)) return false;
        }
        return true;
    }

    public static boolean isButNot(Block block, int metadata, BlockTag isTag, BlockTag... notTags) {
        return is(block, metadata, isTag) && !isAny(block, metadata, notTags);
    }
}
