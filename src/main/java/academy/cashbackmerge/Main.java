package academy.cashbackmerge;

import academy.cashbackmerge.config.ConfigLoader;
import academy.cashbackmerge.config.GameConfig;
import academy.cashbackmerge.config.JsonConfigLoader;
import academy.cashbackmerge.domain.GameState;
import academy.cashbackmerge.game.GameFactory;
import academy.cashbackmerge.render.BoardRenderer;
import java.nio.file.Path;
import java.util.Random;
import java.util.random.RandomGenerator;

/** Точка входа в Cashback Merge. */
public final class Main {

    private static final String DEFAULT_CONFIG = "config/campaign.json";
    private static final long DEFAULT_SEED = 42L;

    private Main() {}

    static void main(String[] args) {
        Args parsed = Args.parse(args);

        ConfigLoader loader = new JsonConfigLoader();
        GameConfig config = loader.load(parsed.configPath());

        RandomGenerator random = new Random(parsed.seed());
        GameState state = GameFactory.createInitial(config, random);

        System.out.println("Config: " + parsed.configPath());
        System.out.println("Seed: " + parsed.seed());
        System.out.println("Board " + config.rows() + "x" + config.cols() + ", movesLimit=" + config.movesLimit());
        System.out.println();
        System.out.println(new BoardRenderer().render(state.board()));
        System.out.println();
        System.out.println("Moves remaining: " + state.movesRemaining());
        System.out.println("Score: " + state.score());
    }

    private record Args(Path configPath, long seed) {

        static Args parse(String[] args) {
            Path configPath = Path.of(DEFAULT_CONFIG);
            long seed = DEFAULT_SEED;

            int i = 0;
            while (i < args.length) {
                String flag = args[i];
                switch (flag) {
                    case "--config" -> {
                        if (i + 1 >= args.length) {
                            throw new IllegalArgumentException("--config requires a value");
                        }
                        configPath = Path.of(args[i + 1]);
                        i += 2;
                    }
                    case "--seed" -> {
                        if (i + 1 >= args.length) {
                            throw new IllegalArgumentException("--seed requires a value");
                        }
                        seed = Long.parseLong(args[i + 1]);
                        i += 2;
                    }
                    default -> throw new IllegalArgumentException("Unknown argument: " + flag);
                }
            }
            return new Args(configPath, seed);
        }
    }
}
