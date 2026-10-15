package academy.cashbackmerge.mandatory.mr1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.fail;

import academy.cashbackmerge.config.GameConfig;
import academy.cashbackmerge.config.Probability;
import academy.cashbackmerge.config.SpawnRule;
import academy.cashbackmerge.domain.GameState;
import academy.cashbackmerge.domain.Tile;
import academy.cashbackmerge.game.GameFactory;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Disabled;
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
    @Disabled("MR3: реализуй тест и удали эту строку")
    @DisplayName("Одинаковый конфиг, seed и стратегия дают одинаковый отчёт симуляции")
    void sameInputsProduceSameSimulationReport() {
        fail("Тест не реализован");
    }
}
