import java.util.HashMap;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        Scanner scanner = new Scanner(System.in);
        TaskManager taskManager = new TaskManager(scanner);


        while (true) {
            Menu.printMenu();
            System.out.println();
            System.out.print("Введите ваш выбор: ");
            int whatToDo = scanner.nextInt();
            int taskId;

            switch (whatToDo) {
                case 1: // Получение списка всех задач: 1
                    taskManager.printAllTasksList();
                    break;
                case 2: // Удаление всех задач: 2
                    System.out.print("Подтвердите удаление всех задач (y/n): ");
                    if (scanner.next().equals("y")) {
                        taskManager.clearAllTasks();
                        System.out.println("Все задачи удалены.");
                    }
                    break;
                case 3: // Получение задачи по идентификатору: 3
                    System.out.print("Введите ID задачи: ");
                    taskId = scanner.nextInt();
                    taskManager.printTask(taskId);
                    break;
                case 4: // Создать задачу: 4
                    taskManager.addTask();
                    break;
                case 5: // Обновить задачу: 5
                    System.out.print("Введите ID задачи: ");
                    taskId = scanner.nextInt();
                    taskManager.updateTask(taskId);
                    break;
                case 6: // Удалить задачу по идентификатору: 6
                    System.out.print("Введите ID задачи: ");
                    taskId = scanner.nextInt();
                    taskManager.deleteTask(taskId);
                    break;
                case 0: // Выход: 0
                    return;
                default:
                    System.out.println("Такой команды нет, попробуйте ещё раз.");
            }
            System.out.println("----------");
        }
    }
}
