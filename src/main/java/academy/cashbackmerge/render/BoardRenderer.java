package academy.cashbackmerge.render;

import academy.cashbackmerge.domain.Board;
import academy.cashbackmerge.domain.Position;
import academy.cashbackmerge.domain.Tile;
import java.util.Optional;

/** Renders a game board to a string representation. */
public final class BoardRenderer {

    /** The character representing an empty cell. */
    private static final char EMPTY = '.';

    /** The character representing a horizontal line in the board. */
    private static final char HORIZONTAL = '-';

    /** The character representing a vertical line in the board. */
    private static final char VERTICAL = '|';

    /** The character representing a corner in the board. */
    private static final char CORNER = '+';

    /**
     * Renders the board to a string.
     *
     * @param board the board to render
     * @return a string representation of the board
     */
    public String render(Board board) {
        int cellWidth = computeCellWidth(board);
        String horizontal = buildHorizontal(cellWidth, board.cols());

        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < board.rows(); r++) {
            sb.append(horizontal).append('\n');
            for (int c = 0; c < board.cols(); c++) {
                sb.append(VERTICAL).append(pad(cellLabel(board, r, c), cellWidth));
            }
            sb.append(VERTICAL).append('\n');
        }
        sb.append(horizontal);
        return sb.toString();
    }

    /**
     * Retrieves the label for a cell at a specified position.
     *
     * @param board the board
     * @param r the row index of the cell
     * @param c the column index of the cell
     * @return the label for the cell, or an empty string if the cell is empty
     */
    private String cellLabel(Board board, int r, int c) {
        Optional<Tile> tile = board.tileAt(new Position(r, c));
        return tile.map(t -> t.cashbackPercent() + "%").orElse(String.valueOf(EMPTY));
    }

    /**
     * Computes the width of the cells based on the contents of the board.
     *
     * @param board the board
     * @return the width of the cells
     */
    private int computeCellWidth(Board board) {
        int max = 1;
        for (int r = 0; r < board.rows(); r++) {
            for (int c = 0; c < board.cols(); c++) {
                max = Math.max(max, cellLabel(board, r, c).length());
            }
        }
        return max;
    }

    /**
     * Builds a horizontal line for the board.
     *
     * @param cellWidth the width of the cells
     * @param cols the number of columns in the board
     * @return a string representing the horizontal line
     */
    private String buildHorizontal(int cellWidth, int cols) {
        StringBuilder sb = new StringBuilder();
        sb.append(CORNER);
        for (int c = 0; c < cols; c++) {
            sb.repeat(String.valueOf(HORIZONTAL), cellWidth).append(CORNER);
        }
        return sb.toString();
    }

    /**
     * Pads a string to the specified width.
     *
     * @param value the string to pad
     * @param width the width to pad to
     * @return the padded string
     */
    private String pad(String value, int width) {
        int totalPadding = width - value.length();
        int left = totalPadding / 2;
        int right = totalPadding - left;
        return " ".repeat(left) + value + " ".repeat(right);
    }
}
