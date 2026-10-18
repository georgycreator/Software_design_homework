package ru.vyshkat.domain;

/** Результат предварительной диагностики, на основании которой ServiceCenter принимает решение. */
public enum TechnicalCondition {
    READY_FOR_SERVICE,
    NEEDS_REPAIR
}
