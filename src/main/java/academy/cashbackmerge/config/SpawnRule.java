package academy.cashbackmerge.config;

import academy.cashbackmerge.domain.Tile;
import java.util.Objects;

/** Represents a rule for spawning a tile with a specified probability. */
public record SpawnRule(Tile tile, Probability probability) {

    /**
     * Constructs a new SpawnRule instance.
     *
     * @param tile the tile to be spawned
     * @param probability the probability of the tile being spawned
     * @throws NullPointerException if the tile or probability is null
     */
    public SpawnRule {
        Objects.requireNonNull(tile, "tile");
        Objects.requireNonNull(probability, "probability");
    }
}
