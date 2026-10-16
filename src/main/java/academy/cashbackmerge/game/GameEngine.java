package academy.cashbackmerge.game;

import academy.cashbackmerge.config.GameConfig;
import academy.cashbackmerge.domain.Board;
import academy.cashbackmerge.domain.Direction;
import academy.cashbackmerge.domain.GameState;
import academy.cashbackmerge.spawn.WeightedSpawner;
import java.util.Objects;
import java.util.random.RandomGenerator;

/**
 * Engine for managing the game state and applying moves.
 */
public final class GameEngine {

    private final GameConfig config;
    private final WeightedSpawner spawner;
    private final RandomGenerator random;

    /**
     * Constructs a new GameEngine instance.
     *
     * @param config the game configuration
     * @param random the random generator used for spawning tiles
     * @throws IllegalArgumentException if the config or random is null
     */
    public GameEngine(GameConfig config, RandomGenerator random) {
        this.config = Objects.requireNonNull(config);
        this.random = Objects.requireNonNull(random);
        this.spawner = new WeightedSpawner(config.spawnRules());
    }

    /**
     * Retrieves the game configuration.
     *
     * @return the game configuration
     */
    public GameConfig config() {
        return config;
    }

    /**
     * Creates the initial game state with the default number of initial tiles.
     *
     * @return the initial game state
     */
    public GameState createInitialState() {
        return createInitialState(GameFactory.DEFAULT_INITIAL_TILES);
    }

    /**
     * Creates the initial game state with the specified number of initial tiles.
     *
     * @param initialTiles the number of initial tiles
     * @return the initial game state
     */
    public GameState createInitialState(int initialTiles) {
        return GameFactory.createInitial(config, random, initialTiles);
    }

    /**
     * Applies a move in the specified direction to the game state.
     *
     * @param state the current game state
     * @param direction the direction of the move
     * @return the result of the move
     * @throws IllegalArgumentException if the state or direction is null
     * @throws IllegalStateException if no moves are remaining
     */
    public MoveResult applyMove(GameState state, Direction direction) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(direction, "direction");

        if (state.movesRemaining() == 0) {
            throw new IllegalStateException("Не осталось ходов");
        }

        SlideResult slide = SlideEngine.apply(state.board(), direction);
        if (!slide.changed()) {
            return MoveResult.unchanged(state);
        }

        Board withSpawn = spawner.spawnRandomTile(slide.board(), random);
        GameState next = state.afterMove(withSpawn, slide.scoreGained());
        return MoveResult.changed(next, slide.scoreGained());
    }
}
