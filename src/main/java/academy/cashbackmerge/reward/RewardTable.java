package academy.cashbackmerge.reward;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public final class RewardTable {

    private final TreeMap<Integer, Long> rewardsByCashback;

    public RewardTable(Map<Integer, Long> rewardsByCashback) {
        if (rewardsByCashback == null || rewardsByCashback.isEmpty()) {
            throw new IllegalArgumentException("Таблица наград должно быть непустой");
        }
        TreeMap<Integer, Long> sorted = new TreeMap<>();
        for (Map.Entry<Integer, Long> entry : rewardsByCashback.entrySet()) {
            if (entry.getKey() <= 0) {
                throw new IllegalArgumentException("Кэшбек должен быть больше 0: " + entry.getKey());
            }
            if (entry.getValue() < 0) {
                throw new IllegalArgumentException("Награда должна быть больше или равна нулю");
            }
            sorted.put(entry.getKey(), entry.getValue());
        }
        this.rewardsByCashback = sorted;
    }

    public long rewardFor(int maxCashback) {
        Map.Entry<Integer, Long> entry = rewardsByCashback.floorEntry(maxCashback);
        return entry == null ? 0L : entry.getValue();
    }

    public Map<Integer, Long> tiers() {
        return Collections.unmodifiableMap(rewardsByCashback);
    }

    public int highestCashback() {
        return rewardsByCashback.lastKey();
    }
}
