package ru.vyshkat.service;

/** Итог операции добавления актива в парк. */
public record OperationResult(boolean successful, String message) {
    public OperationResult {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("У результата операции должно быть пояснение");
        }
    }

    public static OperationResult success(String message) {
        return new OperationResult(true, message);
    }

    public static OperationResult failure(String message) {
        return new OperationResult(false, message);
    }
}
