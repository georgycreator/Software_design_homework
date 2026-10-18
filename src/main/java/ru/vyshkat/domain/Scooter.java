package ru.vyshkat.domain;

/** Электросамокат: прокатный транспорт и потребитель электроэнергии. */
public final class Scooter extends AbstractRentalVehicle implements EnergyConsumer {
    private final double dailyEnergyKwh;

    public Scooter(String inventoryNumber, int beginnerEase,
                   TechnicalCondition technicalCondition, double dailyEnergyKwh) {
        super(inventoryNumber, beginnerEase, technicalCondition);
        this.dailyEnergyKwh = DomainValidation.nonNegativeFiniteEnergy(dailyEnergyKwh);
    }

    @Override
    public String name() {
        return "Электросамокат";
    }

    @Override
    public double dailyEnergyKwh() {
        return dailyEnergyKwh;
    }
}
