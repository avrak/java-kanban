package service;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

import interfaces.HttpTaskManager;
import model.*;

public class HttpTaskServer implements HttpTaskManager {
    private static final int PORT = 8080;
    private static FileBackedTaskManager fileBackedTaskManager;
    public static HttpServer httpServer;
    private static final String startedString = "HTTP-сервер запущен на " + PORT + " порту!";
    private static HttpTaskServer httpTaskServer;

    HttpTaskServer(String fileName) throws IOException {
        fileBackedTaskManager = new FileBackedTaskManager(fileName);
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(fileBackedTaskManager));
        httpServer.createContext("/epics", new EpicHandler(fileBackedTaskManager));
        httpServer.createContext("/subtasks", new SubTaskHandler(fileBackedTaskManager));
        httpServer.createContext("/history", new HistoryHandler(fileBackedTaskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(fileBackedTaskManager));
    }

    public static void main(String[] args) throws IOException {
        httpTaskServer = new HttpTaskServer(InMemoryTaskManager.setFileName());
        httpServer.start(); // запускаем сервер
        System.out.println(startedString);
    }

    public void start() throws IOException {
        httpServer.start(); // запускаем сервер
        System.out.println(startedString);
    }

    public void stop() {
        httpServer.stop(0);
    }

    public  FileBackedTaskManager getHttpBackedTaskManager() {
        return fileBackedTaskManager;
    }
}




