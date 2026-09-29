package com.monitoring.central;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class LimitsTest {

    private final Limits coldStore = new Limits(2, 8);

    @Test
    void outsideWhenTooCold() {
        assertTrue(coldStore.isOutside(1.9));
    }

    @Test
    void outsideWhenTooWarm() {
        assertTrue(coldStore.isOutside(8.1));
    }

    @Test
    void insideAtLimits() {
        assertFalse(coldStore.isOutside(2));
        assertFalse(coldStore.isOutside(8));
    }
}