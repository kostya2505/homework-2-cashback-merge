package academy.cashbackmerge.mandatory.mr2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import academy.cashbackmerge.config.GameConfig;
import academy.cashbackmerge.config.Probability;
import academy.cashbackmerge.config.SpawnRule;
import academy.cashbackmerge.domain.Board;
import academy.cashbackmerge.domain.Direction;
import academy.cashbackmerge.domain.GameState;
import academy.cashbackmerge.domain.Position;
import academy.cashbackmerge.domain.Tile;
import academy.cashbackmerge.game.GameEngine;
import academy.cashbackmerge.game.MoveResult;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: движок ходов — что считается ходом и что после него происходит. */
@DisplayName("MR2. Движок ходов")
class MoveEngineTest {

    private static final SpawnRule ALWAYS_ONE = new SpawnRule(Tile.ofPercent(1), Probability.parse("1.0"));

    private static GameConfig configWithMoves(int movesLimit) {
        return new GameConfig(4, 4, movesLimit, List.of(ALWAYS_ONE));
    }

    @Test
    @DisplayName("Невалидный ход не меняет поле, не тратит ход и не создаёт плитку")
    void invalidMoveChangesNothing() {
        GameEngine engine = new GameEngine(configWithMoves(5), new Random(1L));

        Board boardBefore = Board.of(new int[][] {
            {1, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0}
        });
        GameState state = new GameState(boardBefore, 5, 0L);

        MoveResult boardAfter = engine.applyMove(state, Direction.LEFT);

        assertThat(boardAfter.changed()).isFalse();
        assertThat(boardAfter.state()).isSameAs(state);
        assertThat(boardAfter.state().board()).isSameAs(boardBefore);
        assertThat(boardAfter.state().movesRemaining()).isEqualTo(5);
        assertThat(boardAfter.state().score()).isZero();
        assertThat(boardAfter.state().board().tiles()).hasSize(1);
    }

    @Test
    @DisplayName("После валидного хода появляется ровно одна новая плитка")
    void validMoveSpawnsExactlyOneTile() {
        GameEngine engine = new GameEngine(configWithMoves(5), new Random(1L));

        Board boardBefore = Board.of(new int[][] {
            {1, 1, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0}
        });
        GameState state = new GameState(boardBefore, 5, 0L);

        MoveResult boardAfter = engine.applyMove(state, Direction.LEFT);

        assertThat(boardAfter.changed()).isTrue();
        assertThat(boardAfter.state().board().tiles()).hasSize(2);
        assertThat(boardAfter.state().board().tileAt(new Position(0, 0)))
                .map(Tile::cashbackPercent)
                .contains(2);
    }

    @Test
    @DisplayName("После N валидных ходов игра завершена")
    void gameEndsAfterMovesLimit() {
        GameEngine engine = new GameEngine(configWithMoves(2), new Random(1L));

        Board board = Board.of(new int[][] {
            {0, 0, 1, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0}
        });
        GameState state = new GameState(board, 2, 0L);

        state = engine.applyMove(state, Direction.LEFT).state();
        assertThat(state.movesRemaining()).isEqualTo(1);

        MoveResult second = engine.applyMove(state, Direction.RIGHT);
        assertThat(second.changed()).isTrue();
        state = second.state();
        assertThat(state.movesRemaining()).isZero();

        GameState finalState = state;
        assertThatThrownBy(() -> engine.applyMove(finalState, Direction.LEFT))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Достижение плитки 32% не заканчивает игру — доигрываем до лимита ходов")
    void reachingMaxTileDoesNotEndGame() {
        GameEngine engine = new GameEngine(configWithMoves(5), new Random(1L));

        Board boardBefore = Board.of(new int[][] {
            {16, 16, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0}
        });
        GameState state = new GameState(boardBefore, 5, 0L);

        MoveResult boardAfter = engine.applyMove(state, Direction.LEFT);

        assertThat(boardAfter.changed()).isTrue();
        assertThat(boardAfter.state().board().maxTilePercent()).isEqualTo(32);
        assertThat(boardAfter.state().movesRemaining()).isEqualTo(4);
        assertThat(boardAfter.state().movesRemaining()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Score считается по правилам и растёт только при слияниях")
    void scoreIsCalculatedOnMerges() {
        GameEngine engine = new GameEngine(configWithMoves(10), new Random(1L));

        GameState mergeState = new GameState(
                Board.of(new int[][] {
                    {2, 2, 4, 4},
                    {0, 0, 0, 0},
                    {0, 0, 0, 0},
                    {0, 0, 0, 0}
                }),
                10,
                0L);

        MoveResult merged = engine.applyMove(mergeState, Direction.LEFT);
        assertThat(merged.scoreGained()).isEqualTo(12L);
        assertThat(merged.state().score()).isEqualTo(12L);

        GameEngine otherEngine = new GameEngine(configWithMoves(10), new Random(2L));
        GameState slideState = new GameState(
                Board.of(new int[][] {
                    {0, 2, 0, 0},
                    {0, 2, 0, 0},
                    {0, 0, 0, 0},
                    {0, 0, 0, 0}
                }),
                10,
                0L);

        MoveResult missed = otherEngine.applyMove(slideState, Direction.LEFT);
        assertThat(missed.changed()).isTrue();
        assertThat(missed.scoreGained()).isZero();
        assertThat(missed.state().score()).isZero();
    }
}
