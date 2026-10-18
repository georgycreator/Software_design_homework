package ru.vyshkat.service;

import ru.vyshkat.domain.RentalVehicle;
import ru.vyshkat.domain.TechnicalCondition;

/** Простая доменная политика допуска транспорта в активный парк. */
public final class DefaultServiceCenter implements ServiceCenter {
    @Override
    public InspectionResult inspect(RentalVehicle vehicle) {
        if (vehicle.technicalCondition() == TechnicalCondition.NEEDS_REPAIR) {
            return InspectionResult.rejected("Отклонено техосмотром: требуется ремонт");
        }
        return InspectionResult.passed("Техосмотр пройден");
    }
}
