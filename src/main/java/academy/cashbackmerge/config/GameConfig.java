package academy.cashbackmerge.config;

import java.util.List;

public record GameConfig(int rows, int cols, int movesLimit, List<SpawnRule> spawnRules) {
    public GameConfig {
        if (rows <= 0 || cols <= 0) {
            throw new ConfigValidationException("Board size must be positive: " + rows + "x" + cols);
        }
        if (movesLimit <= 0) {
            throw new ConfigValidationException("movesLimit must be positive: " + movesLimit);
        }
        if (spawnRules == null || spawnRules.isEmpty()) {
            throw new ConfigValidationException("spawnRuleLes cannot be empty");
        }
        long sum = 0;
        for (SpawnRule rule : spawnRules) {
            sum += rule.probability().units();
        }
        if (sum != Probability.SCALE) {
            throw new ConfigValidationException("Spawn rule probabilities must sum to 1.0, got units=" + sum
                    + " (scale=" + Probability.SCALE + ")");
        }
    }
}
