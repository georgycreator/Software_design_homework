package ru.vyshkat.di;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServiceContainerTest {
    @Test
    void lazilyCreatesSingletonOnlyOnce() {
        ServiceContainer container = new ServiceContainer();
        int[] constructions = {0};
        container.registerSingleton(TestService.class, () -> {
            constructions[0]++;
            return new TestService();
        });

        TestService first = container.get(TestService.class);
        TestService second = container.get(TestService.class);

        assertSame(first, second);
        assertEquals(1, constructions[0]);
    }

    @Test
    void failsFastForUnregisteredService() {
        ServiceContainer container = new ServiceContainer();
        assertThrows(IllegalArgumentException.class, () -> container.get(TestService.class));
    }

    @Test
    void rejectsDuplicateRegistration() {
        ServiceContainer container = new ServiceContainer();
        container.registerSingleton(TestService.class, TestService::new);
        assertThrows(IllegalStateException.class,
                () -> container.registerSingleton(TestService.class, TestService::new));
    }

    @Test
    void overrideReplacesProviderBeforeFirstResolve() {
        ServiceContainer container = new ServiceContainer();
        container.registerSingleton(TestService.class, TestService::new);
        TestService replacement = new TestService();

        container.override(TestService.class, () -> replacement);

        assertSame(replacement, container.get(TestService.class));
    }

    @Test
    void overrideAfterResolveIsForbidden() {
        ServiceContainer container = new ServiceContainer();
        container.registerSingleton(TestService.class, TestService::new);
        container.get(TestService.class);
        assertThrows(IllegalStateException.class,
                () -> container.override(TestService.class, TestService::new));
    }

    @Test
    void overrideOfUnknownServiceFails() {
        ServiceContainer container = new ServiceContainer();
        assertThrows(IllegalArgumentException.class,
                () -> container.override(TestService.class, TestService::new));
    }

    @Test
    void detectsCircularDependency() {
        ServiceContainer container = new ServiceContainer();
        container.registerSingleton(TestService.class, () -> {
            container.get(TestService.class);
            return new TestService();
        });
        assertThrows(IllegalStateException.class, () -> container.get(TestService.class));
    }

    private static final class TestService {
    }
}
