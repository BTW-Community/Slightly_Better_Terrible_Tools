package btw.community.abbyread.categories;

public enum BlockSide {
    DOWN(0),
    UP(1),
    NORTH(2),
    SOUTH(3),
    WEST(4),
    EAST(5);

    private final int value;

    BlockSide(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static BlockSide fromId(int value) {
        for (BlockSide side : values()) {
            if (side.value == value) return side;
        }
        throw new IllegalArgumentException("Invalid block side value: " + value);
    }
}
