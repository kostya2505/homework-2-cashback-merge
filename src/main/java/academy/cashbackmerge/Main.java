package academy.cashbackmerge;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.config.JsonCampaignLoader;
import academy.cashbackmerge.domain.GameState;
import academy.cashbackmerge.game.GameEngine;
import academy.cashbackmerge.render.BoardRenderer;
import academy.cashbackmerge.report.MarkdownReportWriter;
import academy.cashbackmerge.report.StrategyComparisonReportWriter;
import academy.cashbackmerge.simulation.SimulationResult;
import academy.cashbackmerge.simulation.SimulationRunner;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import org.jspecify.annotations.Nullable;

/** Main class for running the Cashback Merge game. */
public final class Main {

    /** The default configuration file path. */
    private static final String DEFAULT_CONFIG = "config/campaign.example.json";

    /** The default seed for the random number generator. */
    private static final long DEFAULT_SEED = 42L;

    /** The default number of simulated players. */
    private static final int DEFAULT_PLAYERS = 1000;

    /** Multiplier to convert a fraction to a percent. */
    private static final int PERCENT_MULTIPLIER = 100;

    /** Private constructor to prevent instantiation. */
    private Main() {}

    /**
     * The main method to run the game.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            String[] rest = Arrays.copyOfRange(args, 1, args.length);
            switch (args[0]) {
                case "simulate" -> {
                    simulate(rest);
                    return;
                }
                case "show" -> {
                    showInitial(rest);
                    return;
                }
                case "help" -> {
                    printHelp();
                    return;
                }
                case "compare-strategies" -> {
                    compareStrategies(rest);
                    return;
                }
                // fall through: legacy mode — show initial board with args
            }
        }
        showInitial(args);
    }

    // ---------- show ----------

    private static void showInitial(String[] args) {
        Options o = Options.parse(args);
        CampaignConfig campaign = new JsonCampaignLoader().load(o.configPath());
        GameEngine engine = new GameEngine(campaign.game(), new Random(o.seed()));
        GameState state = engine.createInitialState();

        System.out.println("Конфиг: " + o.configPath());
        System.out.println("Сид: " + o.seed());
        System.out.println("Поле " + campaign.game().rows() + "×"
                + campaign.game().cols() + ", movesLimit=" + campaign.game().movesLimit());
        System.out.println();
        System.out.println(new BoardRenderer().render(state.board()));
        System.out.println();
        System.out.println("Ходов осталось: " + state.movesRemaining());
        System.out.println("Счёт: " + state.score());
    }

    // ---------- simulate ----------

    private static void simulate(String[] args) {
        Options o = Options.parse(args);
        Path output = o.output();
        if (output == null) {
            throw new IllegalArgumentException("--output is required for simulate");
        }

        CampaignConfig campaign = new JsonCampaignLoader().load(o.configPath());
        System.out.println(
                "Запуск симуляции: players=" + o.players() + ", strategy=" + o.strategy() + ", seed=" + o.seed() + "…");
        SimulationResult result = new SimulationRunner(campaign).run(o.players(), o.seed(), o.strategy());

        new MarkdownReportWriter().write(result, output);
        System.out.println("Отчёт записан: " + output);
        System.out.println("Статус: " + result.status()
                + " (actual=" + String.format(Locale.ROOT, "%.2f%%", result.actualMaxRewardRate() * PERCENT_MULTIPLIER)
                + ", allowed="
                + String.format(
                        Locale.ROOT, "%.2f%%", campaign.maxRewardAllowedRate().doubleValue() * PERCENT_MULTIPLIER)
                + ")");
    }

    // ---------- help ----------

    private static void printHelp() {
        System.out.println("""
                Cashback Merge — команды:
                  show [--config X] [--seed Y]
                      Показать стартовое поле.
                  simulate --config X --output report.md
                           [--players N] [--strategy random|greedy-score] [--seed S]
                      Запустить batch-симуляцию и записать отчёт.
                """);
    }

    // ---------- strategy-comparison ----------

    private static void compareStrategies(String[] args) {
        Options o = Options.parse(args);
        if (o.output() == null) {
            throw new IllegalArgumentException("--output required");
        }
        CampaignConfig campaign = new JsonCampaignLoader().load(o.configPath());
        SimulationRunner runner = new SimulationRunner(campaign);
        List<SimulationResult> results = new ArrayList<>();
        for (String s : List.of("random", "greedy-score")) {
            results.add(runner.run(o.players(), o.seed(), s));
        }
        new StrategyComparisonReportWriter().write(results, o.output());
        System.out.println("Отчёт записан: " + o.output());
    }

    // ---------- options ----------

    private record Options(
            Path configPath,
            long seed,
            int players,
            String strategy,
            @Nullable Path output) {

        static Options parse(String[] args) {
            Path config = Path.of(DEFAULT_CONFIG);
            long seed = DEFAULT_SEED;
            int players = DEFAULT_PLAYERS;
            String strategy = "random";
            Path output = null;

            int i = 0;
            while (i < args.length) {
                String flag = args[i];
                switch (flag) {
                    case "--config" -> {
                        requireValue(args, i, "--config");
                        config = Path.of(args[i + 1]);
                        i += 2;
                    }
                    case "--seed" -> {
                        requireValue(args, i, "--seed");
                        seed = Long.parseLong(args[i + 1]);
                        i += 2;
                    }
                    case "--players" -> {
                        requireValue(args, i, "--players");
                        players = Integer.parseInt(args[i + 1]);
                        i += 2;
                    }
                    case "--strategy" -> {
                        requireValue(args, i, "--strategy");
                        strategy = args[i + 1].toLowerCase(Locale.ROOT);
                        i += 2;
                    }
                    case "--output" -> {
                        requireValue(args, i, "--output");
                        output = Path.of(args[i + 1]);
                        i += 2;
                    }
                    default -> throw new IllegalArgumentException("Неизвестный аргумент: " + flag);
                }
            }
            return new Options(config, seed, players, strategy, output);
        }

        @Override
        @Nullable
        public Path output() {
            return output;
        }

        private static void requireValue(String[] args, int i, String flag) {
            if (i + 1 >= args.length) {
                throw new IllegalArgumentException(flag + " требует значения");
            }
        }
    }
}
