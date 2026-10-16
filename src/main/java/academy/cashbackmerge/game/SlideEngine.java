package academy.cashbackmerge.game;

import academy.cashbackmerge.domain.Board;
import academy.cashbackmerge.domain.Direction;
import academy.cashbackmerge.domain.Position;
import academy.cashbackmerge.domain.Tile;
import java.util.Arrays;

public final class SlideEngine {

    private SlideEngine() {}

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

    private static int lineCount(Board board, Direction direction) {
        return switch (direction) {
            case LEFT, RIGHT -> board.rows();
            case UP, DOWN -> board.cols();
        };
    }

    private static int lineLength(Board board, Direction direction) {
        return switch (direction) {
            case LEFT, RIGHT -> board.cols();
            case UP, DOWN -> board.rows();
        };
    }

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

    private static Position cellAt(Board board, Direction direction, int lineIndex, int indexInLine) {
        return switch (direction) {
            case LEFT -> new Position(lineIndex, indexInLine);
            case RIGHT -> new Position(lineIndex, board.cols() - 1 - indexInLine);
            case UP -> new Position(indexInLine, lineIndex);
            case DOWN -> new Position(board.rows() - 1 - indexInLine, lineIndex);
        };
    }

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

    private record MergedLine(int[] values, long score) {}
}
