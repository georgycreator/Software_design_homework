package ru.vyshkat.service;

/** Решение техосмотра и объяснение для оператора. */
public record InspectionResult(boolean passed, String message) {
    public InspectionResult {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("У результата техосмотра должно быть пояснение");
        }
    }

    public static InspectionResult passed(String message) {
        return new InspectionResult(true, message);
    }

    public static InspectionResult rejected(String message) {
        return new InspectionResult(false, message);
    }
}
