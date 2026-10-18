package ru.vyshkat.service;

import org.junit.jupiter.api.Test;
import ru.vyshkat.domain.Bicycle;
import ru.vyshkat.domain.ChargingCabinet;
import ru.vyshkat.domain.ChargingStation;
import ru.vyshkat.domain.ElectricBicycle;
import ru.vyshkat.domain.Helmet;
import ru.vyshkat.domain.InventoryItem;
import ru.vyshkat.domain.RentalVehicle;
import ru.vyshkat.domain.Scooter;
import ru.vyshkat.domain.TechnicalCondition;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParkReportServiceTest {

    @Test
    void sumsEnergyAcrossVehiclesAndEquipmentThatConsumeEnergy() {
        ParkRegistry registry = new ParkRegistry();
        ParkService park = new ParkService(new DefaultServiceCenter(), registry);
        park.admitVehicle(new Scooter("VK-S-1", 8, TechnicalCondition.READY_FOR_SERVICE, 1.4));
        park.admitVehicle(new ElectricBicycle("VK-EB-1", 5, TechnicalCondition.READY_FOR_SERVICE, 0.8));
        park.admitVehicle(new Bicycle("VK-B-1", 7, TechnicalCondition.READY_FOR_SERVICE));
        park.addEquipment(new ChargingStation("VK-CS-1", 0.6));
        park.addEquipment(new ChargingCabinet("VK-CC-1", 1.1));
        park.addEquipment(new Helmet("VK-H-1"));

        ParkReportService reports = new ParkReportService(registry);

        assertEquals(3.9, reports.totalDailyEnergyKwh(), 0.000001);
    }

    @Test
    void beginnerThresholdIsInclusiveAndOnlyAppliesToRentalVehicles() {
        ParkRegistry registry = new ParkRegistry();
        ParkService park = new ParkService(new DefaultServiceCenter(), registry);
        park.admitVehicle(new Scooter("VK-S-6", 6, TechnicalCondition.READY_FOR_SERVICE, 1.0));
        park.admitVehicle(new Scooter("VK-S-5", 5, TechnicalCondition.READY_FOR_SERVICE, 1.0));
        park.admitVehicle(new Bicycle("VK-B-9", 9, TechnicalCondition.READY_FOR_SERVICE));
        park.addEquipment(new Helmet("VK-H-10"));

        List<RentalVehicle> result = new ParkReportService(registry).beginnerFriendlyVehicles();

        assertEquals(List.of("VK-S-6", "VK-B-9"),
                result.stream().map(RentalVehicle::inventoryNumber).toList());
    }

    @Test
    void inventoryReportIncludesVehiclesAndEquipment() {
        ParkRegistry registry = new ParkRegistry();
        ParkService park = new ParkService(new DefaultServiceCenter(), registry);
        park.admitVehicle(new Bicycle("VK-B-1", 7, TechnicalCondition.READY_FOR_SERVICE));
        park.addEquipment(new Helmet("VK-H-1"));

        ParkReportService reports = new ParkReportService(registry);

        assertEquals(2, reports.totalInventoryCount());
        assertEquals(1, reports.equipmentCount());
        assertEquals(List.of("VK-B-1", "VK-H-1"), reports.allInventoryItems().stream()
                .map(item -> item.inventoryNumber()).toList());
    }

    @Test
    void equipmentCountIgnoresInventoryItemsThatAreNeitherVehiclesNorEquipment() {
        ParkRegistry registry = new ParkRegistry();
        ParkService park = new ParkService(new DefaultServiceCenter(), registry);
        park.admitVehicle(new Bicycle("VK-B-1", 7, TechnicalCondition.READY_FOR_SERVICE));
        park.addEquipment(new Helmet("VK-H-1"));
        // Будущий вид актива (например, запчасть), который пока не является ни транспортом, ни вещью.
        registry.register(new InventoryItem() {
            @Override
            public String inventoryNumber() {
                return "VK-SP-1";
            }

            @Override
            public String name() {
                return "Запчасть";
            }
        });

        ParkReportService reports = new ParkReportService(registry);

        assertEquals(3, reports.totalInventoryCount());
        assertEquals(1, reports.equipmentCount());
    }

    @Test
    void countByNameGroupsItemsInRegistrationOrder() {
        ParkRegistry registry = new ParkRegistry();
        ParkService park = new ParkService(new DefaultServiceCenter(), registry);
        park.admitVehicle(new Scooter("VK-S-1", 8, TechnicalCondition.READY_FOR_SERVICE, 1.0));
        park.addEquipment(new Helmet("VK-H-1"));
        park.admitVehicle(new Scooter("VK-S-2", 8, TechnicalCondition.READY_FOR_SERVICE, 1.0));

        var counts = new ParkReportService(registry).countByName();

        assertEquals(List.of("Электросамокат", "Шлем"), List.copyOf(counts.keySet()));
        assertEquals(2L, counts.get("Электросамокат"));
        assertEquals(1L, counts.get("Шлем"));
    }

    @Test
    void inventoryNumbersDifferingOnlyByCaseAreDuplicates() {
        ParkRegistry registry = new ParkRegistry();
        ParkService park = new ParkService(new DefaultServiceCenter(), registry);
        assertTrue(park.addEquipment(new Helmet("vk-h-1")).successful());

        assertFalse(park.addEquipment(new Helmet("VK-H-1")).successful());
        assertEquals(1, new ParkReportService(registry).totalInventoryCount());
    }
}
