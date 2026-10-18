package ru.vyshkat.app;

import ru.vyshkat.di.ApplicationWiring;
import ru.vyshkat.di.ServiceContainer;

/** Точка входа в интерактивное консольное приложение. */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        ServiceContainer container = ApplicationWiring.createContainer();
        container.get(ConsoleApplication.class).run();
    }
}
