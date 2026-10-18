package ru.vyshkat.service;

import org.junit.jupiter.api.Test;
import ru.vyshkat.domain.ChargingStation;
import ru.vyshkat.domain.Helmet;
import ru.vyshkat.domain.RentalVehicle;
import ru.vyshkat.domain.Scooter;
import ru.vyshkat.domain.TechnicalCondition;

import static org.junit.jupiter.api.Assertions.*;

class ParkServiceTest {

    @Test
    void registersVehicleOnlyWhenInjectedInspectionPasses() {
        FakeServiceCenter serviceCenter = new FakeServiceCenter(InspectionResult.passed("fake: ok"));
        ParkRegistry registry = new ParkRegistry();
        ParkService service = new ParkService(serviceCenter, registry);
        RentalVehicle scooter = scooter("VK-100", TechnicalCondition.NEEDS_REPAIR);

        OperationResult result = service.admitVehicle(scooter);

        assertTrue(result.successful());
        assertEquals(1, serviceCenter.calls);
        assertSame(scooter, registry.findByInventoryNumber("VK-100").orElseThrow());
    }

    @Test
    void findByInventoryNumberIgnoresCaseAndSurroundingSpaces() {
        ParkRegistry registry = new ParkRegistry();
        ParkService service = new ParkService(new FakeServiceCenter(InspectionResult.passed("ok")), registry);
        RentalVehicle scooter = scooter("VK-100", TechnicalCondition.READY_FOR_SERVICE);
        service.admitVehicle(scooter);

        assertSame(scooter, registry.findByInventoryNumber("vk-100").orElseThrow());
        assertSame(scooter, registry.findByInventoryNumber("  Vk-100 ").orElseThrow());
        assertTrue(registry.findByInventoryNumber("   ").isEmpty());
        assertTrue(registry.findByInventoryNumber(null).isEmpty());
        assertTrue(registry.findByInventoryNumber("VK-999").isEmpty());
    }

    @Test
    void rejectsVehicleWhenInjectedInspectionFails() {
        FakeServiceCenter serviceCenter = new FakeServiceCenter(
                InspectionResult.rejected("fake: dangerous brakes"));
        ParkRegistry registry = new ParkRegistry();
        ParkService service = new ParkService(serviceCenter, registry);

        OperationResult result = service.admitVehicle(scooter("VK-101", TechnicalCondition.READY_FOR_SERVICE));

        assertFalse(result.successful());
        assertTrue(result.message().contains("dangerous brakes"));
        assertEquals(1, serviceCenter.calls);
        assertTrue(registry.allItems().isEmpty());
    }

    @Test
    void duplicateInventoryNumberIsRejectedWithoutSecondInspection() {
        FakeServiceCenter serviceCenter = new FakeServiceCenter(InspectionResult.passed("fake: ok"));
        ParkRegistry registry = new ParkRegistry();
        ParkService service = new ParkService(serviceCenter, registry);
        assertTrue(service.admitVehicle(scooter("VK-102", TechnicalCondition.READY_FOR_SERVICE)).successful());

        OperationResult second = service.admitVehicle(scooter("VK-102", TechnicalCondition.READY_FOR_SERVICE));

        assertFalse(second.successful());
        assertTrue(second.message().contains("уже используется"));
        assertEquals(1, serviceCenter.calls);
        assertEquals(1, registry.itemCount());
    }

    @Test
    void defaultServiceCenterRejectsVehicleMarkedForRepair() {
        ParkRegistry registry = new ParkRegistry();
        ParkService service = new ParkService(new DefaultServiceCenter(), registry);

        OperationResult result = service.admitVehicle(scooter("VK-103", TechnicalCondition.NEEDS_REPAIR));

        assertFalse(result.successful());
        assertTrue(result.message().contains("требуется ремонт"));
        assertEquals(0, registry.itemCount());
    }

    @Test
    void equipmentIsAddedWithoutVehicleInspection() {
        FakeServiceCenter serviceCenter = new FakeServiceCenter(InspectionResult.passed("fake: ok"));
        ParkRegistry registry = new ParkRegistry();
        ParkService service = new ParkService(serviceCenter, registry);

        OperationResult result = service.addEquipment(new ChargingStation("VK-CS-1", 0.4));

        assertTrue(result.successful());
        assertEquals(0, serviceCenter.calls);
        assertEquals(1, registry.itemCount());
    }

    @Test
    void equipmentCannotReuseInventoryNumberOfVehicle() {
        FakeServiceCenter serviceCenter = new FakeServiceCenter(InspectionResult.passed("fake: ok"));
        ParkRegistry registry = new ParkRegistry();
        ParkService service = new ParkService(serviceCenter, registry);
        assertTrue(service.admitVehicle(scooter("VK-SHARED", TechnicalCondition.READY_FOR_SERVICE)).successful());

        OperationResult result = service.addEquipment(new Helmet("VK-SHARED"));

        assertFalse(result.successful());
        assertTrue(result.message().contains("уже используется"));
        assertEquals(1, registry.itemCount());
    }

    @Test
    void vehicleCannotReuseInventoryNumberOfEquipmentAndIsNotInspected() {
        FakeServiceCenter serviceCenter = new FakeServiceCenter(InspectionResult.passed("fake: ok"));
        ParkRegistry registry = new ParkRegistry();
        ParkService service = new ParkService(serviceCenter, registry);
        assertTrue(service.addEquipment(new Helmet("VK-SHARED")).successful());

        OperationResult result = service.admitVehicle(scooter("VK-SHARED", TechnicalCondition.READY_FOR_SERVICE));

        assertFalse(result.successful());
        assertTrue(result.message().contains("уже используется"));
        assertEquals(0, serviceCenter.calls);
        assertEquals(1, registry.itemCount());
    }

    @Test
    void equipmentCannotReuseInventoryNumberOfOtherEquipment() {
        ParkRegistry registry = new ParkRegistry();
        ParkService service = new ParkService(new DefaultServiceCenter(), registry);
        assertTrue(service.addEquipment(new Helmet("VK-H-1")).successful());

        assertFalse(service.addEquipment(new ChargingStation("VK-H-1", 0.3)).successful());
        assertEquals(1, registry.itemCount());
    }

    private static Scooter scooter(String id, TechnicalCondition condition) {
        return new Scooter(id, 7, condition, 1.2);
    }

    private static final class FakeServiceCenter implements ServiceCenter {
        private final InspectionResult result;
        private int calls;

        private FakeServiceCenter(InspectionResult result) {
            this.result = result;
        }

        @Override
        public InspectionResult inspect(RentalVehicle vehicle) {
            calls++;
            return result;
        }
    }
}
