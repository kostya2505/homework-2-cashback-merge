package academy.cashbackmerge.game;

import academy.cashbackmerge.domain.Board;
import java.util.Objects;

public record SlideResult(Board board, long scoreGained, boolean changed) {
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
