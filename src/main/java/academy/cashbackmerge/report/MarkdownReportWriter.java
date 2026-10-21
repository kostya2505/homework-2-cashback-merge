package academy.cashbackmerge.report;

import academy.cashbackmerge.simulation.SimulationResult;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public final class MarkdownReportWriter {

    private static final int PERCENT_MULTIPLIER = 100;

    public String render(SimulationResult r) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Cashback Merge — отчёт симуляции\n\n");
        appendConfiguration(sb, r);
        appendRun(sb, r);
        appendResults(sb, r);
        appendTileDistribution(sb, r);
        appendRewardDistribution(sb, r);
        sb.append("_Это эмпирический результат одного запуска, ");
        sb.append("а не математическая гарантия._\n");
        return sb.toString();
    }

    private static void appendConfiguration(StringBuilder sb, SimulationResult r) {
        sb.append("## Конфигурация\n\n");
        sb.append("| Параметр | Значение |\n|---|---|\n");
        sb.append("| Размер поля | ")
                .append(r.campaign().game().rows())
                .append("×")
                .append(r.campaign().game().cols())
                .append(" |\n");
        sb.append("| Лимит ходов | ").append(r.campaign().game().movesLimit()).append(" |\n");
        sb.append("| Кэшбэк максимальной награды | ")
                .append(r.campaign().maxRewardCashback())
                .append("% |\n");
        sb.append("| Допустимая доля максимальной награды | ")
                .append(formatPercent(r.campaign().maxRewardAllowedRate().doubleValue()))
                .append(" |\n\n");
    }

    private static void appendRun(StringBuilder sb, SimulationResult r) {
        sb.append("## Запуск\n\n");
        sb.append("| Параметр | Значение |\n|---|---|\n");
        sb.append("| Игроков | ").append(r.players()).append(" |\n");
        sb.append("| Seed | ").append(r.seed()).append(" |\n");
        sb.append("| Стратегия | ").append(r.strategyName()).append(" |\n\n");
    }

    private static void appendResults(StringBuilder sb, SimulationResult r) {
        sb.append("## Результаты\n\n");
        sb.append("| Метрика | Значение |\n|---|---|\n");
        sb.append("| Средний счёт | ")
                .append(String.format(Locale.ROOT, "%.2f", r.meanScore()))
                .append(" |\n");
        sb.append("| Максимальный счёт | ").append(r.maxScore()).append(" |\n");
        sb.append("| Фактическая доля максимальной награды | ")
                .append(formatPercent(r.actualMaxRewardRate()))
                .append(" |\n");
        sb.append("| Допустимая доля максимальной награды | ")
                .append(formatPercent(r.campaign().maxRewardAllowedRate().doubleValue()))
                .append(" |\n");
        sb.append("| Статус | **").append(r.status()).append("** |\n\n");
    }

    private static void appendTileDistribution(StringBuilder sb, SimulationResult r) {
        sb.append("## Распределение максимальных плиток\n\n");
        sb.append("| Максимальная плитка | Игроков | Доля |\n|---|---|---|\n");
        for (var e : r.maxTileDistribution().entrySet()) {
            sb.append("| ")
                    .append(e.getKey())
                    .append("% | ")
                    .append(e.getValue())
                    .append(" | ")
                    .append(formatPercent((double) e.getValue() / r.players()))
                    .append(" |\n");
        }
        sb.append('\n');
    }

    private static void appendRewardDistribution(StringBuilder sb, SimulationResult r) {
        sb.append("## Распределение наград\n\n");
        sb.append("| Награда | Игроков | Доля |\n|---|---|---|\n");
        for (var e : r.rewardDistribution().entrySet()) {
            sb.append("| ")
                    .append(e.getKey())
                    .append(" | ")
                    .append(e.getValue())
                    .append(" | ")
                    .append(formatPercent((double) e.getValue() / r.players()))
                    .append(" |\n");
        }
        sb.append('\n');
    }

    public void write(SimulationResult result, Path path) {
        try {
            Path parent = path.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(path, render(result));
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось записать отчёт: " + path, e);
        }
    }

    private static String formatPercent(double v) {
        return String.format(Locale.ROOT, "%.2f%%", v * PERCENT_MULTIPLIER);
    }
}
