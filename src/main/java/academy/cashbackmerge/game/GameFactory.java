package academy.cashbackmerge.game;

import academy.cashbackmerge.config.GameConfig;
import academy.cashbackmerge.domain.Board;
import academy.cashbackmerge.domain.GameState;
import academy.cashbackmerge.spawn.WeightedSpawner;
import java.util.random.RandomGenerator;

/** Factory class for creating initial game states. */
public final class GameFactory {

    /** The default number of initial tiles to place on the board. */
    public static final int DEFAULT_INITIAL_TILES = 2;

    /** Private constructor to prevent instantiation. */
    private GameFactory() {}

    /**
     * Creates an initial game state with the default number of initial tiles.
     *
     * @param config the game configuration
     * @param random the random number generator to use for tile placement
     * @return a new game state
     * @throws IllegalArgumentException if the initial tiles exceed the board capacity
     */
    public static GameState createInitial(GameConfig config, RandomGenerator random) {
        return createInitial(config, random, DEFAULT_INITIAL_TILES);
    }

    /**
     * Creates an initial game state with the specified number of initial tiles.
     *
     * @param config the game configuration
     * @param random the random number generator to use for tile placement
     * @param initialTiles the number of initial tiles to place on the board, must be non-negative
     * @return a new game state
     * @throws IllegalArgumentException if the initial tiles exceed the board capacity or if initialTiles is negative
     */
    public static GameState createInitial(GameConfig config, RandomGenerator random, int initialTiles) {
        if (initialTiles < 0) {
            throw new IllegalArgumentException("Исходные плитки должны быть больше нуля: " + initialTiles);
        }
        Board board = new Board(config.rows(), config.cols());
        if (initialTiles > board.rows() * board.cols()) {
            throw new IllegalArgumentException("Исходные плитки превосходят вместимость поля");
        }
        WeightedSpawner spawner = new WeightedSpawner(config.spawnRules());
        for (int i = 0; i < initialTiles; i++) {
            board = spawner.spawnRandomTile(board, random);
        }
        return new GameState(board, config.movesLimit(), 0L);
    }
}
