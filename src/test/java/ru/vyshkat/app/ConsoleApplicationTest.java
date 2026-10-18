package ru.vyshkat.app;

import org.junit.jupiter.api.Test;
import ru.vyshkat.service.DefaultServiceCenter;
import ru.vyshkat.service.ParkRegistry;
import ru.vyshkat.service.ParkReportService;
import ru.vyshkat.service.ParkService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleApplicationTest {

    @Test
    void menuCanAddVehicleAndShowReports() {
        String input = String.join("\n",
                "1", "1", "VK-S-CLI", "6", "1", "1.4",
                "3", "4", "5", "6", "0", "");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ConsoleApplication app = application(input, bytes);

        app.run();

        String output = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Транспорт VK-S-CLI принят"));
        assertTrue(output.contains("Суточное энергопотребление: 1.40 кВт·ч"));
        assertTrue(output.contains("VK-S-CLI"));
        assertTrue(output.contains("Электросамокат"));
    }

    @Test
    void menuShowsRejectedVehicleAndAddsEquipment() {
        String input = String.join("\n",
                "1", "1", "VK-S-BAD", "8", "2", "1.2",
                "2", "3", "VK-CC-CLI", "0.5",
                "3", "0", "");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ConsoleApplication app = application(input, bytes);

        app.run();

        String output = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("не принят"));
        assertTrue(output.contains("Зарядный шкаф"));
        assertTrue(output.contains("Всего инвентарных единиц: 1"));
        assertTrue(output.contains("0.50 кВт·ч"));
    }


    @Test
    void invalidVehicleTypeIsRejectedBeforeAskingForDetails() {
        String input = String.join("\n", "1", "99", "0", "");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        application(input, bytes).run();

        String output = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Неизвестный тип транспорта: 99"));
        assertFalse(output.contains("Инвентарный номер:"));
        assertTrue(output.contains("До свидания"));
    }

    @Test
    void invalidEquipmentTypeIsRejectedBeforeAskingForDetails() {
        String input = String.join("\n", "2", "99", "0", "");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        application(input, bytes).run();

        String output = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Неизвестный тип вещи: 99"));
        assertFalse(output.contains("Инвентарный номер:"));
        assertTrue(output.contains("До свидания"));
    }

    @Test
    void endOfInputDuringVehicleEntryExitsGracefullyWithoutRegisteringVehicle() {
        String input = String.join("\n", "1", "1", "VK-S-EOF", "");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ConsoleApplication app = application(input, bytes);

        app.run();

        String output = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Ввод завершён. Приложение закрыто."));
        assertFalse(output.contains("Транспорт VK-S-EOF принят"));
    }

    @Test
    void invalidNumberShowsErrorAndReturnsToMenu() {
        String input = String.join("\n", "1", "not-a-number", "0", "");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        application(input, bytes).run();

        String output = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Ошибка ввода: ожидалось целое число"));
        assertTrue(output.contains("До свидания"));
    }


    @Test
    void blankInventoryNumberIsRejectedBeforeAskingForMoreFields() {
        String input = String.join("\n", "1", "1", "", "0", "");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        application(input, bytes).run();

        String output = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Инвентарный номер не может быть пустым"));
        assertFalse(output.contains("Простота для новичка"));
        assertTrue(output.contains("До свидания"));
    }

    @Test
    void invalidBeginnerEaseIsRejectedBeforeAskingForCondition() {
        String input = String.join("\n", "1", "1", "VK-S-EASE", "11", "0", "");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        application(input, bytes).run();

        String output = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Простота для новичка должна быть от 1 до 10"));
        assertFalse(output.contains("Техническое состояние:"));
        assertTrue(output.contains("До свидания"));
    }

    @Test
    void emptyParkReportsAreFriendly() {
        String input = String.join("\n", "3", "4", "5", "6", "0", "");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        application(input, bytes).run();

        String output = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Всего инвентарных единиц: 0"));
        assertTrue(output.contains("Подходящего транспорта пока нет."));
        assertTrue(output.contains("Парк пока пуст."));
        assertTrue(output.contains("Прокатного транспорта пока нет."));
    }

    @Test
    void byteOrderMarkAtStartOfInputIsIgnored() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        application("\uFEFF3\n0\n", bytes).run();

        String output = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Всего инвентарных единиц: 0"));
        assertFalse(output.contains("Неизвестный пункт меню"));
    }

    @Test
    void unknownMenuItemShowsHint() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        application(String.join("\n", "9", "0", ""), bytes).run();

        assertTrue(bytes.toString(StandardCharsets.UTF_8).contains("Неизвестный пункт меню"));
    }

    @Test
    void malformedEnergyValueIsRejected() {
        String input = String.join("\n", "2", "2", "VK-CS-X", "1d", "3", "0", "");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        application(input, bytes).run();

        String output = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("ожидалось число, получено: 1d"));
        assertTrue(output.contains("Всего инвентарных единиц: 0"));
    }

    @Test
    void summaryShowsCountsByKindAndLowercaseNumberIsDuplicate() {
        String input = String.join("\n",
                "2", "1", "vk-h-1",
                "2", "1", "VK-H-1",
                "3", "0", "");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        application(input, bytes).run();

        String output = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("уже используется"));
        assertTrue(output.contains("Шлем: 1"));
    }

    private static ConsoleApplication application(String input, ByteArrayOutputStream bytes) {
        ParkRegistry registry = new ParkRegistry();
        ParkService parkService = new ParkService(new DefaultServiceCenter(), registry);
        ParkReportService reportService = new ParkReportService(registry);
        return new ConsoleApplication(parkService, reportService,
                new Scanner(input), new PrintStream(bytes, true, StandardCharsets.UTF_8));
    }
}
