package academy.cashbackmerge.game;

import academy.cashbackmerge.domain.GameState;
import java.util.Objects;

/** Represents the result of a move in the game. */
public record MoveResult(GameState state, boolean changed, long scoreGained) {

    /**
     * Constructs a new MoveResult instance.
     *
     * @param state the current game state
     * @param changed indicates whether the move changed the game state
     * @param scoreGained the score gained from the move
     * @throws IllegalArgumentException if the state is null, scoreGained is negative, and changed is false while
     *     scoreGained is non-zero
     */
    public MoveResult {
        Objects.requireNonNull(state, "state");
        if (scoreGained < 0) {
            throw new IllegalArgumentException("Полученный счёт не может быть меньше нуля: " + scoreGained);
        }
        if (!changed && scoreGained != 0) {
            throw new IllegalArgumentException("Непроизведённый ход не может изменить счёт");
        }
    }

    /**
     * Creates a MoveResult indicating that the move was not changed.
     *
     * @param state the current game state
     * @return the MoveResult
     */
    public static MoveResult unchanged(GameState state) {
        return new MoveResult(state, false, 0L);
    }

    /**
     * Creates a MoveResult indicating that the move was changed.
     *
     * @param state the current game state
     * @param scoreGained the score gained from the move
     * @return the MoveResult
     */
    public static MoveResult changed(GameState state, long scoreGained) {
        return new MoveResult(state, true, scoreGained);
    }
}
