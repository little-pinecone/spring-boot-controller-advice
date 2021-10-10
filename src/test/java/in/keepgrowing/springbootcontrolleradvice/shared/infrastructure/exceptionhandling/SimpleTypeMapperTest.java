package in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.exceptionhandling;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;

class SimpleTypeMapperTest {

    private static final List<Class<?>> TYPES = List.of(byte.class, short.class, int.class, long.class, char.class,
            boolean.class, Integer.class, Long.class, Double.class, Float.class, Instant.class, ZonedDateTime.class,
            LocalDateTime.class, LocalDate.class, String.class, Character.class, UUID.class, Boolean.class, List.class);

    private static final Pattern UNKNOWN_TYPE = Pattern.compile(SimpleTypeMapper.UNKNOWN_TYPE);

    private SimpleTypeMapper typeProvider;

    @BeforeEach
    void setUp() {
        typeProvider = new SimpleTypeMapper();
    }

    @Test
    void shouldProvideDetailsForMismatchedInputExceptionForSupportedTypes() {
        TYPES.forEach(type -> {
            String actual = typeProvider.map(type);
            Matcher bicMatcher = UNKNOWN_TYPE.matcher(actual);

            assertFalse(bicMatcher.find(), "Unhandled type: " + type.getName());
        });
    }
}