package ru.vyshkat.di;

import org.junit.jupiter.api.Test;
import ru.vyshkat.domain.Bicycle;
import ru.vyshkat.domain.RentalVehicle;
import ru.vyshkat.domain.TechnicalCondition;
import ru.vyshkat.service.InspectionResult;
import ru.vyshkat.service.OperationResult;
import ru.vyshkat.service.ParkReportService;
import ru.vyshkat.service.ParkService;
import ru.vyshkat.service.ServiceCenter;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Подмена техосмотра именно через DI-контейнер: production-граф собирается как обычно,
 * заменяется одна зависимость, а ParkService получает её из контейнера.
 */
class ServiceCenterSubstitutionTest {

    @Test
    void containerInjectsFakeServiceCenterThatRejectsEverything() {
        CountingServiceCenter fake = new CountingServiceCenter(InspectionResult.rejected("fake: отказ"));
        ServiceContainer container = containerWith(fake);
        ParkService park = container.get(ParkService.class);

        // Исправный по полю condition велосипед отклонён: решает подставленный ServiceCenter.
        OperationResult result = park.admitVehicle(
                new Bicycle("VK-B-DI", 8, TechnicalCondition.READY_FOR_SERVICE));

        assertFalse(result.successful());
        assertTrue(result.message().contains("fake: отказ"));
        assertEquals(1, fake.calls);
        assertEquals(0, container.get(ParkReportService.class).totalInventoryCount());
    }

    @Test
    void containerInjectsFakeServiceCenterThatAcceptsEverything() {
        CountingServiceCenter fake = new CountingServiceCenter(InspectionResult.passed("fake: ок"));
        ServiceContainer container = containerWith(fake);
        ParkService park = container.get(ParkService.class);

        // Неисправный по полю condition велосипед принят: поле само по себе ничего не решает.
        OperationResult result = park.admitVehicle(
                new Bicycle("VK-B-DI2", 8, TechnicalCondition.NEEDS_REPAIR));

        assertTrue(result.successful());
        assertEquals(1, container.get(ParkReportService.class).totalInventoryCount());
    }

    private static ServiceContainer containerWith(ServiceCenter fake) {
        ServiceContainer container = new ServiceContainer();
        ApplicationWiring.registerDefaults(container, new Scanner("0\n"),
                new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8), false);
        container.override(ServiceCenter.class, () -> fake);
        return container;
    }

    private static final class CountingServiceCenter implements ServiceCenter {
        private final InspectionResult answer;
        private int calls;

        private CountingServiceCenter(InspectionResult answer) {
            this.answer = answer;
        }

        @Override
        public InspectionResult inspect(RentalVehicle vehicle) {
            calls++;
            return answer;
        }
    }
}
