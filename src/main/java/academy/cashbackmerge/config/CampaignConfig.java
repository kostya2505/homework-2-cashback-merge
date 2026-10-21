package academy.cashbackmerge.config;

import academy.cashbackmerge.reward.RewardTable;
import java.math.BigDecimal;
import java.util.Objects;

public record CampaignConfig(
        GameConfig game, RewardTable rewardTable, int maxRewardCashback, BigDecimal maxRewardAllowedRate) {

    public CampaignConfig {
        Objects.requireNonNull(game, "game");
        Objects.requireNonNull(rewardTable, "rewardTable");
        Objects.requireNonNull(maxRewardAllowedRate, "maxRewardAllowedRate");
        if (maxRewardCashback <= 0) {
            throw new ConfigValidationException(
                    "Максимальная награда кэшбека должна быть больше 0: " + maxRewardCashback);
        }
        if (maxRewardAllowedRate.signum() < 0 || maxRewardAllowedRate.compareTo(BigDecimal.ONE) > 0) {
            throw new ConfigValidationException(
                    "maxRewardAllowedRate должен быть в интервале [0; 1]: " + maxRewardAllowedRate);
        }
    }
}
