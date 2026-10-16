package academy.cashbackmerge.game;

import academy.cashbackmerge.domain.GameState;

import java.util.Objects;

public record MoveResult(GameState state, boolean changed, long scoreGained) {
    public MoveResult {
        Objects.requireNonNull(state, "state");
        if (scoreGained < 0) {
            throw new IllegalArgumentException("Полученный счёт не может быть меньше нуля: " + scoreGained);
        }
        if (!changed && scoreGained != 0) {
            throw new IllegalArgumentException("Непроизведённый ход не может изменить счёт");
        }
    }

    public static MoveResult unchanged(GameState state) {
        return new MoveResult(state, false, 0L);
    }

    public static MoveResult changed(GameState state, long scoreGained) {
        return new MoveResult(state, true, scoreGained);
    }
}
