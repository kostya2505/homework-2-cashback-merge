package academy.cashbackmerge.report;

import academy.cashbackmerge.simulation.SimulationResult;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public final class StrategyComparisonReportWriter {

    private static final int PERCENT_MULTIPLIER = 100;

    public String render(List<SimulationResult> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Сравнение стратегий\n\n");
        sb.append("от же сид, разные стратегии.\n\n");
        sb.append(
                "| Стратегия  | Средний счёт | Максимальный счёт | Фактическая доля максимальной награды | Статус |\\n");
        sb.append("|---|---|---|---|---|\n");
        for (SimulationResult r : results) {
            sb.append("| ")
                    .append(r.strategyName())
                    .append(" | ")
                    .append(String.format(Locale.ROOT, "%.2f", r.meanScore()))
                    .append(" | ")
                    .append(r.maxScore())
                    .append(" | ")
                    .append(String.format(Locale.ROOT, "%.2f%%", r.actualMaxRewardRate() * PERCENT_MULTIPLIER))
                    .append(" | ")
                    .append(r.status())
                    .append(" |\n");
        }
        sb.append('\n');

        sb.append("## Детали по стратегиям\n\n");
        for (SimulationResult r : results) {
            sb.append("### ").append(r.strategyName()).append("\n\n");
            sb.append("Сид: ")
                    .append(r.seed())
                    .append(", игроков: ")
                    .append(r.players())
                    .append("\n\n");
            sb.append("Распределение максимальных плиток:\n\n");
            sb.append("| Максимальная плитка | Игроков |\n|---|---|\n");
            for (var e : r.maxTileDistribution().entrySet()) {
                sb.append("| ")
                        .append(e.getKey())
                        .append("% | ")
                        .append(e.getValue())
                        .append(" |\n");
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public void write(List<SimulationResult> results, Path path) {
        try {
            Files.createDirectories(path.toAbsolutePath().getParent());
            Files.writeString(path, render(results));
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось записать отчёт", e);
        }
    }
}
