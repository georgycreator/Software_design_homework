package ru.vyshkat.service;

import ru.vyshkat.domain.ParkEquipment;
import ru.vyshkat.domain.RentalVehicle;

import java.util.Objects;

/** Сценарии изменения парка: транспорт проходит техосмотр, вещи получают инвентарный номер. */
public final class ParkService {
    private final ServiceCenter serviceCenter;
    private final ParkRegistry registry;

    public ParkService(ServiceCenter serviceCenter, ParkRegistry registry) {
        this.serviceCenter = Objects.requireNonNull(serviceCenter, "ServiceCenter обязателен");
        this.registry = Objects.requireNonNull(registry, "Реестр парка обязателен");
    }

    public OperationResult admitVehicle(RentalVehicle vehicle) {
        Objects.requireNonNull(vehicle, "Транспорт обязателен");
        if (registry.containsInventoryNumber(vehicle.inventoryNumber())) {
            return OperationResult.failure("Отклонено: инвентарный номер "
                    + vehicle.inventoryNumber() + " уже используется");
        }

        InspectionResult inspection = serviceCenter.inspect(vehicle);
        if (!inspection.passed()) {
            return OperationResult.failure("Транспорт " + vehicle.inventoryNumber()
                    + " не принят. " + inspection.message());
        }

        registry.register(vehicle);
        return OperationResult.success("Транспорт " + vehicle.inventoryNumber()
                + " принят. " + inspection.message());
    }

    public OperationResult addEquipment(ParkEquipment equipment) {
        Objects.requireNonNull(equipment, "Вещь обязательна");
        if (registry.containsInventoryNumber(equipment.inventoryNumber())) {
            return OperationResult.failure("Не добавлено: инвентарный номер "
                    + equipment.inventoryNumber() + " уже используется");
        }
        registry.register(equipment);
        return OperationResult.success("Добавлено на баланс: " + equipment.name()
                + " [" + equipment.inventoryNumber() + "]");
    }
}
