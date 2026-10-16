package academy.cashbackmerge;

import academy.cashbackmerge.config.ConfigLoader;
import academy.cashbackmerge.config.GameConfig;
import academy.cashbackmerge.config.JsonConfigLoader;
import academy.cashbackmerge.domain.Direction;
import academy.cashbackmerge.domain.GameState;
import academy.cashbackmerge.game.GameEngine;
import academy.cashbackmerge.game.GameFactory;
import academy.cashbackmerge.game.MoveResult;
import academy.cashbackmerge.render.BoardRenderer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.random.RandomGenerator;

/** Main class for running the Cashback Merge game. */
public final class Main {

    /** The default configuration file path. */
    private static final String DEFAULT_CONFIG = "config/campaign.example.json";

    /** The default seed for the random number generator. */
    private static final long DEFAULT_SEED = 42L;

    /** Private constructor to prevent instantiation. */
    private Main() {}

    /**
     * The main method to run the game.
     *
     * @param args command-line arguments
     */
    static void main(String[] args) {
        Args parsed = Args.parse(args);

        ConfigLoader loader = new JsonConfigLoader();
        GameConfig config = loader.load(parsed.configPath());

        RandomGenerator random = new Random(parsed.seed());
        GameEngine engine = new GameEngine(config, random);
        GameState state = GameFactory.createInitial(config, random);

        System.out.println("Конфиг: " + parsed.configPath());
        System.out.println("Сид: " + parsed.seed());
        System.out.println("Поле " + config.rows() + "x" + config.cols() + ", movesLimit=" + config.movesLimit());
        System.out.println();
        System.out.println(new BoardRenderer().render(state.board()));
        System.out.println();
        System.out.println("Ходов осталось: " + state.movesRemaining());
        System.out.println("Счёт: " + state.score());

        for (Direction direction : parsed.moves()) {
            if (state.movesRemaining() == 0) {
                System.out.println("\nЛимит ходов исчерпан.");
                break;
            }
            MoveResult result = engine.applyMove(state, direction);
            state = result.state();
            System.out.println();
            System.out.println("Ход " + direction + (result.changed() ? "" : " (без изменений)"));
            System.out.println(new BoardRenderer().render(state.board()));
            System.out.println("Ходов осталось: " + state.movesRemaining());
            System.out.println("Счёт: " + state.score());
        }
    }

    /** Represents command-line arguments. */
    private record Args(Path configPath, long seed, List<Direction> moves) {

        /**
         * Parses command-line arguments and returns the parsed arguments.
         *
         * @param args the command-line arguments
         * @return the parsed arguments
         * @throws IllegalArgumentException if there are invalid arguments
         */
        static Args parse(String[] args) {
            Path configPath = Path.of(DEFAULT_CONFIG);
            long seed = DEFAULT_SEED;
            List<Direction> moves = List.of();

            int i = 0;
            while (i < args.length) {
                String flag = args[i];
                switch (flag) {
                    case "--config" -> {
                        if (i + 1 >= args.length) {
                            throw new IllegalArgumentException("--config требует значения");
                        }
                        configPath = Path.of(args[i + 1]);
                        i += 2;
                    }
                    case "--seed" -> {
                        if (i + 1 >= args.length) {
                            throw new IllegalArgumentException("--seed требует значения");
                        }
                        seed = Long.parseLong(args[i + 1]);
                        i += 2;
                    }
                    case "--moves" -> {
                        if (i + 1 >= args.length) {
                            throw new IllegalArgumentException("--moves требует значения");
                        }
                        moves = parseMoves(args[i + 1]);
                        i += 2;
                    }
                    default -> throw new IllegalArgumentException("Неизвестный аргумент: " + flag);
                }
            }
            return new Args(configPath, seed, moves);
        }

        private static List<Direction> parseMoves(String raw) {
            List<Direction> result = new ArrayList<>();
            for (String token : raw.split(",")) {
                String trimmed = token.trim().toUpperCase(Locale.ROOT);
                if (!trimmed.isEmpty()) {
                    result.add(Direction.valueOf(trimmed));
                }
            }
            return List.copyOf(result);
        }
    }
}
