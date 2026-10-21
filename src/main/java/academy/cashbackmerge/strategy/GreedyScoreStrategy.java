package academy.cashbackmerge.strategy;

import academy.cashbackmerge.domain.Direction;
import academy.cashbackmerge.domain.GameState;
import academy.cashbackmerge.game.SlideEngine;
import academy.cashbackmerge.game.SlideResult;
import org.jspecify.annotations.Nullable;

public class GreedyScoreStrategy implements MoveStrategy {

    @Override
    public String name() {
        return "greedy-score";
    }

    @Override
    @Nullable
    public Direction choose(GameState state) {
        Direction best = null;
        long bestScore = -1;
        int bestEmpties = -1;
        for (Direction d : Direction.values()) {
            SlideResult r = SlideEngine.apply(state.board(), d);
            if (!r.changed()) {
                continue;
            }
            int empties = r.board().emptyPositions().size();
            if (best == null
                    || r.scoreGained() > bestScore
                    || (r.scoreGained() == bestScore && empties > bestEmpties)) {
                best = d;
                bestScore = r.scoreGained();
                bestEmpties = empties;
            }
        }
        return best;
    }
}
