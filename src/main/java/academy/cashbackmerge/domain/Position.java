package academy.cashbackmerge.domain;

/** Represents a tile on the game board with a specific cashback percentage. */
public record Position(int row, int col) {
    /**
     * Constructs a new Position instance.
     *
     * @param row the row coordinate of the position, must be non-negative
     * @param col the column coordinate of the position, must be non-negative
     * @throws IllegalArgumentException if either row or col is negative
     */
    public Position {
        if (row < 0 || col < 0) {
            throw new IllegalArgumentException("Координаты позиции должны быть положительными: " + row + "," + col);
        }
    }
}
