package ru.vyshkat.service;

import ru.vyshkat.domain.EnergyConsumer;
import ru.vyshkat.domain.InventoryItem;
import ru.vyshkat.domain.ParkEquipment;
import ru.vyshkat.domain.RentalVehicle;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Отчёты о состоянии парка; не меняет каталог и не выполняет техосмотр. */
public final class ParkReportService {
    private final ParkRegistry registry;

    public ParkReportService(ParkRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "Реестр парка обязателен");
    }

    public int totalInventoryCount() {
        return registry.itemCount();
    }

    public int equipmentCount() {
        return (int) registry.allItems().stream()
                .filter(ParkEquipment.class::isInstance)
                .count();
    }

    /** Суммирует все зарегистрированные активы, реализующие EnergyConsumer. */
    public double totalDailyEnergyKwh() {
        return registry.allItems().stream()
                .filter(EnergyConsumer.class::isInstance)
                .map(EnergyConsumer.class::cast)
                .mapToDouble(EnergyConsumer::dailyEnergyKwh)
                .sum();
    }

    /** Число единиц по наименованиям в порядке первого появления в реестре. */
    public Map<String, Long> countByName() {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (InventoryItem item : registry.allItems()) {
            counts.merge(item.name(), 1L, Long::sum);
        }
        return counts;
    }

    public List<RentalVehicle> beginnerFriendlyVehicles() {
        return registry.allVehicles().stream()
                .filter(RentalVehicle::isBeginnerFriendly)
                .toList();
    }

    public List<InventoryItem> allInventoryItems() {
        return registry.allItems();
    }

    public List<RentalVehicle> allVehicles() {
        return registry.allVehicles();
    }
}
