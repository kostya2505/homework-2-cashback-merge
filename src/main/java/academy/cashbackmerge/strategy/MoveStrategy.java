package academy.cashbackmerge.strategy;

import academy.cashbackmerge.domain.Direction;
import academy.cashbackmerge.domain.GameState;
import org.jspecify.annotations.Nullable;

public interface MoveStrategy {

    String name();

    @Nullable
    Direction choose(GameState state);
}
