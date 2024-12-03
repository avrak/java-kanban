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
        firstNode = null;
        lastNode = null;
    }

    protected void linkLast(Task task) {
        Node newNode = new Node(task, lastNode, null);
        if (lastNode != null) {
            lastNode.setNextNode(newNode);
        }
        historyList.put(newNode.getId(), newNode);
        lastNode = newNode;

        if (firstNode == null) {
            firstNode = newNode;
        }
    }

    protected void removeNode(Integer taskId) {
        Node currentNode = historyList.remove(taskId);
        if (currentNode == null) {
            return;
        }
        Node prevNode = currentNode.getPrevNode();
        Node nextNode = currentNode.getNextNode();

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
//        ArrayList<Task> nodesList = new ArrayList<>();
//        Integer taskId = tailTaskId;
//
//        while (taskId != END_HISTORY) {
//            Node node = historyList.get(taskId);
//            nodesList.add(node.getTask());
//            taskId = node.getPrevId();
//        }
//        return nodesList;

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
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        removeNode(id);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<Task>(getTasks());
    }

}
