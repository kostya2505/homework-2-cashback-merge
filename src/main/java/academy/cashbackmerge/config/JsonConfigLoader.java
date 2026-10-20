package academy.cashbackmerge.config;

import academy.cashbackmerge.domain.Tile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Loads game configuration from a JSON file. */
public final class JsonConfigLoader implements ConfigLoader {

    private final ObjectMapper mapper;

    /** Constructs a new JsonConfigLoader. */
    public JsonConfigLoader() {
        this.mapper = new ObjectMapper();
    }

    /**
     * Loads the game configuration from the specified file path.
     *
     * @param path the path to the configuration file
     * @return the loaded game configuration
     * @throws ConfigValidationException if the configuration file is not found or is invalid
     */
    @Override
    public GameConfig load(Path path) {
        if (!Files.isRegularFile(path)) {
            throw new ConfigValidationException("Конфиг не найден: " + path);
        }
        try {
            JsonNode root = mapper.readTree(path.toFile());
            return loadFromNode(root);
        } catch (IOException e) {
            throw new ConfigValidationException("Не получилось прочитать конфиг: " + path, e);
        }
    }

    public GameConfig loadFromNode(JsonNode root) {
        int boardSize = requireInt(root, "boardSize");
        int movesLimit = requireInt(root, "movesLimit");

        JsonNode rulesNode = root.get("spawnRules");
        if (rulesNode == null || !rulesNode.isArray() || rulesNode.isEmpty()) {
            throw new ConfigValidationException("Правила появления должны быть непустым списком");
        }

        List<SpawnRule> rules = new ArrayList<>(rulesNode.size());
        for (JsonNode rule : rulesNode) {
            rules.add(parseRule(rule));
        }
        return new GameConfig(boardSize, boardSize, movesLimit, rules);
    }

    /**
     * Parses a spawn rule from the given JSON node.
     *
     * @param node the JSON node representing a spawn rule
     * @return the parsed SpawnRule
     * @throws ConfigValidationException if the spawn rule is invalid
     */
    private SpawnRule parseRule(JsonNode node) {
        JsonNode cashback = node.get("cashback");
        JsonNode probability = node.get("probability");
        if (cashback == null || !cashback.isTextual()) {
            throw new ConfigValidationException("Правило появления должно быть текстовым 'cashback'");
        }
        if (probability == null || !probability.isTextual()) {
            throw new ConfigValidationException("Правило появления должно быть текстовым 'probability'");
        }
        int percent;
        try {
            percent = Integer.parseInt(cashback.asText().trim());
        } catch (NumberFormatException e) {
            throw new ConfigValidationException("Неверный кэшбэк: " + cashback.asText(), e);
        }
        return new SpawnRule(Tile.ofPercent(percent), Probability.parse(probability.asText()));
    }

    /**
     * Requires an integer value from the JSON node with the given field name.
     *
     * @param root the JSON node containing the configuration data
     * @param field the field name to retrieve the integer value from
     * @return the integer value
     * @throws ConfigValidationException if the field is missing or not an integer
     */
    private int requireInt(JsonNode root, String field) {
        JsonNode node = root.get(field);
        if (node == null || !node.canConvertToInt()) {
            throw new ConfigValidationException("Недастоющее или неверное поле: " + field);
        }
        return node.asInt();
    }
}
