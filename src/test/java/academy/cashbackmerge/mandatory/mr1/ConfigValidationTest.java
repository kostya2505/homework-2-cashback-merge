package academy.cashbackmerge.mandatory.mr1;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import academy.cashbackmerge.config.ConfigValidationException;
import academy.cashbackmerge.config.GameConfig;
import academy.cashbackmerge.config.Probability;
import academy.cashbackmerge.config.SpawnRule;
import academy.cashbackmerge.domain.Tile;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: конфиг кампании. */
@DisplayName("MR1. Конфиг кампании")
class ConfigValidationTest {

    private static SpawnRule rule(int percent, String probability) {
        return new SpawnRule(Tile.ofPercent(percent), Probability.parse(probability));
    }

    @Test
    @DisplayName("Валидный конфиг принимается")
    void validConfigIsAccepted() {
        GameConfig config = assertDoesNotThrow(
                () -> new GameConfig(4, 4, 100, List.of(rule(1, "0.85"), rule(2, "0.1"), rule(4, "0.05"))));
        assertEquals(4, config.rows());
        assertEquals(4, config.cols());
        assertEquals(100, config.movesLimit());
        assertEquals(3, config.spawnRules().size());
    }

    @Test
    @DisplayName("Сумма вероятностей spawn-правил не равна 1 — конфиг отклоняется")
    void probabilitiesNotSummingToOneAreRejected() {
        assertThrows(
                ConfigValidationException.class,
                () -> new GameConfig(4, 4, 100, List.of(rule(1, "0.1"), rule(2, "0.3"), rule(4, "0.5"))));
        assertThrows(
                ConfigValidationException.class,
                () -> new GameConfig(4, 4, 100, List.of(rule(1, "0.4"), rule(2, "0.4"), rule(4, "0.4"))));
    }

    @Test
    @DisplayName("movesLimit <= 0 — конфиг отклоняется")
    void nonPositiveMovesLimitIsRejected() {
        assertThrows(
                ConfigValidationException.class,
                () -> new GameConfig(4, 4, 0, List.of(rule(1, "0.85"), rule(2, "0.1"), rule(4, "0.05"))));
        assertThrows(
                ConfigValidationException.class,
                () -> new GameConfig(4, 4, -1, List.of(rule(1, "0.85"), rule(2, "0.1"), rule(4, "0.05"))));
    }

    @Test
    @DisplayName("Пустой список spawn-правил — конфиг отклоняется")
    void emptySpawnRulesAreRejected() {
        assertThrows(ConfigValidationException.class, () -> new GameConfig(4, 4, 0, List.of()));
        assertThrows(ConfigValidationException.class, () -> new GameConfig(4, 4, 0, null));
    }
}
