package academy.cashbackmerge.game;

import academy.cashbackmerge.domain.Board;
import academy.cashbackmerge.domain.Direction;
import academy.cashbackmerge.domain.Position;
import academy.cashbackmerge.domain.Tile;
import java.util.Arrays;

/**
 * Engine for applying moves to the game board.
 */
public final class SlideEngine {

    /**
     * Private constructor to prevent instantiation.
     */
    private SlideEngine() {}

    /**
     * Applies a move to the game board in the specified direction.
     *
     * @param board the game board to apply the move to
     * @param direction the direction of the move
     * @return the result of the move
     */
    public static SlideResult apply(Board board, Direction direction) {
        int linesToProcess = lineCount(board, direction);
        int lengthOfEachLine = lineLength(board, direction);

        Board updatedBoard = board;
        long scoreGained = 0L;
        boolean changed = false;

        for (int lineIndex = 0; lineIndex < linesToProcess; lineIndex++) {
            int[] originalLine = readLine(board, direction, lineIndex);
            MergedLine mergedLine = mergeLine(originalLine);
            int[] mergedValues = mergedLine.values();
            if (Arrays.equals(originalLine, mergedValues)) {
                continue;
            }
            changed = true;
            for (int indexInLine = 0; indexInLine < lengthOfEachLine; indexInLine++) {
                Position position = cellAt(board, direction, lineIndex, indexInLine);
                int value = mergedValues[indexInLine];
                updatedBoard = value == 0
                        ? updatedBoard.withoutTile(position)
                        : updatedBoard.withTile(position, Tile.ofPercent(value));
            }
            scoreGained += mergedLine.score();
        }
        return new SlideResult(updatedBoard, scoreGained, changed);
    }

    /**
     * Calculates the number of lines to process for a given direction.
     *
     * @param board the game board
     * @param direction the direction of the move
     * @return the number of lines to process
     */
    private static int lineCount(Board board, Direction direction) {
        return switch (direction) {
            case LEFT, RIGHT -> board.rows();
            case UP, DOWN -> board.cols();
        };
    }

    /**
     * Calculates the length of each line for a given direction.
     *
     * @param board the game board
     * @param direction the direction of the move
     * @return the length of each line
     */
    private static int lineLength(Board board, Direction direction) {
        return switch (direction) {
            case LEFT, RIGHT -> board.cols();
            case UP, DOWN -> board.rows();
        };
    }

    /**
     * Reads a line from the board for a given direction.
     *
     * @param board the game board
     * @param direction the direction of the move
     * @param lineIndex the index of the line to read
     * @return the values of the line
     */
    private static int[] readLine(Board board, Direction direction, int lineIndex) {
        int lengthOfLine = lineLength(board, direction);
        int[] values = new int[lengthOfLine];
        for (int indexInLine = 0; indexInLine < lengthOfLine; indexInLine++) {
            Position position = cellAt(board, direction, lineIndex, indexInLine);
            values[indexInLine] =
                    board.tileAt(position).map(Tile::cashbackPercent).orElse(0);
        }
        return values;
    }

    /**
     * Determines the cell position for a given line index and index in line.
     *
     * @param board the game board
     * @param direction the direction of the move
     * @param lineIndex the index of the line
     * @param indexInLine the index within the line
     * @return the position of the cell
     */
    private static Position cellAt(Board board, Direction direction, int lineIndex, int indexInLine) {
        return switch (direction) {
            case LEFT -> new Position(lineIndex, indexInLine);
            case RIGHT -> new Position(lineIndex, board.cols() - 1 - indexInLine);
            case UP -> new Position(indexInLine, lineIndex);
            case DOWN -> new Position(board.rows() - 1 - indexInLine, lineIndex);
        };
    }

    /**
     * Merges a line by combining tiles with the same value.
     *
     * @param originalLine the original line of tiles
     * @return the result of merging the line
     */
    private static MergedLine mergeLine(int[] originalLine) {
        int lengthOfLine = originalLine.length;
        int[] compactedValues = new int[lengthOfLine];
        int compactedCount = 0;
        for (int value : originalLine) {
            if (value != 0) {
                compactedValues[compactedCount++] = value;
            }
        }

        int[] mergedValues = new int[lengthOfLine];
        int writeIndex = 0;
        long score = 0L;
        int readIndex = 0;
        while (readIndex < compactedCount) {
            if (readIndex + 1 < compactedCount && compactedValues[readIndex] == compactedValues[readIndex + 1]) {
                int mergedValue = compactedValues[readIndex] * 2;
                mergedValues[writeIndex++] = mergedValue;
                score += mergedValue;
                readIndex += 2;
            } else {
                mergedValues[writeIndex++] = compactedValues[readIndex];
                readIndex++;
            }
        }
        return new MergedLine(mergedValues, score);
    }

    /**
     * Represents the result of merging a line.
     */
    private record MergedLine(int[] values, long score) {}
}
