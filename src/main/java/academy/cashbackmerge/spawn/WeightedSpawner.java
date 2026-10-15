package academy.cashbackmerge.spawn;

import academy.cashbackmerge.config.SpawnRule;
import academy.cashbackmerge.domain.Board;
import academy.cashbackmerge.domain.Position;
import academy.cashbackmerge.domain.Tile;
import java.util.List;
import java.util.Optional;
import java.util.random.RandomGenerator;

/** Spawns tiles on the board based on a list of weighted spawn rules. */
public final class WeightedSpawner {

    /** The list of spawn rules. */
    private final List<SpawnRule> rules;

    /** The total units representing the combined probability of all spawn rules. */
    private final long totalUnits;

    /**
     * Constructs a new WeightedSpawner instance.
     *
     * @param rules the list of spawn rules, must not be empty
     * @throws IllegalArgumentException if the rules list is empty
     */
    public WeightedSpawner(List<SpawnRule> rules) {
        if (rules == null || rules.isEmpty()) {
            throw new IllegalArgumentException("Правила не могут быть пустыми");
        }
        this.rules = List.copyOf(rules);
        this.totalUnits = rules.stream().mapToLong(r -> r.probability().units()).sum();
    }

    /**
     * Chooses a tile based on the weighted spawn rules.
     *
     * @param random the random number generator to use for selection
     * @return a Tile instance chosen based on the spawn rules
     */
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

    /**
     * Spawns a random tile on an empty position of the board.
     *
     * @param board the board to spawn the tile on
     * @param random the random number generator to use for tile placement and selection
     * @return the board with the newly spawned tile
     */
    public Board spawnRandomTile(Board board, RandomGenerator random) {
        List<Position> empties = board.emptyPositions();
        if (empties.isEmpty()) {
            return board;
        }
        Position position = empties.get(random.nextInt(empties.size()));
        return board.withTile(position, choose(random));
    }

    /**
     * Attempts to spawn a random tile on an empty position of the board.
     *
     * @param board the board to spawn the tile on
     * @param random the random number generator to use for tile placement and selection
     * @return an Optional containing the board with the newly spawned tile, or an empty Optional if the board is full
     */
    public Optional<Board> trySpawnRandomTile(Board board, RandomGenerator random) {
        if (board.isFull()) {
            return Optional.empty();
        }
        return Optional.of(spawnRandomTile(board, random));
    }
}
