package service;

import interfaces.FileTaskManager;
import model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import static model.TaskType.*;

public class FileBackedTaskManager extends InMemoryTaskManager implements FileTaskManager {

    public static final String BACKED_FILE_NAME = "backed_file";
    public static final int FIRST_TASK_POSITION = 1;
    public static final int TASK_TYPE = 0;
    public static final int TASK_ID = 1;
    public static final int TASK_NAME = 2;
    public static final int TASK_DESCRIPTION = 3;
    public static final int TASK_STATUS = 4;
    public static final int TASK_DURATION = 5;
    public static final int TASK_START_TIME = 6;
    public static final int SUBTASK_EPIC_ID = 7;

    private File tmpFile;

    public FileBackedTaskManager(String fileName) {
        tmpFile = new File(fileName);
        String[] lines = null;

        if (tmpFile.isFile()) {
            lines = getLines(fileName);
        }

        if (lines == null || lines.length <= FIRST_TASK_POSITION) {
            try {
                tmpFile = File.createTempFile(BACKED_FILE_NAME, ".csv");
            } catch (IOException e) {
                System.out.println("Не удалось создать файл: " + e.getMessage());
            }
        } else {
            FileBackedTaskManager.restore(this, fileName);
        }
    }

    public File getTmpFile() {
        return tmpFile;
    }

    @Override
    public void save() {
        try (Writer fileWriter = new FileWriter(tmpFile)) {
            fileWriter.write("type, taskId, name, description, status, epicId, duration, startTime\n");
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
        try (Writer fileWriter = new FileWriter(tmpFile, true)) {
            List<Task> tasks = new ArrayList<>(super.getTasks().values());
            if (!tasks.isEmpty()) {
                for (Task task : tasks) {
                    fileWriter.write(task.toFileString() + "\n");
                }
            }
            List<Epic> epics = new ArrayList<>(super.getEpics().values());
            if (!epics.isEmpty()) {
                for (Epic epic : epics) {
                    fileWriter.write(epic.toFileString() + "\n");
                }
            }
            List<SubTask> subTasks = new ArrayList<>(super.getSubTasks().values());
            if (!subTasks.isEmpty()) {
                for (SubTask subTask : subTasks) {
                    fileWriter.write(subTask.toFileString() + "\n");
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    @Override
    public boolean checkFile(String fileName) {
        File file = new File(fileName);
        return file.isFile();
    }

    private static String[] getLines(String  fileName) {
        File file = new File(fileName);
        String[] tasks;

        try {
            tasks = Files.readString(file.toPath()).split("\n");
        } catch (IOException e) {
            throw new ManagerReadException(e.getMessage());
        }

        return tasks;
    }

    public static void restore(FileBackedTaskManager fileBackedTaskManager, String fileName) {
        String[] tasks = getLines(fileName);

        for (int i = FIRST_TASK_POSITION; i < tasks.length; i++)  {
            String[] task = tasks[i].split(",");

            switch (TaskType.valueOf(task[TASK_TYPE])) {
                case TASK:
                    Task newTask = new Task(TASK,
                            task[TASK_NAME],
                            task[TASK_DESCRIPTION],
                            Integer.parseInt(task[TASK_ID]),
                            task[TASK_STATUS],
                            Duration.ofMinutes(Long.parseLong(task[TASK_DURATION])),
                            LocalDateTime.parse(task[TASK_START_TIME], Task.DATE_TIME_FORMATTER));
                    fileBackedTaskManager.addNewTask(newTask);

                    break;
                case EPIC:
                    Epic newEpic = new Epic(EPIC,
                            task[TASK_NAME],
                            task[TASK_DESCRIPTION],
                            Integer.parseInt(task[TASK_ID]),
                            task[TASK_STATUS]);
                    fileBackedTaskManager.addNewEpic(newEpic);
                    break;
                case SUBTASK:
                    SubTask newSubTask = new SubTask(SUBTASK,
                            Integer.parseInt(task[SUBTASK_EPIC_ID]),
                            task[TASK_NAME],
                            task[TASK_DESCRIPTION],
                            Integer.parseInt(task[TASK_ID]),
                            task[TASK_STATUS],
                            Duration.ofMinutes(Long.parseLong(task[TASK_DURATION])),
                            LocalDateTime.parse(task[TASK_START_TIME], Task.DATE_TIME_FORMATTER));
                        fileBackedTaskManager.addNewSubtask(newSubTask);
                    break;
                default:
                    throw new ManagerReadException("Некорректный тип задачи!");
            }
        }
    }

    @Override
    public void addNewTask(Task task) {
        super.addNewTask(task);
        save();
    }

    @Override
    public void addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        save();
    }

    @Override
    public void addNewSubtask(SubTask subTask) {
        super.addNewSubtask(subTask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(SubTask subTask) {
        super.updateSubtask(subTask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubTask(Integer id) {
        super.deleteSubTask(id);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteSubTasks() {
        super.deleteSubTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }
}
