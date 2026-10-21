package academy.cashbackmerge.mandatory.mr1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.config.GameConfig;
import academy.cashbackmerge.config.JsonCampaignLoader;
import academy.cashbackmerge.config.Probability;
import academy.cashbackmerge.config.SpawnRule;
import academy.cashbackmerge.domain.GameState;
import academy.cashbackmerge.domain.Tile;
import academy.cashbackmerge.game.GameFactory;
import academy.cashbackmerge.report.MarkdownReportWriter;
import academy.cashbackmerge.simulation.SimulationResult;
import academy.cashbackmerge.simulation.SimulationRunner;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: воспроизводимость по seed. */
@DisplayName("MR1. Воспроизводимость по seed")
class SeedTest {

    private static GameConfig config() {
        return new GameConfig(
                4,
                4,
                50,
                List.of(
                        new SpawnRule(Tile.ofPercent(1), Probability.parse("0.85")),
                        new SpawnRule(Tile.ofPercent(2), Probability.parse("0.10")),
                        new SpawnRule(Tile.ofPercent(4), Probability.parse("0.05"))));
    }

    @Test
    @DisplayName("Одинаковый конфиг и seed дают одинаковое начальное поле")
    void sameConfigAndSeedProduceSameInitialBoard() {
        GameConfig config = config();
        long seed = 42L;

        GameState first = GameFactory.createInitial(config, new Random(seed));
        GameState second = GameFactory.createInitial(config, new Random(seed));

        assertEquals(first.board(), second.board());
        assertEquals(first.movesRemaining(), second.movesRemaining());
        assertEquals(first.score(), second.score());

        assertNotSame(first.board(), second.board());
        assertEquals(2, first.board().tiles().size());
    }

    @Test
    @DisplayName("Одинаковый конфиг, seed и стратегия дают одинаковый отчёт симуляции")
    void sameInputsProduceSameSimulationReport() {
        CampaignConfig campaign = new JsonCampaignLoader().load(Path.of("config/campaign.example.json"));
        SimulationRunner runner = new SimulationRunner(campaign);
        SimulationResult first = runner.run(100, 42L, "random");
        SimulationResult second = runner.run(100, 42L, "random");
        String report1 = new MarkdownReportWriter().render(first);
        String report2 = new MarkdownReportWriter().render(second);
        assertEquals(report1, report2);
        assertEquals(first.meanScore(), second.meanScore());
        assertEquals(first.maxScore(), second.maxScore());
        assertEquals(first.actualMaxRewardRate(), second.actualMaxRewardRate());
        assertEquals(first.status(), second.status());
        assertEquals(first.maxTileDistribution(), second.maxTileDistribution());
    }
}
