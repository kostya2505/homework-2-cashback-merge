package academy.cashbackmerge.config;

import java.util.List;

/** Represents a game configuration. */
public record GameConfig(int rows, int cols, int movesLimit, List<SpawnRule> spawnRules) {

    /**
     * Constructs a new GameConfig instance.
     *
     * @param rows the number of rows in the board
     * @param cols the number of columns in the board
     * @param movesLimit the maximum number of moves allowed in the game
     * @param spawnRules the list of spawn rules for tiles
     * @throws ConfigValidationException if the configuration parameters are invalid
     */
    public GameConfig {
        if (rows <= 0 || cols <= 0) {
            throw new ConfigValidationException("Размер поля должен быть положительным: " + rows + "x" + cols);
        }
        if (movesLimit <= 0) {
            throw new ConfigValidationException("Ограничение ходоа должно быть положительным: " + movesLimit);
        }
        if (spawnRules == null || spawnRules.isEmpty()) {
            throw new ConfigValidationException("Правила появления не могут быть пустыми");
        }
        long sum = 0;
        for (SpawnRule rule : spawnRules) {
            sum += rule.probability().units();
        }
        if (sum != Probability.SCALE) {
            throw new ConfigValidationException("Сумма вероятностей правил должна давать 1.0, получено units=" + sum
                    + " (scale=" + Probability.SCALE + ")");
        }
    }
}
