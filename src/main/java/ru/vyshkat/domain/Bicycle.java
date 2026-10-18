package ru.vyshkat.domain;

/** Обычный велосипед не потребляет электроэнергию, поэтому EnergyConsumer не реализуется. */
public final class Bicycle extends AbstractRentalVehicle {
    public Bicycle(String inventoryNumber, int beginnerEase, TechnicalCondition technicalCondition) {
        super(inventoryNumber, beginnerEase, technicalCondition);
    }

    @Override
    public String name() {
        return "Велосипед";
    }
}
