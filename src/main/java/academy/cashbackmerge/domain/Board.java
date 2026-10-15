package academy.cashbackmerge.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/** Represents the game board with a grid of tiles. */
public final class Board {

    /** The number of cashback percent of an empty cell. */
    private static final int EMPTY = 0;

    private final int rows;
    private final int cols;
    private final int[] cells;

    /**
     * Constructs a new Board instance.
     *
     * @param rows the number of rows in the board, must be greater than 0
     * @param cols the number of columns in the board, must be greater than 0
     * @throws IllegalArgumentException if rows or cols is not greater than 0
     */
    public Board(int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Размер поля должен быть положительным: " + rows + "x" + cols);
        }
        this.rows = rows;
        this.cols = cols;
        this.cells = new int[rows * cols];
    }

    /**
     * Constructs a new Board instance.
     *
     * @param rows the number of rows in the board, must be greater than 0
     * @param cols the number of columns in the board, must be greater than 0
     * @throws IllegalArgumentException if rows or cols is not greater than 0
     */
    private Board(int rows, int cols, int[] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;
    }

    /**
     * Retrieves the number of rows on the board.
     *
     * @return the number of rows on the board
     */
    public int rows() {
        return rows;
    }

    /**
     * Retrieves the number of columns on the board.
     *
     * @return the number of columns on the board
     */
    public int cols() {
        return cols;
    }

    /**
     * Retrieves the tile at the specified position.
     *
     * @param p the position on the board
     * @return an Optional containing the tile at the specified position, or an empty Optional if the position is empty
     */
    public Optional<Tile> tileAt(Position p) {
        int v = cells[index(p)];
        return v == EMPTY ? Optional.empty() : Optional.of(Tile.ofPercent(v));
    }

    /**
     * Checks if the specified position is empty.
     *
     * @param p the position on the board
     * @return true if the specified position is empty, false otherwise
     */
    public boolean isEmpty(Position p) {
        return cells[index(p)] == EMPTY;
    }

    /**
     * Creates a new board with the specified tile at the specified position.
     *
     * @param p the position on the board
     * @param tile the tile to place at the specified position
     * @return a new Board instance with the specified tile placed
     */
    public Board withTile(Position p, Tile tile) {
        int[] copy = cells.clone();
        copy[index(p)] = tile.cashbackPercent();
        return new Board(rows, cols, copy);
    }

    /**
     * Creates a new board without the tile at the specified position.
     *
     * @param p the position on the board
     * @return a new Board instance without the tile at the specified position
     */
    public Board withoutTile(Position p) {
        int[] copy = cells.clone();
        copy[index(p)] = EMPTY;
        return new Board(rows, cols, copy);
    }

    /**
     * Retrieves a list of empty positions on the board.
     *
     * @return a list of empty positions on the board
     */
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

    /**
     * Retrieves a list of tiles on the board.
     *
     * @return a list of tiles on the board
     */
    public List<Tile> tiles() {
        List<Tile> result = new ArrayList<>();
        for (int value : cells) {
            if (value != EMPTY) {
                result.add(Tile.ofPercent(value));
            }
        }
        return result;
    }

    /**
     * Retrieves the maximum cashback percentage of tiles on the board.
     *
     * @return the maximum cashback percentage of tiles on the board
     */
    public int maxTilePercent() {
        int max = EMPTY;
        for (int value : cells) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    /**
     * Checks if the board is full.
     *
     * @return true if the board is full, false otherwise
     */
    public boolean isFull() {
        for (int value : cells) {
            if (value == EMPTY) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retrieves the index of a cell at the specified position.
     *
     * @param p the position on the board
     * @return the index of the cell at the specified position
     * @throws IndexOutOfBoundsException if the position is outside the board
     */
    private int index(Position p) {
        if (p.row() >= rows || p.col() >= cols) {
            throw new IndexOutOfBoundsException("Позиция " + p + " за пределами поля: " + rows + "x" + cols + " board");
        }
        return p.row() * cols + p.col();
    }

    /**
     * Checks for equality with another object.
     *
     * @param o the object to compare with
     * @return true if this board is equal to the other object, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Board other)) {
            return false;
        }
        return rows == other.rows && cols == other.cols && Arrays.equals(cells, other.cells);
    }

    /**
     * Retrieves the hash code of this board.
     *
     * @return the hash code of this board
     */
    @Override
    public int hashCode() {
        int result = Integer.hashCode(rows);
        result = 31 * result + Integer.hashCode(cols);
        result = 31 * result + Arrays.hashCode(cells);
        return result;
    }
}
