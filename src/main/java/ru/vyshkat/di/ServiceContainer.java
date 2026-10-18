package ru.vyshkat.di;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Минимальный DI-контейнер приложения: поддерживает регистрации и ленивые singleton-объекты.
 * Используется только в Composition Root; бизнес-классы о контейнере ничего не знают.
 */
public final class ServiceContainer {
    private final Map<Class<?>, Supplier<?>> providers = new HashMap<>();
    private final Map<Class<?>, Object> singletons = new HashMap<>();
    private final Set<Class<?>> currentlyCreating = new HashSet<>();

    public <T> void registerSingleton(Class<T> type, Supplier<? extends T> provider) {
        Objects.requireNonNull(type, "Тип сервиса обязателен");
        Objects.requireNonNull(provider, "Фабрика сервиса обязательна");
        if (providers.containsKey(type) || singletons.containsKey(type)) {
            throw new IllegalStateException("Сервис уже зарегистрирован: " + type.getName());
        }
        providers.put(type, provider);
    }

    /**
     * Заменяет уже зарегистрированную реализацию (например, на фейк в тесте).
     * Заменять можно только до первого получения сервиса: иначе часть графа уже
     * держит старый экземпляр, и тихая подмена дала бы несогласованное состояние.
     */
    public <T> void override(Class<T> type, Supplier<? extends T> provider) {
        Objects.requireNonNull(type, "Тип сервиса обязателен");
        Objects.requireNonNull(provider, "Фабрика сервиса обязательна");
        if (!providers.containsKey(type)) {
            throw new IllegalArgumentException("Нечего заменять, сервис не зарегистрирован: " + type.getName());
        }
        if (singletons.containsKey(type)) {
            throw new IllegalStateException("Сервис уже создан, заменить нельзя: " + type.getName());
        }
        providers.put(type, provider);
    }

    public <T> T get(Class<T> type) {
        Objects.requireNonNull(type, "Тип сервиса обязателен");
        Object instance = singletons.get(type);
        if (instance != null) {
            return type.cast(instance);
        }

        Supplier<?> provider = providers.get(type);
        if (provider == null) {
            throw new IllegalArgumentException("Сервис не зарегистрирован: " + type.getName());
        }
        if (!currentlyCreating.add(type)) {
            throw new IllegalStateException("Обнаружена циклическая зависимость при создании " + type.getName());
        }
        try {
            Object created = Objects.requireNonNull(provider.get(), "Провайдер вернул null для " + type.getName());
            if (!type.isInstance(created)) {
                throw new IllegalStateException("Провайдер вернул объект неправильного типа для " + type.getName());
            }
            singletons.put(type, created);
            return type.cast(created);
        } finally {
            currentlyCreating.remove(type);
        }
    }
}
