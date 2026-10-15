package academy.cashbackmerge.config;

import java.nio.file.Path;

public interface ConfigLoader {
    GameConfig load(Path path);
}
