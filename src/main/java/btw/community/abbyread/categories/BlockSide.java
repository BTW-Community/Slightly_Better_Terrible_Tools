package btw.community.abbyread.categories;

public enum BlockSide {
    DOWN(0),
    UP(1),
    NORTH(2),
    SOUTH(3),
    WEST(4),
    EAST(5);

    private final int id;

    BlockSide(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static BlockSide fromId(int id) {
        for (BlockSide side : values()) {
            if (side.id == id) return side;
        }
        throw new IllegalArgumentException("Invalid block side id: " + id);
    }
}
