package academy.cashbackmerge.mandatory.mr2;

import static org.assertj.core.api.Assertions.assertThat;

import academy.cashbackmerge.domain.Board;
import academy.cashbackmerge.domain.Direction;
import academy.cashbackmerge.domain.Position;
import academy.cashbackmerge.domain.Tile;
import academy.cashbackmerge.game.SlideEngine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Обязательные тесты: правила слияния плиток. */
@DisplayName("MR2. Слияние плиток")
class MergeRulesTest {

    @ParameterizedTest
    @CsvSource({
        "'1,1,.,.', '2,.,.,.'",
        "'1,1,1,.', '2,1,.,.'",
        "'1,1,1,1', '2,2,.,.'",
        "'2,2,4,4', '4,8,.,.'",
    })
    @DisplayName("Сдвиг влево: {0} -> {1}")
    void rowShiftsLeftAndMerges(String before, String expected) {
        Board boardBefore = boardWithRow(before);
        Board boardAfter = SlideEngine.apply(boardBefore, Direction.LEFT).board();

        assertThat(rowAsString(boardAfter)).isEqualTo(expected.trim());
    }

    @Test
    @DisplayName("Одна плитка не сливается дважды за ход: [1,1,2,.] -> [2,2,.,.]")
    void tileDoesNotMergeTwiceInOneMove() {
        Board boardAfter =
                SlideEngine.apply(boardWithRow("1,1,2,."), Direction.LEFT).board();

        assertThat(rowAsString(boardAfter)).isEqualTo("2,2,.,.");
    }

    @Test
    @DisplayName("Слияние работает во всех четырёх направлениях")
    void mergeWorksInAllDirections() {
        assertThat(slideRow("2,.,.,2", Direction.LEFT)).isEqualTo("4,.,.,.");
        assertThat(slideRow("2,.,.,2", Direction.RIGHT)).isEqualTo(".,.,.,4");

        assertThat(slideColumn("2,.,.,2", Direction.UP)).isEqualTo("4,.,.,.");
        assertThat(slideColumn("2,.,.,2", Direction.DOWN)).isEqualTo(".,.,.,4");
    }

    private static Board boardWithRow(String row) {
        int[] values = parseLine(row);
        int[][] matrix = new int[4][4];
        matrix[0] = values;
        return Board.of(matrix);
    }

    private static Board boardWithColumn(String column) {
        int[] values = parseLine(column);
        int[][] matrix = new int[4][4];
        for (int r = 0; r < values.length; r++) {
            matrix[r][0] = values[r];
        }
        return Board.of(matrix);
    }

    private static int[] parseLine(String line) {
        String[] tokens = line.split(",");
        int[] values = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i].trim();
            values[i] = ".".equals(token) || token.isEmpty() ? 0 : Integer.parseInt(token);
        }
        return values;
    }

    private static String slideRow(String row, Direction direction) {
        return rowAsString(SlideEngine.apply(boardWithRow(row), direction).board());
    }

    private static String rowAsString(Board board) {
        StringBuilder sb = new StringBuilder();
        for (int c = 0; c < board.cols(); c++) {
            if (c > 0) {
                sb.append(',');
            }
            int v = board.tileAt(new Position(0, c)).map(Tile::cashbackPercent).orElse(0);
            sb.append(v == 0 ? '.' : String.valueOf(v));
        }
        return sb.toString();
    }

    private static String slideColumn(String column, Direction direction) {
        Board result = SlideEngine.apply(boardWithColumn(column), direction).board();
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < 4; r++) {
            if (r > 0) {
                sb.append(',');
            }
            int v = result.tileAt(new Position(r, 0)).map(Tile::cashbackPercent).orElse(0);
            sb.append(v == 0 ? '.' : String.valueOf(v));
        }
        return sb.toString();
    }
}
