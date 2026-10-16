package academy.cashbackmerge.game;

import academy.cashbackmerge.config.GameConfig;
import academy.cashbackmerge.domain.Board;
import academy.cashbackmerge.domain.Direction;
import academy.cashbackmerge.domain.GameState;
import academy.cashbackmerge.spawn.WeightedSpawner;
import java.util.Objects;
import java.util.random.RandomGenerator;

public final class GameEngine {

    private final GameConfig config;
    private final WeightedSpawner spawner;
    private final RandomGenerator random;

    public GameEngine(GameConfig config, RandomGenerator random) {
        this.config = Objects.requireNonNull(config);
        this.random = Objects.requireNonNull(random);
        this.spawner = new WeightedSpawner(config.spawnRules());
    }

    public GameConfig config() {
        return config;
    }

    public GameState createInitialState() {
        return createInitialState(GameFactory.DEFAULT_INITIAL_TILES);
    }

    public GameState createInitialState(int initialTiles) {
        return GameFactory.createInitial(config, random, initialTiles);
    }

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
