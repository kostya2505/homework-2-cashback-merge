package academy.cashbackmerge.game;

import academy.cashbackmerge.domain.Board;
import java.util.Objects;

/**
 * Represents the result of applying a slide operation to a game board.
 */
public record SlideResult(Board board, long scoreGained, boolean changed) {

    /**
     * Constructs a new SlideResult instance.
     *
     * @param board the game board after the slide operation
     * @param scoreGained the score gained from the slide operation
     * @param changed indicates whether the slide operation changed the game state
     * @throws IllegalArgumentException if the board is null, scoreGained is negative, and changed is false while scoreGained is non-zero
     */
    public SlideResult {
        Objects.requireNonNull(board, "board");
        if (scoreGained < 0) {
            throw new IllegalArgumentException("Полученный счёт не может быть отрицательным: " + scoreGained);
        }
        if (!changed && scoreGained != 0) {
            throw new IllegalArgumentException("Непроизведённый сдвиг не может изменить счёт");
        }
    }
}
