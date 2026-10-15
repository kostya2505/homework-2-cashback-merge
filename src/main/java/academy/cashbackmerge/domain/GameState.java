package academy.cashbackmerge.domain;

import java.util.Objects;

/** Represents the state of the game, including the current board, remaining moves, and score. */
public record GameState(Board board, int movesRemaining, long score) {

    /**
     * Constructs a new GameState instance.
     *
     * @param board the current state of the board
     * @param movesRemaining the number of moves remaining in the game
     * @param score the current score of the player
     * @throws NullPointerException if the board is null
     * @throws IllegalArgumentException if movesRemaining is negative
     */
    public GameState {
        Objects.requireNonNull(board, "board");
        if (movesRemaining < 0) {
            throw new IllegalArgumentException("Оставшееся количество ходов не может быть отрицательным: " + movesRemaining);
        }
    }

    /**
     * Creates a new game state with the specified board.
     *
     * @param newBoard the new board to use
     * @return a new game state with the updated board
     */
    public GameState withBoard(Board newBoard) {
        return new GameState(newBoard, movesRemaining, score);
    }

    /**
     * Creates a new game state after a move, including the new board and additional score.
     *
     * @param newBoard the new board after the move
     * @param additionalScore the score gained from the move
     * @return a new game state with the updated board and score
     * @throws IllegalStateException if no moves remain
     */
    public GameState afterMove(Board newBoard, long additionalScore) {
        if (movesRemaining == 0) {
            throw new IllegalStateException("Не осталось доступных ходов");
        }
        return new GameState(newBoard, movesRemaining - 1, score + additionalScore);
    }
}
