package ru.vyshkat.domain;

/** Транспорт, который можно выдавать в аренду. */
public interface RentalVehicle extends InventoryItem {
    /** Минимальная простота (включительно), с которой транспорт можно выдавать новичкам. */
    int MIN_BEGINNER_EASE = 6;

    /** Условная оценка простоты управления в диапазоне 1–10. */
    int beginnerEase();

    TechnicalCondition technicalCondition();

    /** Правило допуска новичков живёт рядом с данными, на которых оно основано. */
    default boolean isBeginnerFriendly() {
        return beginnerEase() >= MIN_BEGINNER_EASE;
    }
}
