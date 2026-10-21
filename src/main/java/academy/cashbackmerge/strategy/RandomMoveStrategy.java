package academy.cashbackmerge.strategy;

import academy.cashbackmerge.domain.Direction;
import academy.cashbackmerge.domain.GameState;
import academy.cashbackmerge.game.SlideEngine;
import java.util.random.RandomGenerator;
import org.jspecify.annotations.Nullable;

public final class RandomMoveStrategy implements MoveStrategy {

    private final RandomGenerator random;

    public RandomMoveStrategy(RandomGenerator random) {
        this.random = random;
    }

    @Override
    public String name() {
        return "random";
    }

    @Override
    @Nullable
    public Direction choose(GameState state) {
        Direction[] valid = new Direction[Direction.values().length];
        int count = 0;
        for (Direction d : Direction.values()) {
            if (SlideEngine.apply(state.board(), d).changed()) {
                valid[count++] = d;
            }
        }
        if (count == 0) {
            return null;
        }
        return valid[random.nextInt(count)];
    }
}
