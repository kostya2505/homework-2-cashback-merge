package academy.cashbackmerge.config;

import java.math.BigDecimal;

public record Probability(long units) {
    public static final long SCALE = 1_000_000_000L;
    private static final int SCALE_DIGITS = 9;

    public Probability {
        if (units <= 0 || units > SCALE) {
            throw new ConfigValidationException("Probability must be in (0; 1], got units=" + units);
        }
    }

    public static Probability parse(String text) {
        if (text == null || text.isBlank()) {
            throw new ConfigValidationException("Probability is empty");
        }
        try {
            BigDecimal value = new BigDecimal(text.trim());
            if (value.signum() <= 0) {
                throw new ConfigValidationException("Probability must be positive: " + text);
            }
            BigDecimal scaled = value.movePointRight(SCALE_DIGITS);
            return new Probability(scaled.longValueExact());
        } catch (NumberFormatException | ArithmeticException e) {
            throw new ConfigValidationException("Invalid probability: " + text, e);
        }
    }

    public BigDecimal toBigDecimal() {
        return BigDecimal.valueOf(units, SCALE_DIGITS);
    }
}
