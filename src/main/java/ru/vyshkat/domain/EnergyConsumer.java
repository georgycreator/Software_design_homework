package ru.vyshkat.domain;

/** Роль объектов, у которых учитывается суточное потребление электроэнергии, кВт·ч. */
public interface EnergyConsumer {
    double dailyEnergyKwh();
}
