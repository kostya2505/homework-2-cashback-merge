package academy.cashbackmerge.simulation;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.domain.Direction;
import academy.cashbackmerge.domain.GameState;
import academy.cashbackmerge.game.GameEngine;
import academy.cashbackmerge.strategy.MoveStrategy;
import academy.cashbackmerge.strategy.StrategyFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.random.RandomGenerator;

public final class SimulationRunner {

    private final CampaignConfig campaign;

    public SimulationRunner(CampaignConfig campaign) {
        this.campaign = campaign;
    }

    public SimulationResult run(int players, long seed, String strategyName) {
        if (players <= 0) {
            throw new IllegalArgumentException("Игроков должно быть больше 0: " + players);
        }

        RandomGenerator master = new Random(seed);
        List<Long> playerSeeds = new ArrayList<>(players);
        for (int i = 0; i < players; i++) {
            playerSeeds.add(master.nextLong());
        }

        List<GameOutcome> outcomes = new ArrayList<>(players);
        for (long playerSeed : playerSeeds) {
            outcomes.add(playOneGame(playerSeed, strategyName));
        }
        return aggregate(outcomes, players, seed, strategyName);
    }

    private GameOutcome playOneGame(long playerSeed, String strategyName) {
        RandomGenerator random = new Random(playerSeed);
        GameEngine engine = new GameEngine(campaign.game(), random);
        MoveStrategy strategy = StrategyFactory.create(strategyName, random);
        GameState state = engine.createInitialState();
        while (state.movesRemaining() > 0) {
            Direction direction = strategy.choose(state);
            if (direction == null) {
                break;
            }
            state = engine.applyMove(state, direction).state();
        }
        int maxTile = state.board().maxTilePercent();
        long reward = campaign.rewardTable().rewardFor(maxTile);
        return new GameOutcome(maxTile, state.score(), reward);
    }

    private SimulationResult aggregate(List<GameOutcome> outcomes, int players, long seed, String strategyName) {
        Map<Integer, Integer> tileDist = new TreeMap<>();
        Map<Long, Integer> rewardDist = new TreeMap<>();
        long totalScore = 0L;
        long maxScore = 0L;
        int maxRewardCount = 0;
        int maxRewardCashback = campaign.maxRewardCashback();
        for (GameOutcome o : outcomes) {
            tileDist.merge(o.finalMaxTile(), 1, Integer::sum);
            rewardDist.merge(o.reward(), 1, Integer::sum);
            totalScore += o.score();
            if (o.score() > maxScore) {
                maxScore = o.score();
            }
            if (o.finalMaxTile() >= maxRewardCashback) {
                maxRewardCount++;
            }
        }
        double meanScore = (double) totalScore / players;
        double actualMaxRewardRate = (double) maxRewardCount / players;
        double allowedRate = campaign.maxRewardAllowedRate().doubleValue();
        SimulationResult.Status status =
                actualMaxRewardRate <= allowedRate ? SimulationResult.Status.OK : SimulationResult.Status.FAILED;
        return new SimulationResult(
                campaign,
                players,
                seed,
                strategyName,
                tileDist,
                rewardDist,
                meanScore,
                maxScore,
                actualMaxRewardRate,
                status);
    }
}
