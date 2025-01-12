package service;

import interfaces.HistoryManager;
import model.Node;
import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private HashMap<Integer, Node> historyList;
    private Node firstNode;
    private Node lastNode;


    public InMemoryHistoryManager() {
        historyList = new HashMap<>();
    }

    protected void linkLast(Task task) {
        Node newNode = new Node(task, lastNode, null);

        if (lastNode != null) {
            lastNode.setNextNode(newNode);
        }
        lastNode = newNode;

        if (firstNode == null) {
            firstNode = newNode;
        }
    }

    private void removeNode(Node node) {

        if (node == null) {
            return;
        }
        Node prevNode = node.getPrevNode();
        Node nextNode = node.getNextNode();

        if (prevNode != null) {
            prevNode.setNextNode(nextNode);
        } else {
            firstNode = nextNode;
        }

        if (nextNode != null) {
            nextNode.setPrevNode(prevNode);
        } else {
            lastNode = prevNode;
        }
    }

    protected List<Task> getTasks() {
        List<Task> tasksList = new ArrayList<>();
        Node node = lastNode;
        while (node != null) {
            tasksList.add(node.getTask());
            node = node.getPrevNode();
        }
        return tasksList;
    }

    @Override
    public void add(Task task) {
        final int id = task.getTaskId();

        removeNode(historyList.remove(id));
        linkLast(task);
        historyList.put(id, lastNode);
    }

    @Override
    public void remove(int id) {
        Node node = historyList.remove(id);
        removeNode(node);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<Task>(getTasks());
    }

}
