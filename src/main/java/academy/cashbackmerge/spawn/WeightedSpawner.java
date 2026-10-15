package academy.cashbackmerge.spawn;

import academy.cashbackmerge.config.SpawnRule;
import academy.cashbackmerge.domain.Board;
import academy.cashbackmerge.domain.Position;
import academy.cashbackmerge.domain.Tile;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.random.RandomGenerator;

public final class WeightedSpawner {
    private final List<SpawnRule> rules;
    private final long totalUnits;

    public WeightedSpawner(List<SpawnRule> rules) {
        if (rules == null || rules.isEmpty()) {
            throw new IllegalArgumentException("rules must not be empty");
        }
        this.rules = List.copyOf(rules);
        this.totalUnits = rules.stream().mapToLong(r -> r.probability().units()).sum();
    }

    public Tile choose(RandomGenerator random) {
        long pick = random.nextLong(totalUnits);
        long acc = 0;
        for (SpawnRule rule : rules) {
            acc += rule.probability().units();
            if (pick < acc) {
                return rule.tile();
            }
        }
        return rules.getLast().tile();
    }

    public Board spawnRandomTile(Board board, RandomGenerator random) {
        List<Position> empties = board.emptyPositions();
        if (empties.isEmpty()) {
            return board;
        }
        Position position = empties.get(new Random().nextInt(empties.size()));
        return board.withTile(position, choose(random));
    }

    public Optional<Board> trySpawnRandomTile(Board board, RandomGenerator random) {
        if (board.isFull()) {
            return Optional.empty();
        }
        return Optional.of(spawnRandomTile(board, random));
    }
}
