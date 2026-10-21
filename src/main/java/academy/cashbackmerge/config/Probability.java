package academy.cashbackmerge.config;

import java.math.BigDecimal;

/**
 * Represents a probability with a fixed scale of 1,000,000,000 units.
 *
 * <p>This class ensures that the probability value is within the range (0, 1].
 */
public record Probability(long units) {

    /** The fixed scale for the probability units. */
    public static final long SCALE = 1_000_000_000L;

    /** The number of digits representing the scale. */
    private static final int SCALE_DIGITS = 9;

    /**
     * Constructs a new Probability instance.
     *
     * @param units the units representing the probability, must be in the range (0, 1]
     * @throws ConfigValidationException if the units are not within the valid range
     */
    public Probability {
        if (units <= 0 || units > SCALE) {
            throw new ConfigValidationException("Вероятность должна быть в (0; 1], получено units=" + units);
        }
    }

    /**
     * Parses a string representation of a probability into a Probability instance.
     *
     * @param text the string representation of the probability
     * @return a new Probability instance
     * @throws ConfigValidationException if the input text is invalid
     */
    public static Probability parse(String text) {
        if (text == null || text.isBlank()) {
            throw new ConfigValidationException("Вероятность не может быть пустой");
        }
        try {
            BigDecimal value = new BigDecimal(text.trim());
            if (value.signum() <= 0) {
                throw new ConfigValidationException("Вероятность должна быть положительной: " + text);
            }
            BigDecimal scaled = value.movePointRight(SCALE_DIGITS);
            return new Probability(scaled.longValueExact());
        } catch (NumberFormatException | ArithmeticException e) {
            throw new ConfigValidationException("Неверная вероятность: " + text, e);
        }
    }

    /**
     * Converts the Probability instance to a BigDecimal representation.
     *
     * @return the BigDecimal representation of the probability
     */
    public BigDecimal toBigDecimal() {
        return BigDecimal.valueOf(units, SCALE_DIGITS);
    }
}
