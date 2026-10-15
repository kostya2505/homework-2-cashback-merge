package academy.cashbackmerge.game;

import academy.cashbackmerge.config.GameConfig;
import academy.cashbackmerge.domain.Board;
import academy.cashbackmerge.domain.GameState;
import academy.cashbackmerge.spawn.WeightedSpawner;
import java.util.random.RandomGenerator;

public final class GameFactory {
    public static final int DEFAULT_INITIAL_TILES = 2;

    private GameFactory() {}

    public static GameState createInitial(GameConfig config, RandomGenerator random) {
        return createInitial(config, random, DEFAULT_INITIAL_TILES);
    }

    public static GameState createInitial(GameConfig config, RandomGenerator random, int initialTiles) {
        if (initialTiles < 0) {
            throw new IllegalArgumentException("initialTiles must be non-negative: " + initialTiles);
        }
        Board board = new Board(config.rows(), config.cols());
        if (initialTiles > board.rows() * board.cols()) {
            throw new IllegalArgumentException("initialTiles exceeds board capacity");
        }
        WeightedSpawner spawner = new WeightedSpawner(config.spawnRules());
        for (int i = 0; i < initialTiles; i++) {
            board = spawner.spawnRandomTile(board, random);
        }
        return new GameState(board, config.movesLimit(), 0L);
    }
}
