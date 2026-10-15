package academy.cashbackmerge.config;

import java.nio.file.Path;

/** Interface for loading configuration files. */
public interface ConfigLoader {
    /**
     * Loads a game configuration from a specified path.
     *
     * @param path the path to the configuration file
     * @return the loaded game configuration
     */
    GameConfig load(Path path);
}
