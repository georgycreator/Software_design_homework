package ru.vyshkat.service;

import ru.vyshkat.domain.RentalVehicle;

/** Абстракция техосмотра: её можно заменить fake/mock в тесте. */
public interface ServiceCenter {
    InspectionResult inspect(RentalVehicle vehicle);
}
