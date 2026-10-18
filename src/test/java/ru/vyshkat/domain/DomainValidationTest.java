package ru.vyshkat.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DomainValidationTest {

    @Test
    void rejectsBeginnerEaseOutsideRange() {
        assertThrows(IllegalArgumentException.class,
                () -> new Bicycle("VK-B-1", 11, TechnicalCondition.READY_FOR_SERVICE));
    }

    @Test
    void rejectsNegativeEnergyConsumption() {
        assertThrows(IllegalArgumentException.class,
                () -> new Scooter("VK-S-1", 6, TechnicalCondition.READY_FOR_SERVICE, -0.1));
    }

    @Test
    void requiresInventoryNumber() {
        assertThrows(IllegalArgumentException.class,
                () -> new Helmet("  "));
    }

    @Test
    void inventoryNumberIsTrimmedAndCaseInsensitive() {
        assertEquals("VK-H-1", new Helmet("  vk-h-1 ").inventoryNumber());
    }

    @Test
    void beginnerRuleIsInclusiveAtThreshold() {
        assertTrue(new Bicycle("VK-B-6", 6, TechnicalCondition.READY_FOR_SERVICE).isBeginnerFriendly());
        assertFalse(new Bicycle("VK-B-5", 5, TechnicalCondition.READY_FOR_SERVICE).isBeginnerFriendly());
    }
}
