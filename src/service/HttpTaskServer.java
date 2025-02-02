package service;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

import interfaces.HttpTaskManager;
import model.*;

public class HttpTaskServer implements HttpTaskManager {
    private static final int PORT = 8080;
    private static FileBackedTaskManager fileBackedTaskManager;

    HttpTaskServer(String fileName) {
        fileBackedTaskManager = new FileBackedTaskManager(fileName);
    }

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(fileBackedTaskManager));
        httpServer.createContext("/epics", new EpicHandler(fileBackedTaskManager));
        httpServer.createContext("/subtasks", new SubTaskHandler(fileBackedTaskManager));
        httpServer.createContext("/history", new HistoryHandler(fileBackedTaskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(fileBackedTaskManager));

        httpServer.start(); // запускаем сервер
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void start() throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(fileBackedTaskManager));
        httpServer.createContext("/epics", new EpicHandler(fileBackedTaskManager));
        httpServer.createContext("/subtasks", new SubTaskHandler(fileBackedTaskManager));
        httpServer.createContext("/history", new HistoryHandler(fileBackedTaskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(fileBackedTaskManager));

        httpServer.start(); // запускаем сервер
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public  FileBackedTaskManager getHttpBackedTaskManager() {
        return fileBackedTaskManager;
    }
}




