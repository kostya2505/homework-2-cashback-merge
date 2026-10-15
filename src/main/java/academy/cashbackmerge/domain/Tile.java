package academy.cashbackmerge.domain;

import org.jspecify.annotations.NonNull;

/** Represents a tile on the game board with a specific cashback percentage. */
public record Tile(int cashbackPercent) implements Comparable<Tile> {

    /**
     * Constructs a new Tile instance.
     *
     * @param cashbackPercent the cashback percentage of the tile, must be between 0 and 100
     * @throws IllegalArgumentException if the cashback percentage is out of the valid range
     */
    public Tile {
        if (cashbackPercent <= 0) {
            throw new IllegalArgumentException("Процент кэшбэка должен быть положительным: " + cashbackPercent);
        }
    }

    /**
     * Creates a Tile instance from a cashback percentage.
     *
     * @param cashbackPercent the cashback percentage to convert to a Tile
     * @return a new Tile instance with the specified cashback percentage
     */
    public static Tile ofPercent(int cashbackPercent) {
        return new Tile(cashbackPercent);
    }

    /**
     * Checks if this tile can merge with another tile.
     *
     * @param other the other tile to check for merging
     * @return true if this tile can merge with the other tile, false otherwise
     */
    public boolean canMergeWith(Tile other) {
        return other != null && other.cashbackPercent == this.cashbackPercent;
    }

    /**
     * Merges this tile with another tile.
     *
     * @param other the other tile to merge with
     * @return a new Tile instance representing the merged tile
     * @throws IllegalArgumentException if this tile cannot merge with the other tile
     */
    public Tile mergeWith(Tile other) {
        if (!canMergeWith(other)) {
            throw new IllegalArgumentException("Нельзя объединить " + this + " с " + other);
        }
        return new Tile(cashbackPercent * 2);
    }

    /**
     * Compares this tile with another tile based on their cashback percentages.
     *
     * @param o the other tile to compare with
     * @return a negative integer, zero, or a positive integer as this tile is less than, equal to, or greater than the
     *     other tile
     */
    @Override
    public int compareTo(Tile o) {
        return Integer.compare(cashbackPercent, o.cashbackPercent);
    }

    /**
     * Returns a string representation of this tile.
     *
     * @return a string representation of this tile
     */
    @Override
    public @NonNull String toString() {
        return cashbackPercent + "%";
    }
}
