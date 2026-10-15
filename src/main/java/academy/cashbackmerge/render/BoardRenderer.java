package academy.cashbackmerge.render;

import academy.cashbackmerge.domain.Board;
import academy.cashbackmerge.domain.Position;
import academy.cashbackmerge.domain.Tile;
import java.util.Optional;

public final class BoardRenderer {
    private static final char EMPTY = '.';
    private static final char HORIZONTAL = '-';
    private static final char VERTICAL = '|';
    private static final char CORNER = '+';

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

    private String cellLabel(Board board, int r, int c) {
        Optional<Tile> tile = board.tileAt(new Position(r, c));
        return tile.map(t -> t.cashbackPercent() + "%").orElse(String.valueOf(EMPTY));
    }

    private int computeCellWidth(Board board) {
        int max = 1;
        for (int r = 0; r < board.rows(); r++) {
            for (int c = 0; c < board.cols(); c++) {
                max = Math.max(max, cellLabel(board, r, c).length());
            }
        }
        return max;
    }

    private String buildHorizontal(int cellWidth, int cols) {
        StringBuilder sb = new StringBuilder();
        sb.append(CORNER);
        for (int c = 0; c < cols; c++) {
            sb.repeat(String.valueOf(HORIZONTAL), cellWidth).append(CORNER);
        }
        return sb.toString();
    }

    private String pad(String value, int width) {
        int totalPadding = width - value.length();
        int left = totalPadding / 2;
        int right = totalPadding - left;
        return " ".repeat(left) + value + " ".repeat(right);
    }
}
