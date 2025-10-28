package btw.community.abbyread.categories;

import java.util.Objects;

public class QualifiedBlock {
    public final int blockID;
    public final int metadata;

    public QualifiedBlock(int blockID, int metadata) {
        this.blockID = blockID;
        this.metadata = metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QualifiedBlock qb)) return false;
        return blockID == qb.blockID && metadata == qb.metadata;
    }

    @Override
    public int hashCode() {
        return Objects.hash(blockID, metadata);
    }

    @Override
    public String toString() {
        return "QualifiedBlock{" + blockID + ":" + metadata + "}";
    }
}
