package academy.cashbackmerge.domain;

public record Position(int row, int col) {
    public Position {
        if (row < 0 || col < 0) {
            throw new IllegalArgumentException("Position coordinates must be non-negative: " + row + "," + col);
        }
    }
}
