package main.java.ru.yandex.practicum.canban;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import main.java.ru.yandex.practicum.canban.service;
import main.java.ru.yandex.practicum.canban.model.*;
import main.java.ru.yandex.practicum.canban.service.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        Scanner scanner = new Scanner(System.in);
        InMemoryTaskManager taskManager = new InMemoryTaskManager();


        while (true) {
            Menu.printMenu();
            System.out.println();
            String name;
            String description;
            Integer taskId;
            TaskType taskType;
            System.out.print("Введите ваш выбор: ");
            int whatToDo = scanner.nextInt();
            System.out.println("..........");

            switch (whatToDo) {
                case 1: // Получение списка всех задач: 1
                    List<Task> tasks = taskManager.getTasks();
                    if (!tasks.isEmpty()) {
                        System.out.println(tasks);
                    }
                    List<Epic> epics = taskManager.getEpics();
                    if (!epics.isEmpty()) {
                        System.out.println(epics);
                    }
                    List<SubTask> subTasks = taskManager.getSubTasks();
                    if (!subTasks.isEmpty()) {
                        System.out.println(subTasks);
                    }
                    break;
                case 2: // Удаление всех задач: 2
                    taskType = getTaskType(scanner);
                    if (taskType == null) {
                        System.out.println("Неизвестный тип задачи, попробуйте ещё раз");
                        break;
                    }
                    switch (taskType) {
                        case TASK:
                            taskManager.deleteTasks();
                            break;
                        case SUBTASK:
                            taskManager.deleteSubTasks();
                            break;
                        case EPIC:
                            taskManager.deleteEpics();
                    }
                    break;
                case 3: // Получение задачи по идентификатору: 3
                    System.out.print("Введите ID задачи: ");
                    taskId = scanner.nextInt();
                    //taskManager.printTask(taskId);
                    Task  task = taskManager.getTask(taskId);
                    Epic  epic = taskManager.getEpic(taskId);
                    SubTask subTask = taskManager.getSubTask(taskId);
                    if (task != null) {
                        System.out.println(task);
                    } else if (epic != null ){
                        System.out.println(epic);
                    } else if (subTask != null) {
                        System.out.println(subTask);
                    } else {
                        System.out.println("Такой задачи нет");
                    }
                    break;
                case 4: // Создать задачу: 4
                    //taskManager.addTask();
                    int epicId = 0;
                    name = setTaskName(scanner);
                    description = setTaskDescription(scanner);

                    taskType = getTaskType(scanner);
                    if (taskType == null) {
                        System.out.println("Неизвестный тип задачи, попробуйте ещё раз");
                        break;
                    }

                    if (taskType == TaskType.SUBTASK) {
                        epicId = setEpicId(scanner, taskManager);
                        if (epicId == -1) {
                            System.out.println("Эпик не найден, попробуйте ещё раз");
                            break;
                        }
                    }

                    switch (taskType) {
                        case TASK:
                            Task newTask = new Task(TaskType.TASK, name, description);
                            taskManager.addNewTask(newTask);
                            break;
                        case EPIC:
                            Epic newEpic = new Epic(TaskType.EPIC, name, description);
                            taskManager.addNewEpic(newEpic);
                            break;
                        case SUBTASK:
                            SubTask newSubTask = new SubTask(TaskType.SUBTASK, epicId, name, description);
                            taskManager.addNewSubtask(newSubTask);
                            break;
                        case HISTORY:
                            System.out.println(taskManager.getHistory());
                    }


                    break;
                case 5: // Обновить задачу: 5
                    taskId = setTaskId(scanner);
                    Task updatingTask = taskManager.getTask(taskId);
                    Epic updatingEpic = taskManager.getEpic(taskId);
                    SubTask updatingSubTask = taskManager.getSubTask(taskId);

                    if (updatingTask != null) {
                        updatingTask.setName(setTaskName(scanner));
                        updatingTask.setDescription(setTaskDescription(scanner));
                        updatingTask.setStatus(setTaskStatus(scanner));
                        taskManager.updateTask(updatingTask);
                    } else if (updatingEpic != null) {
                        updatingEpic.setName(setTaskName(scanner));
                        updatingEpic.setDescription(setTaskDescription(scanner));
                        taskManager.updateEpic(updatingEpic);
                    } else if(updatingSubTask != null) {
                        updatingSubTask.setName(setTaskName(scanner));
                        updatingSubTask.setDescription(setTaskDescription(scanner));
                        updatingSubTask.setStatus(setTaskStatus(scanner));
                        taskManager.updateSubtask(updatingSubTask);
                    } else {
                        System.out.println("Задача не найдена");
                    }
                    break;
                case 6: // Удалить задачу по идентификатору: 6
                    taskId = setTaskId(scanner);
                    if (taskManager.getTask(taskId) != null) {
                        taskManager.deleteTask(taskId);
                    } else if (taskManager.getEpic(taskId) != null ){
                        taskManager.deleteEpic(taskId);
                    } else if (taskManager.getSubTask(taskId) != null) {
                        taskManager.deleteSubTask(taskId);
                    } else {
                        System.out.println("Такой задачи нет");
                    }
                    break;
                case 0: // Выход: 0
                    return;
                default:
                    System.out.println("Такой команды нет, попробуйте ещё раз.");
            }
            System.out.println("----------");
        }
    }

    public static String setTaskName(Scanner scanner) {
        System.out.print("Введите название задачи: ");
        return scanner.next();
    }

    public static String setTaskDescription(Scanner scanner) {
        System.out.print("Введите описание задачи: ");
        return scanner.next();
    }

    public static TaskType getTaskType(Scanner scanner) {
        System.out.print("Введите тип задачи: ");
        String taskTypeString = scanner.next();
        for (TaskType taskType : TaskType.values()) {
            if (taskType.toString().equals(taskTypeString)) {
                return taskType;
            }
        }
        return null;
    }

    public static Integer setEpicId(Scanner scanner, InMemoryTaskManager taskManager) {
        System.out.print("Введите ID эпика: ");
        int epicId = scanner.nextInt();
        if (taskManager.getEpic(epicId) != null) {
            return epicId;
        } else {
            return -1;
        }

    }

    public static Integer setTaskId(Scanner scanner) {
        System.out.print("Введите ID задачи: ");
        return scanner.nextInt();
    }

    public static TaskStatus setTaskStatus(Scanner scanner) {
        System.out.print("Введите статус задачи: ");
        String statusString = scanner.next();
        for (TaskStatus status : TaskStatus.values()) {
            if (status.toString().equals(statusString)) {
                return status;
            }
        }
        System.out.println("Некорректный статус, попробуйте ещё");
        return null;
    }
}
