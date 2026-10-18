package ru.vyshkat.domain;

/** Защитная экипировка, учитываемая на балансе парка. */
public final class Helmet implements ParkEquipment {
    private final String inventoryNumber;

    public Helmet(String inventoryNumber) {
        this.inventoryNumber = InventoryItem.normalize(inventoryNumber);
    }

    @Override
    public String inventoryNumber() {
        return inventoryNumber;
    }

    @Override
    public String name() {
        return "Шлем";
    }
}
