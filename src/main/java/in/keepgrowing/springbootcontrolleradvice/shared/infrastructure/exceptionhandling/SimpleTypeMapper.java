package in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.exceptionhandling;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SimpleTypeMapper {

    protected static final String SIMPLE_FLOATING = "a floating point number";
    protected static final String SIMPLE_INTEGER = "an integer number";
    protected static final String SIMPLE_STRING = "a string";
    protected static final String SIMPLE_CHAR = "a character";
    protected static final String SIMPLE_DATE = "a date";
    protected static final String SIMPLE_DATE_TIME = "a date and time";
    protected static final String SIMPLE_UUID = "a UUID";
    protected static final String SIMPLE_BOOLEAN = "a boolean";
    protected static final String SIMPLE_LIST = "a list";
    protected static final String SIMPLE_ENUM = "an enum '%s'";
    protected static final String UNKNOWN_TYPE = "type could not be established";

    private static final Map<List<Class<?>>, String> MISMATCH_MESSAGES = Map.of(
            List.of(Double.class, double.class, Float.class, float.class), SIMPLE_FLOATING,
            List.of(Integer.class, int.class, Long.class, long.class, Short.class, short.class, Byte.class, byte.class),
            SIMPLE_INTEGER,
            List.of(String.class), SIMPLE_STRING,
            List.of(Character.class, char.class), SIMPLE_CHAR,
            List.of(LocalDate.class), SIMPLE_DATE,
            List.of(Instant.class, ZonedDateTime.class, LocalDateTime.class), SIMPLE_DATE_TIME,
            List.of(UUID.class), SIMPLE_UUID,
            List.of(Boolean.class, boolean.class), SIMPLE_BOOLEAN,
            List.of(List.class), SIMPLE_LIST
    );

    public String map(Class<?> requiredType) {
        if (requiredType == null) {
            return "";
        }
        if (requiredType.isEnum()) {
            String enumValues = Arrays.toString(requiredType.getEnumConstants());
            return String.format(SIMPLE_ENUM, enumValues);
        }

        return MISMATCH_MESSAGES.entrySet().stream()
                .filter(e -> e.getKey().stream().anyMatch(requiredType::isAssignableFrom))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(UNKNOWN_TYPE);
    }
}
