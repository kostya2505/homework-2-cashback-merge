package academy.cashbackmerge.config;

import academy.cashbackmerge.domain.Tile;
import java.util.Objects;

public record SpawnRule(Tile tile, Probability probability) {
    public SpawnRule {
        Objects.requireNonNull(tile, "tile");
        Objects.requireNonNull(probability, "probability");
    }
}
