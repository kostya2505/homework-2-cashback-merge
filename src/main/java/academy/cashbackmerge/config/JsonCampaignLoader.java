package academy.cashbackmerge.config;

import academy.cashbackmerge.reward.RewardTable;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class JsonCampaignLoader {

    private final ObjectMapper mapper = new ObjectMapper();
    private final JsonConfigLoader configLoader = new JsonConfigLoader();

    public CampaignConfig load(Path path) {
        if (!Files.isRegularFile(path)) {
            throw new ConfigValidationException("Конфиг не найден: " + path);
        }
        try {
            JsonNode root = mapper.readTree(path.toFile());
            GameConfig gameConfig = configLoader.loadFromNode(root);
            RewardTable rewardTable = parseRewardTable(root.get("rewardTable"));
            int maxRewardCashback = requireInt(root, "maxRewardCashback");
            BigDecimal maxRewardAllowedRate = new BigDecimal(requireText(root, "maxRewardAllowedRate"));
            return new CampaignConfig(gameConfig, rewardTable, maxRewardCashback, maxRewardAllowedRate);

        } catch (IOException exception) {
            throw new ConfigValidationException("Не получилось прочитать конфиг: " + path, exception);
        }
    }

    private RewardTable parseRewardTable(JsonNode node) {
        if (node == null || !node.isArray() || node.isEmpty()) {
            throw new ConfigValidationException("Таблица наград должна быть непустой");
        }
        Map<Integer, Long> rewards = new LinkedHashMap<>();
        for (JsonNode entry : node) {
            int cashback = Integer.parseInt(requireText(entry, "cashback"));
            long reward = Long.parseLong(requireText(entry, "reward"));
            rewards.put(cashback, reward);
        }
        return new RewardTable(rewards);
    }

    private static int requireInt(JsonNode root, String field) {
        JsonNode node = root.get(field);
        if (node == null) {
            throw new ConfigValidationException("Пропущено поле: " + field);
        }
        if (node.isTextual()) {
            try {
                return Integer.parseInt(node.asText().trim());
            } catch (NumberFormatException exception) {
                throw new ConfigValidationException("Неверный int для " + field + ": " + node.asText(), exception);
            }
        }
        if (!node.canConvertToInt()) {
            throw new ConfigValidationException("Неверный int для " + field + ": " + node);
        }
        return node.asInt();
    }

    private static String requireText(JsonNode root, String field) {
        JsonNode node = root.get(field);
        if (node == null || !node.isTextual()) {
            throw new ConfigValidationException("Упущено или не текстовое поле: " + field);
        }
        return node.asText();
    }
}
