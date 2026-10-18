package ru.vyshkat.domain;

/** Зарядный шкаф: вещь на балансе и потребитель энергии. */
public final class ChargingCabinet implements ParkEquipment, EnergyConsumer {
    private final String inventoryNumber;
    private final double dailyEnergyKwh;

    public ChargingCabinet(String inventoryNumber, double dailyEnergyKwh) {
        this.inventoryNumber = InventoryItem.normalize(inventoryNumber);
        this.dailyEnergyKwh = DomainValidation.nonNegativeFiniteEnergy(dailyEnergyKwh);
    }

    @Override
    public String inventoryNumber() {
        return inventoryNumber;
    }

    @Override
    public String name() {
        return "Зарядный шкаф";
    }

    @Override
    public double dailyEnergyKwh() {
        return dailyEnergyKwh;
    }
}
