package ru.vyshkat.domain;

/** Зарядная станция: вещь на балансе и потребитель энергии. */
public final class ChargingStation implements ParkEquipment, EnergyConsumer {
    private final String inventoryNumber;
    private final double dailyEnergyKwh;

    public ChargingStation(String inventoryNumber, double dailyEnergyKwh) {
        this.inventoryNumber = InventoryItem.normalize(inventoryNumber);
        this.dailyEnergyKwh = DomainValidation.nonNegativeFiniteEnergy(dailyEnergyKwh);
    }

    @Override
    public String inventoryNumber() {
        return inventoryNumber;
    }

    @Override
    public String name() {
        return "Зарядная станция";
    }

    @Override
    public double dailyEnergyKwh() {
        return dailyEnergyKwh;
    }
}
