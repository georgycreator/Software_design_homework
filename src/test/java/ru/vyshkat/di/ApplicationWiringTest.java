package ru.vyshkat.di;

import org.junit.jupiter.api.Test;
import ru.vyshkat.domain.Bicycle;
import ru.vyshkat.domain.TechnicalCondition;
import ru.vyshkat.service.ParkReportService;
import ru.vyshkat.service.ParkService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ApplicationWiringTest {
    @Test
    void containerWiresServicesToSharedRegistry() {
        ServiceContainer container = ApplicationWiring.createContainer(
                new Scanner("0\n"), new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8), false);
        ParkService park = container.get(ParkService.class);
        ParkReportService reports = container.get(ParkReportService.class);

        park.admitVehicle(new Bicycle("VK-B-DI", 7, TechnicalCondition.READY_FOR_SERVICE));

        assertSame(park, container.get(ParkService.class));
        assertEquals(1, reports.totalInventoryCount());
    }
}
