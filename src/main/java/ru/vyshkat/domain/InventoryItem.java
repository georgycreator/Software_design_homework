package ru.vyshkat.domain;

import java.util.Locale;

/** Объект, находящийся на балансе парка и имеющий уникальный инвентарный номер. */
public interface InventoryItem {
    /** Номер в нормализованном виде (см. {@link #normalize(String)}). */
    String inventoryNumber();

    String name();

    /**
     * Единое правило приведения инвентарного номера к каноническому виду: без пробелов по краям,
     * в верхнем регистре. Им пользуются и сущности при создании, и реестр при поиске.
     */
    static String normalize(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Инвентарный номер не может быть пустым");
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }
}
