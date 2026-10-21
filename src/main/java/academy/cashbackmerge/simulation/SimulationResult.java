package academy.cashbackmerge.simulation;

import academy.cashbackmerge.config.CampaignConfig;
import java.util.Map;

public record SimulationResult(
        CampaignConfig campaign,
        int players,
        long seed,
        String strategyName,
        Map<Integer, Integer> maxTileDistribution,
        Map<Long, Integer> rewardDistribution,
        double meanScore,
        long maxScore,
        double actualMaxRewardRate,
        Status status) {

    public enum Status {
        OK,
        FAILED
    }
}
