package ru.vyshkat.service;

import ru.vyshkat.domain.InventoryItem;
import ru.vyshkat.domain.RentalVehicle;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** In-memory каталог активов. Изменение каталога выполняется через ParkService. */
public final class ParkRegistry {
    private final Map<String, InventoryItem> itemsByInventoryNumber = new LinkedHashMap<>();

    boolean containsInventoryNumber(String inventoryNumber) {
        return itemsByInventoryNumber.containsKey(key(inventoryNumber));
    }

    void register(InventoryItem item) {
        if (containsInventoryNumber(item.inventoryNumber())) {
            throw new IllegalStateException("Инвентарный номер уже зарегистрирован: " + item.inventoryNumber());
        }
        itemsByInventoryNumber.put(item.inventoryNumber(), item);
    }

    public List<InventoryItem> allItems() {
        return List.copyOf(itemsByInventoryNumber.values());
    }

    public List<RentalVehicle> allVehicles() {
        List<RentalVehicle> vehicles = new ArrayList<>();
        for (InventoryItem item : itemsByInventoryNumber.values()) {
            if (item instanceof RentalVehicle vehicle) {
                vehicles.add(vehicle);
            }
        }
        return List.copyOf(vehicles);
    }

    /** Поиск не зависит от регистра и пробелов по краям; пустой запрос ничего не находит. */
    public Optional<InventoryItem> findByInventoryNumber(String inventoryNumber) {
        return Optional.ofNullable(itemsByInventoryNumber.get(key(inventoryNumber)));
    }

    private static String key(String rawInventoryNumber) {
        if (rawInventoryNumber == null || rawInventoryNumber.isBlank()) {
            return null;
        }
        return InventoryItem.normalize(rawInventoryNumber);
    }

    public int itemCount() {
        return itemsByInventoryNumber.size();
    }
}
