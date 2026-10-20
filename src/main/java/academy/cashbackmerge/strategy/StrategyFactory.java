package academy.cashbackmerge.strategy;

import java.util.List;
import java.util.random.RandomGenerator;

public final class StrategyFactory {

    private static final List<String> KNOWN = List.of("random", "greedy-score");

    private StrategyFactory() {}

    public static MoveStrategy create(String name, RandomGenerator random) {
        return switch (name) {
            case "random" -> new RandomMoveStrategy(random);
            case "greedy-score" -> new GreedyScoreStrategy();
            default -> throw new IllegalArgumentException("Неизвестная стратегия: " + name + ". Доступно: " + KNOWN);
        };
    }

    public static List<String> knownStrategies() {
        return KNOWN;
    }
}
