package btw.community.abbyread.categories;

import btw.block.blocks.*;
import net.minecraft.src.Block;
import btw.block.BTWBlocks;
import btw.block.blocks.GrassSlabBlock;
import net.minecraft.src.BlockGrass;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Handles static and metadata-based block categories
public class BlockCategories {

    // ===== Static block sets =====

    private static final Set<Block> LOOSE_BLOCKS = Set.of(
            BTWBlocks.looseDirt,
            BTWBlocks.looseDirtSlab,
            BTWBlocks.looseSparseGrass,
            BTWBlocks.looseSparseGrassSlab,
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
            Block.dirt,
            BTWBlocks.looseDirt,
            Block.grass,
            BTWBlocks.looseSparseGrass
    );

    private static final Set<Block> SPARSE_BLOCKS = Set.of(
            BTWBlocks.looseSparseGrass,
            BTWBlocks.looseSparseGrassSlab
    );

    // ===== Public method =====

    public static Set<BlockCategory> of(Block block, int metadata) {
        Set<BlockCategory> categories = new HashSet<>();

        // --- Static categories ---
        if (DIRTLIKE_BLOCKS.contains(block)) categories.add(BlockCategory.DIRTLIKE);
        if (LOOSE_BLOCKS.contains(block)) categories.add(BlockCategory.LOOSE);
        if (DIRT_BLOCKS.contains(block)) categories.add(BlockCategory.DIRT);
        if (CUBE_BLOCKS.contains(block)) categories.add(BlockCategory.CUBE);
        if (SLAB_BLOCKS.contains(block)) categories.add(BlockCategory.SLAB);

        // --- Metadata-dependent categories ---
        if (isDirt(block, metadata)) categories.add(BlockCategory.DIRT);
        if (isGrass(block, metadata)) categories.add(BlockCategory.GRASS);
        if (isSparse(block, metadata)) categories.add(BlockCategory.SPARSE);
        if (isPackedEarth(block, metadata)) categories.add(BlockCategory.PACKED_EARTH);

        return categories;
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

    private static boolean isPackedEarth(Block block, int metadata) {
        if (block instanceof AestheticOpaqueEarthBlock)
            return metadata == AestheticOpaqueEarthBlock.SUBTYPE_PACKED_EARTH;
        if (block instanceof DirtSlabBlock)
            return metadata == DirtSlabBlock.SUBTYPE_PACKED_EARTH;
        return false;
    }

    // ===== Somewhat confusing hacks =====

    private static final Logger LOGGER = LogManager.getLogger("BlockCategories");

    // Automatically populate the SLAB_BLOCKS Set by referencing the block name
    private static final Set<Block> SLAB_BLOCKS;
    static {
        Set<Block> temp = new HashSet<>();
        Field[] fields = BTWBlocks.class.getFields(); // all public static fields

        for (Field field : fields) {
            try {
                Object value = field.get(null); // static field
                if (value instanceof Block && field.getName().contains("Slab")) {
                    temp.add((Block) value);
                }
            } catch (IllegalAccessException e) {
                LOGGER.error("Failed to access BTWBlocks field: {}", field.getName(), e);
            }
        }
        SLAB_BLOCKS = Set.copyOf(temp); // immutable
    }
}
