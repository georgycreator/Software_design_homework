package ru.vyshkat.app;

import ru.vyshkat.domain.Bicycle;
import ru.vyshkat.domain.ChargingCabinet;
import ru.vyshkat.domain.ChargingStation;
import ru.vyshkat.domain.ElectricBicycle;
import ru.vyshkat.domain.Helmet;
import ru.vyshkat.domain.InventoryItem;
import ru.vyshkat.domain.ParkEquipment;
import ru.vyshkat.domain.RentalVehicle;
import ru.vyshkat.domain.Scooter;
import ru.vyshkat.domain.TechnicalCondition;
import ru.vyshkat.service.OperationResult;
import ru.vyshkat.service.ParkReportService;
import ru.vyshkat.service.ParkService;

import java.io.PrintStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

/** Тонкий консольный адаптер: ввод/вывод и преобразование пользовательского ввода в доменные команды. */
public final class ConsoleApplication {
    private final ParkService parkService;
    private final ParkReportService reports;
    private final Scanner scanner;
    private final PrintStream output;
    private final boolean echoInput;

    public ConsoleApplication(ParkService parkService, ParkReportService reports,
                              Scanner scanner, PrintStream output) {
        this(parkService, reports, scanner, output, false);
    }

    /**
     * @param echoInput печатать прочитанные строки; полезно для наглядного прогона из файла.
     */
    public ConsoleApplication(ParkService parkService, ParkReportService reports,
                              Scanner scanner, PrintStream output, boolean echoInput) {
        this.parkService = Objects.requireNonNull(parkService, "ParkService обязателен");
        this.reports = Objects.requireNonNull(reports, "ParkReportService обязателен");
        this.scanner = Objects.requireNonNull(scanner, "Scanner обязателен");
        this.output = Objects.requireNonNull(output, "Поток вывода обязателен");
        this.echoInput = echoInput;
    }

    public void run() {
        output.println("=======================================");
        output.println(" ВышКат — учёт парка");
        output.println("=======================================");
        boolean running = true;
        while (running) {
            try {
                printMenu();
                String choice = readLine("Выберите действие: ").trim();
                switch (choice) {
                    case "1" -> addVehicle();
                    case "2" -> addEquipment();
                    case "3" -> printSummary();
                    case "4" -> printBeginnerFriendlyVehicles();
                    case "5" -> printInventory();
                    case "6" -> printVehicles();
                    case "0" -> {
                        output.println("До свидания! Доедем до дедлайна.");
                        running = false;
                    }
                    default -> output.println("Неизвестный пункт меню. Выберите число от 0 до 6.");
                }
            } catch (EndOfInputException exception) {
                output.println("\nВвод завершён. Приложение закрыто.");
                break;
            } catch (IllegalArgumentException exception) {
                output.println("Ошибка ввода: " + exception.getMessage());
            }
            output.println();
        }
    }

    private void printMenu() {
        output.println("1. Принять транспорт (с техосмотром)");
        output.println("2. Добавить вещь на баланс");
        output.println("3. Отчёт по парку и энергопотреблению");
        output.println("4. Транспорт для новичков (простота ≥ " + RentalVehicle.MIN_BEGINNER_EASE + ")");
        output.println("5. Весь инвентарь с номерами");
        output.println("6. Только прокатный транспорт");
        output.println("0. Выход");
    }

    private void addVehicle() {
        output.println("Тип: 1 — электросамокат; 2 — велосипед; 3 — электровелосипед");
        int type = readInt("Тип транспорта: ");
        if (type < 1 || type > 3) {
            throw new IllegalArgumentException("Неизвестный тип транспорта: " + type);
        }
        String inventoryNumber = readLine("Инвентарный номер: ").trim();
        validateInventoryNumber(inventoryNumber);
        int ease = readInt("Простота для новичка (1–10): ");
        if (ease < 1 || ease > 10) {
            throw new IllegalArgumentException("Простота для новичка должна быть от 1 до 10");
        }
        TechnicalCondition condition = readCondition();

        RentalVehicle vehicle = switch (type) {
            case 1 -> new Scooter(inventoryNumber, ease, condition,
                    readDouble("Суточный расход энергии, кВт·ч: "));
            case 2 -> new Bicycle(inventoryNumber, ease, condition);
            case 3 -> new ElectricBicycle(inventoryNumber, ease, condition,
                    readDouble("Суточный расход энергии, кВт·ч: "));
            default -> throw new IllegalArgumentException("Неизвестный тип транспорта: " + type);
        };
        showResult(parkService.admitVehicle(vehicle));
    }

    private void validateInventoryNumber(String inventoryNumber) {
        if (inventoryNumber.isBlank()) {
            throw new IllegalArgumentException("Инвентарный номер не может быть пустым");
        }
    }

    private TechnicalCondition readCondition() {
        output.println("Техническое состояние: 1 — исправен; 2 — требуется ремонт");
        return switch (readInt("Состояние: ")) {
            case 1 -> TechnicalCondition.READY_FOR_SERVICE;
            case 2 -> TechnicalCondition.NEEDS_REPAIR;
            default -> throw new IllegalArgumentException("Выберите 1 или 2 для технического состояния");
        };
    }

    private void addEquipment() {
        output.println("Тип вещи: 1 — шлем; 2 — зарядная станция; 3 — зарядный шкаф");
        int type = readInt("Тип вещи: ");
        if (type < 1 || type > 3) {
            throw new IllegalArgumentException("Неизвестный тип вещи: " + type);
        }
        String inventoryNumber = readLine("Инвентарный номер: ").trim();
        validateInventoryNumber(inventoryNumber);
        ParkEquipment equipment = switch (type) {
            case 1 -> new Helmet(inventoryNumber);
            case 2 -> new ChargingStation(inventoryNumber,
                    readDouble("Суточное энергопотребление, кВт·ч: "));
            case 3 -> new ChargingCabinet(inventoryNumber,
                    readDouble("Суточное энергопотребление, кВт·ч: "));
            default -> throw new IllegalArgumentException("Неизвестный тип вещи: " + type);
        };
        showResult(parkService.addEquipment(equipment));
    }

    private void printSummary() {
        output.println("--- Отчёт по парку ---");
        output.println("Всего инвентарных единиц: " + reports.totalInventoryCount());
        output.println("Прокатного транспорта: " + reports.allVehicles().size());
        output.println("Вещей на балансе: " + reports.equipmentCount());
        output.println("Суточное энергопотребление: "
                + String.format(Locale.ROOT, "%.2f", reports.totalDailyEnergyKwh()) + " кВт·ч");
        Map<String, Long> byName = reports.countByName();
        if (!byName.isEmpty()) {
            output.println("По видам:");
            byName.forEach((name, count) -> output.println("  " + name + ": " + count));
        }
    }

    private void printBeginnerFriendlyVehicles() {
        output.println("--- Транспорт для новичков (простота ≥ " + RentalVehicle.MIN_BEGINNER_EASE + ") ---");
        List<RentalVehicle> vehicles = reports.beginnerFriendlyVehicles();
        if (vehicles.isEmpty()) {
            output.println("Подходящего транспорта пока нет.");
            return;
        }
        vehicles.forEach(vehicle -> output.printf("%s [%s], простота: %d/10%n",
                vehicle.name(), vehicle.inventoryNumber(), vehicle.beginnerEase()));
    }

    private void printInventory() {
        output.println("--- Весь инвентарь ---");
        List<InventoryItem> items = reports.allInventoryItems();
        if (items.isEmpty()) {
            output.println("Парк пока пуст.");
            return;
        }
        items.forEach(item -> output.printf("%s — %s%n", item.name(), item.inventoryNumber()));
    }

    private void printVehicles() {
        output.println("--- Прокатный транспорт ---");
        List<RentalVehicle> vehicles = reports.allVehicles();
        if (vehicles.isEmpty()) {
            output.println("Прокатного транспорта пока нет.");
            return;
        }
        vehicles.forEach(vehicle -> output.printf("%s — %s; простота %d/10%n",
                vehicle.name(), vehicle.inventoryNumber(), vehicle.beginnerEase()));
    }

    private void showResult(OperationResult result) {
        output.println((result.successful() ? "[УСПЕХ] " : "[ОТКЛОНЕНО] ") + result.message());
    }

    private String readLine(String prompt) {
        output.print(prompt);
        if (!scanner.hasNextLine()) {
            throw new EndOfInputException();
        }
        // PowerShell при конвейере и некоторые редакторы добавляют BOM к первой строке
        String value = scanner.nextLine().replace("\uFEFF", "");
        if (echoInput) {
            output.println(value);
        }
        return value;
    }

    private int readInt(String prompt) {
        String value = readLine(prompt).trim();
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("ожидалось целое число, получено: " + value);
        }
    }

    private double readDouble(String prompt) {
        String value = readLine(prompt).trim().replace(',', '.');
        if (!value.matches("[+-]?\\d+(\\.\\d+)?")) {
            throw new IllegalArgumentException("ожидалось число, получено: " + value);
        }
        try {
            double result = Double.parseDouble(value);
            if (!Double.isFinite(result) || result < 0) {
                throw new IllegalArgumentException("значение должно быть конечным и неотрицательным");
            }
            return result;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("ожидалось число, получено: " + value);
        }
    }

    /** Сигнал о завершении stdin, в том числе в середине заполнения формы. */
    private static final class EndOfInputException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}
