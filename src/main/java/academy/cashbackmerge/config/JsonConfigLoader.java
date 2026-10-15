package academy.cashbackmerge.config;

import academy.cashbackmerge.domain.Tile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class JsonConfigLoader implements ConfigLoader {
    private final ObjectMapper mapper;

    public JsonConfigLoader() {
        this.mapper = new ObjectMapper();
    }

    @Override
    public GameConfig load(Path path) {
        if (!Files.isRegularFile(path)) {
            throw new ConfigValidationException("Config file not found: " + path);
        }
        try {
            JsonNode root = mapper.readTree(path.toFile());
            int boardSize = requireInt(root, "boardSize");
            int movesLimit = requireInt(root, "movesLimit");

            JsonNode rulesNode = root.get("spawnRules");
            if (rulesNode == null || !rulesNode.isArray() || rulesNode.isEmpty()) {
                throw new ConfigValidationException("spawnRules must be a non-empty array");
            }

            List<SpawnRule> rules = new ArrayList<>(rulesNode.size());
            for (JsonNode rule : rulesNode) {
                rules.add(parseRule(rule));
            }
            return new GameConfig(boardSize, boardSize, movesLimit, rules);
        } catch (IOException e) {
            throw new ConfigValidationException("Failed to read config file: " + path, e);
        }
    }

    private SpawnRule parseRule(JsonNode node) {
        JsonNode cashback = node.get("cashback");
        JsonNode probability = node.get("probability");
        if (cashback == null || !cashback.isTextual()) {
            throw new ConfigValidationException("Spawn rule must be textual 'cashback'");
        }
        if (probability == null || !probability.isTextual()) {
            throw new ConfigValidationException("Spawn rule must be textual 'probability'");
        }
        int percent;
        try {
            percent = Integer.parseInt(cashback.asText().trim());
        } catch (NumberFormatException e) {
            throw new ConfigValidationException("Invalid cashback: " + cashback.asText(), e);
        }
        return new SpawnRule(Tile.ofPercent(percent), Probability.parse(probability.asText()));
    }

    private int requireInt(JsonNode root, String field) {
        JsonNode node = root.get(field);
        if (node == null || !node.canConvertToInt()) {
            throw new ConfigValidationException("Missing or invalid field: " + field);
        }
        return node.asInt();
    }
}
