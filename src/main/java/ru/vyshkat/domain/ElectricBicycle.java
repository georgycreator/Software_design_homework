package ru.vyshkat.domain;

/** Электровелосипед: прокатный транспорт и потребитель электроэнергии. */
public final class ElectricBicycle extends AbstractRentalVehicle implements EnergyConsumer {
    private final double dailyEnergyKwh;

    public ElectricBicycle(String inventoryNumber, int beginnerEase,
                           TechnicalCondition technicalCondition, double dailyEnergyKwh) {
        super(inventoryNumber, beginnerEase, technicalCondition);
        this.dailyEnergyKwh = DomainValidation.nonNegativeFiniteEnergy(dailyEnergyKwh);
    }

    @Override
    public String name() {
        return "Электровелосипед";
    }

    @Override
    public double dailyEnergyKwh() {
        return dailyEnergyKwh;
    }
}
