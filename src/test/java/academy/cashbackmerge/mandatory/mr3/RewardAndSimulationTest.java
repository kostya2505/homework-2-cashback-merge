package academy.cashbackmerge.mandatory.mr3;

import static org.assertj.core.api.Assertions.assertThat;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.config.GameConfig;
import academy.cashbackmerge.config.Probability;
import academy.cashbackmerge.config.SpawnRule;
import academy.cashbackmerge.domain.Tile;
import academy.cashbackmerge.report.MarkdownReportWriter;
import academy.cashbackmerge.reward.RewardTable;
import academy.cashbackmerge.simulation.SimulationResult;
import academy.cashbackmerge.simulation.SimulationRunner;
import academy.cashbackmerge.spawn.WeightedSpawner;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: награды, симуляция и отчёт. */
@DisplayName("MR3. Награды и симуляция")
class RewardAndSimulationTest {

    @Test
    @DisplayName("Максимальная плитка 16% даёт награду соответствующего tier")
    void rewardMatchesHighestTile() {
        RewardTable table = new RewardTable(Map.of(
                2, 0L,
                4, 50L,
                8, 150L,
                16, 500L,
                32, 2000L));

        assertThat(table.rewardFor(16)).isEqualTo(500L);
        assertThat(table.rewardFor(32)).isEqualTo(2000L);
        assertThat(table.rewardFor(8)).isEqualTo(150L);

        assertThat(table.rewardFor(12)).isEqualTo(150L);
    }

    @Test
    @DisplayName("Weighted random учитывает вероятности из конфига")
    void spawnRespectsConfiguredProbabilities() {
        List<SpawnRule> rules = List.of(
                new SpawnRule(Tile.ofPercent(1), Probability.parse("0.90")),
                new SpawnRule(Tile.ofPercent(2), Probability.parse("0.10")));
        WeightedSpawner spawner = new WeightedSpawner(rules);
        Random random = new Random(42L);
        int ones = 0;
        int twos = 0;
        int iterations = 10_000;
        for (int i = 0; i < iterations; i++) {
            Tile tile = spawner.choose(random);
            if (tile.cashbackPercent() == 1) {
                ones++;
            } else if (tile.cashbackPercent() == 2) {
                twos++;
            }
        }
        assertThat(ones)
                .as("ones=%d, twos=%d, total=%d", ones, twos, ones + twos)
                .isBetween(8500, 9500);
        assertThat(twos)
                .as("ones=%d, twos=%d, total=%d", ones, twos, ones + twos)
                .isBetween(500, 1500);
        assertThat(ones + twos).isEqualTo(iterations);
    }

    @Test
    @DisplayName("Отчёт содержит распределение максимальных плиток и наград")
    void reportContainsDistributions() {
        CampaignConfig campaign = createCampaignConfig(new BigDecimal("0.20"));
        SimulationResult result = new SimulationRunner(campaign).run(50, 42L, "random");
        String report = new MarkdownReportWriter().render(result);
        assertThat(report).contains("Распределение максимальных плиток");
        assertThat(report).contains("Распределение наград");
        assertThat(report).containsPattern("\\| \\d+% \\| \\d+ \\|");
        assertThat(report).containsPattern("\\| \\d+ \\| \\d+ \\|");
    }

    @Test
    @DisplayName("Отчёт содержит долю игроков с максимальной наградой")
    void reportContainsMaxRewardRate() {
        CampaignConfig campaign = createCampaignConfig(new BigDecimal("0.20"));
        SimulationResult result = new SimulationRunner(campaign).run(50, 42L, "random");
        String report = new MarkdownReportWriter().render(result);
        assertThat(report).contains("Фактическая доля максимальной награды");
        assertThat(report).contains("Допустимая доля максимальной награды");
        assertThat(report).contains("20.00%");
        assertThat(report).containsPattern("Фактическая доля максимальной награды \\| \\d+\\.\\d+%");
    }

    @Test
    @DisplayName("Статус OK, если actualMaxRewardRate <= maxRewardAllowedRate, иначе FAILED")
    void reportStatusReflectsBusinessConstraint() {
        CampaignConfig okCampaign = createCampaignConfig(BigDecimal.ONE);
        SimulationResult okResult = new SimulationRunner(okCampaign).run(50, 42L, "random");
        assertThat(okResult.status()).isEqualTo(SimulationResult.Status.OK);
        CampaignConfig failCampaign = createCampaignConfig(BigDecimal.ZERO);
        SimulationResult failResult = new SimulationRunner(failCampaign).run(50, 42L, "random");
        assertThat(failResult.status()).isEqualTo(SimulationResult.Status.FAILED);
        String report = new MarkdownReportWriter().render(failResult);
        assertThat(report).contains("**FAILED**");
    }

    private static CampaignConfig createCampaignConfig(BigDecimal allowedRate) {
        GameConfig game = new GameConfig(
                4,
                4,
                100,
                List.of(
                        new SpawnRule(Tile.ofPercent(1), Probability.parse("0.85")),
                        new SpawnRule(Tile.ofPercent(2), Probability.parse("0.15"))));
        RewardTable rewards = new RewardTable(Map.of(2, 0L, 4, 50L, 8, 150L, 16, 500L, 32, 2000L));
        return new CampaignConfig(game, rewards, 32, allowedRate);
    }
}
