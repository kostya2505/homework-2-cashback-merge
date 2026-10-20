package academy.cashbackmerge.mandatory.mr3;

import static org.assertj.core.api.Assertions.assertThat;

import academy.cashbackmerge.domain.Board;
import academy.cashbackmerge.domain.Direction;
import academy.cashbackmerge.domain.Position;
import academy.cashbackmerge.domain.Tile;
import academy.cashbackmerge.game.SlideEngine;
import academy.cashbackmerge.game.SlideResult;
import java.util.Random;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GenerativeTests {

    @ParameterizedTest
    @ValueSource(longs = {1, 42, 100, 999, 1234, 98765})
    void sumOfTileValuesIsPreservedDuringSlideAndMerge(long seed) {
        Board board = randomBoard(seed, 4, 4);
        int sumBefore = sumOf(board);
        for (Direction d : Direction.values()) {
            int sumAfter = sumOf(SlideEngine.apply(board, d).board());
            assertThat(sumAfter).as("sum for %s", d).isEqualTo(sumBefore);
        }
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 42, 100, 999, 1234, 98765})
    void numberOfTilesNeverIncreasesDuringSlide(long seed) {
        Board board = randomBoard(seed, 4, 4);
        int before = board.tiles().size();
        for (Direction d : Direction.values()) {
            int after = SlideEngine.apply(board, d).board().tiles().size();
            assertThat(after).as("tiles for %s", d).isLessThanOrEqualTo(before);
        }
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 42, 100, 999, 1234, 98765})
    void noOutputTileExceedsTwiceTheMaxInputInItsLine(long seed) {
        Board board = randomBoard(seed, 4, 4);
        for (Direction d : Direction.values()) {
            Board after = SlideEngine.apply(board, d).board();
            assertNoDoubleMergePerLine(board, after, d);
        }
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 42, 100, 999, 12345, 98765})
    void slideIsIdempotentWhenNoMergesHappen(long seed) {
        Board board = randomBoard(seed, 4, 4);
        for (Direction d : Direction.values()) {
            SlideResult first = SlideEngine.apply(board, d);
            if (first.scoreGained() != 0) {
                continue;
            }
            SlideResult second = SlideEngine.apply(first.board(), d);
            assertThat(second.scoreGained())
                    .as("second slide %s after no-op", d)
                    .isZero();
            assertThat(second.board()).as("board stable after no-op %s", d).isEqualTo(first.board());
        }
    }

    private static void assertNoDoubleMergePerLine(Board input, Board output, Direction d) {
        if (d == Direction.LEFT || d == Direction.RIGHT) {
            for (int r = 0; r < input.rows(); r++) {
                checkLineMax(maxInRow(input, r), maxInRow(output, r), "row " + r + " / " + d);
            }
        } else {
            for (int c = 0; c < input.cols(); c++) {
                checkLineMax(maxInColumn(input, c), maxInColumn(output, c), "column " + c + " / " + d);
            }
        }
    }

    private static void checkLineMax(int maxInput, int maxOutput, String label) {
        if (maxInput == 0) {
            return;
        }
        assertThat(maxOutput)
                .as("%s: max output (%d) must be <= 2 * max input (%d)", label, maxOutput, maxInput)
                .isLessThanOrEqualTo(2 * maxInput);
    }

    private static Board randomBoard(long seed, int rows, int cols) {
        Random random = new Random(seed);
        int[][] matrix = new int[rows][cols];
        int[] pool = {1, 2, 4, 8, 16};
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                matrix[r][c] = random.nextInt(10) < 3 ? 0 : pool[random.nextInt(pool.length)];
            }
        }
        return Board.of(matrix);
    }

    private static int maxInRow(Board board, int r) {
        int max = 0;
        for (int c = 0; c < board.cols(); c++) {
            max = Math.max(max, valueAt(board, r, c));
        }
        return max;
    }

    private static int maxInColumn(Board board, int c) {
        int max = 0;
        for (int r = 0; r < board.rows(); r++) {
            max = Math.max(max, valueAt(board, r, c));
        }
        return max;
    }

    private static int sumOf(Board board) {
        return board.tiles().stream().mapToInt(Tile::cashbackPercent).sum();
    }

    private static int valueAt(Board board, int r, int c) {
        return board.tileAt(new Position(r, c)).map(Tile::cashbackPercent).orElse(0);
    }
}
