package ru.vyshkat.domain;

import java.util.Objects;

/** Общая реализация неизменяемых характеристик прокатного транспорта. */
public abstract class AbstractRentalVehicle implements RentalVehicle {
    private final String inventoryNumber;
    private final int beginnerEase;
    private final TechnicalCondition technicalCondition;

    protected AbstractRentalVehicle(
            String inventoryNumber,
            int beginnerEase,
            TechnicalCondition technicalCondition) {
        this.inventoryNumber = InventoryItem.normalize(inventoryNumber);
        if (beginnerEase < 1 || beginnerEase > 10) {
            throw new IllegalArgumentException("Простота для новичка должна быть от 1 до 10");
        }
        this.beginnerEase = beginnerEase;
        this.technicalCondition = Objects.requireNonNull(technicalCondition, "Техническое состояние обязательно");
    }

    @Override
    public final String inventoryNumber() {
        return inventoryNumber;
    }

    @Override
    public final int beginnerEase() {
        return beginnerEase;
    }

    @Override
    public final TechnicalCondition technicalCondition() {
        return technicalCondition;
    }
}
