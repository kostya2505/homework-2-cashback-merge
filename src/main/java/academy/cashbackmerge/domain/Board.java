package academy.cashbackmerge.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Board {

    private static final int EMPTY = 0;

    private final int rows;
    private final int cols;
    private final int[] cells;

    public Board(int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Board size must be positive: " + rows + "x" + cols);
        }
        this.rows = rows;
        this.cols = cols;
        this.cells = new int[rows * cols];
    }

    private Board(int rows, int cols, int[] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;
    }

    public int rows() {
        return rows;
    }

    public int cols() {
        return cols;
    }

    public Optional<Tile> tileAt(Position p) {
        int v = cells[index(p)];
        return v == EMPTY ? Optional.empty() : Optional.of(Tile.ofPercent(v));
    }

    public boolean isEmpty(Position p) {
        return cells[index(p)] == EMPTY;
    }

    public Board withTile(Position p, Tile tile) {
        int[] copy = cells.clone();
        copy[index(p)] = tile.cashbackPercent();
        return new Board(rows, cols, copy);
    }

    public Board withoutTile(Position p) {
        int[] copy = cells.clone();
        copy[index(p)] = EMPTY;
        return new Board(rows, cols, copy);
    }

    public List<Position> emptyPositions() {
        List<Position> result = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r * cols + c] == EMPTY) {
                    result.add(new Position(r, c));
                }
            }
        }
        return result;
    }

    public List<Tile> tiles() {
        List<Tile> result = new ArrayList<>();
        for (int value : cells) {
            if (value != EMPTY) {
                result.add(Tile.ofPercent(value));
            }
        }
        return result;
    }

    public int maxTilePercent() {
        int max = EMPTY;
        for (int value : cells) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public boolean isFull() {
        for (int value : cells) {
            if (value == EMPTY) {
                return false;
            }
        }
        return true;
    }

    private int index(Position p) {
        if (p.row() >= rows || p.col() >= cols) {
            throw new IndexOutOfBoundsException("Position " + p + " is outside " + rows + "x" + cols + " board");
        }
        return p.row() * cols + p.col();
    }
}
