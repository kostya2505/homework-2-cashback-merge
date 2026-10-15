package academy.cashbackmerge.domain;

public record Tile(int cashbackPercent) implements Comparable<Tile> {
    public Tile {
        if (cashbackPercent <= 0) {
            throw new IllegalArgumentException("cashbackPercent must be positive: " + cashbackPercent);
        }
    }

    public static Tile ofPercent(int percent) {
        return new Tile(percent);
    }

    public boolean canMergeWith(Tile other) {
        return other != null && other.cashbackPercent == this.cashbackPercent;
    }

    public Tile mergeWith(Tile other) {
        if (!canMergeWith(other)) {
            throw new IllegalArgumentException("Cannot merge " + this + " with " + other);
        }
        return new Tile(cashbackPercent * 2);
    }

    @Override
    public int compareTo(Tile o) {
        return Integer.compare(cashbackPercent, o.cashbackPercent);
    }

    @Override
    public String toString() {
        return cashbackPercent + "%";
    }
}
