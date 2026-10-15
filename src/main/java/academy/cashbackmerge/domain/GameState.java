package academy.cashbackmerge.domain;

import java.util.Objects;

public record GameState(Board board, int movesRemaining, long score) {
    public GameState {
        Objects.requireNonNull(board, "board");
        if (movesRemaining < 0) {
            throw new IllegalArgumentException("movesRemaining must be >= 0: " + movesRemaining);
        }
    }

    public GameState withBoard(Board newBoard) {
        return new GameState(newBoard, movesRemaining, score);
    }

    public GameState afterMove(Board newBoard, long additionalScore) {
        if (movesRemaining == 0) {
            throw new IllegalStateException("No moves remaining");
        }
        return new GameState(newBoard, movesRemaining - 1, score + additionalScore);
    }
}
