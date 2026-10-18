package ru.vyshkat.domain;

/** Повторно используемые проверки доменных значений, чтобы одинаковые правила не расходились. */
final class DomainValidation {
    private DomainValidation() {
    }

    static double nonNegativeFiniteEnergy(double value) {
        if (!Double.isFinite(value) || value < 0) {
            throw new IllegalArgumentException(
                    "Суточный расход энергии не может быть отрицательным или бесконечным");
        }
        return value;
    }
}
