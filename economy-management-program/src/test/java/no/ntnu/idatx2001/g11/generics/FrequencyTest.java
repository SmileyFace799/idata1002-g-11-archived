package no.ntnu.idatx2001.g11.generics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.ntnu.idatx2001.g11.enums.TimeType;

import static org.junit.jupiter.api.Assertions.*;

class FrequencyTest {
    private Frequency validFrequency;

    @BeforeEach
    void before() {
        validFrequency = new Frequency(15, TimeType.DAYS);
    }

    @Test
    void testCreationOfFrequencyWithInvalidAmount() {
        assertThrows(IllegalArgumentException.class, () -> new Frequency(0, TimeType.DAYS));
    }

    @Test
    void testCreationOfFrequencyWithNoTimeType() {
        assertThrows(IllegalArgumentException.class, () -> new Frequency(15, null));
    }

    @Test
    void testValidCreationOfFrequency() {
        assertEquals(15, validFrequency.getAmount());
        assertEquals(TimeType.DAYS, validFrequency.getTimeType());
    }

    @Test
    void testFrequencyEquals() {
        assertEquals(validFrequency, validFrequency);
        assertNotEquals(null, validFrequency);
        assertNotEquals(new Object(), validFrequency);

        assertNotEquals(new Frequency(2, TimeType.DAYS), validFrequency);
        assertNotEquals(new Frequency(15, TimeType.YEARS), validFrequency);

        assertEquals(new Frequency(15, TimeType.DAYS), validFrequency);
    }
}
