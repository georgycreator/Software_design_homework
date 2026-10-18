package ru.vyshkat.di;

import ru.vyshkat.app.ConsoleApplication;
import ru.vyshkat.service.DefaultServiceCenter;
import ru.vyshkat.service.ParkRegistry;
import ru.vyshkat.service.ParkReportService;
import ru.vyshkat.service.ParkService;
import ru.vyshkat.service.ServiceCenter;

import java.io.PrintStream;
import java.util.Scanner;

/** Composition Root: единственное место, где выбираются конкретные реализации зависимостей. */
public final class ApplicationWiring {
    private ApplicationWiring() {
    }

    /** Боевая сборка на стандартных потоках; кодировку вывода определяет консоль/JVM (см. README). */
    public static ServiceContainer createContainer() {
        return createContainer(new Scanner(System.in), System.out, Boolean.getBoolean("vyshkat.echoInput"));
    }

    /** Сборка с управляемыми потоками (для тестов и повторяемых прогонов). */
    public static ServiceContainer createContainer(Scanner scanner, PrintStream output, boolean echoInput) {
        ServiceContainer container = new ServiceContainer();
        registerDefaults(container, scanner, output, echoInput);
        return container;
    }

    /**
     * Регистрирует production-реализации. Вынесено отдельно, чтобы тест мог создать контейнер,
     * вызвать {@link ServiceContainer#override} для одной зависимости и получить остальной граф «как в проде».
     */
    public static void registerDefaults(ServiceContainer container, Scanner scanner,
                                        PrintStream output, boolean echoInput) {
        container.registerSingleton(ParkRegistry.class, ParkRegistry::new);
        container.registerSingleton(ServiceCenter.class, DefaultServiceCenter::new);
        container.registerSingleton(ParkService.class,
                () -> new ParkService(container.get(ServiceCenter.class), container.get(ParkRegistry.class)));
        container.registerSingleton(ParkReportService.class,
                () -> new ParkReportService(container.get(ParkRegistry.class)));
        container.registerSingleton(ConsoleApplication.class,
                () -> new ConsoleApplication(container.get(ParkService.class),
                        container.get(ParkReportService.class), scanner, output, echoInput));
    }
}
