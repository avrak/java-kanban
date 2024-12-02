package service;

import interfaces.HistoryManager;
import model.Node;
import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int END_HISTORY = -1;

    private final HistoryListLog historyListLog;

    public InMemoryHistoryManager() {
        historyListLog = new HistoryListLog();
    }

    static class HistoryListLog {
        HashMap<Integer, Node> historyList;

        Integer tailTaskId;

        HistoryListLog() {
            historyList = new HashMap<>();
            tailTaskId = END_HISTORY;
        }

        protected void linkLast(Task task) {
            Node node = new Node(task, tailTaskId, null);
            Node prevNode = historyList.getOrDefault(tailTaskId, null);
            if (prevNode != null) {
                prevNode.setNextId(task.getTaskId());
            }
            historyList.put(node.getId(), node);
            tailTaskId = task.getTaskId();
        }

        protected void removeNode(Integer taskId) {
            Node currentNode = historyList.remove(taskId);
            if (currentNode == null) {
                return;
            }
            Node prevNode = historyList.getOrDefault(currentNode.getPrevId(), null);
            Node nextNode = historyList.getOrDefault(currentNode.getNextId(), null);

            if (prevNode != null) {
                if (nextNode != null) {
                    prevNode.setNextId(nextNode.getId());
                } else {
                    prevNode.setNextId(END_HISTORY);
                }
            }

            if (nextNode != null) {
                if (prevNode != null) {
                    nextNode.setPrevId(prevNode.getId());
                } else {
                    nextNode.setPrevId(END_HISTORY);
                }
            } else {
                if (prevNode != null) {
                    tailTaskId = prevNode.getId();
                } else {
                    tailTaskId = END_HISTORY;
                }
            }

//            if (historyList.size() == 0) {
//                tailTaskId = END_HISTORY;
//            }
        }

        protected List<Task> getTasks() {
            ArrayList<Task> nodesList = new ArrayList<>();
            Integer taskId = tailTaskId;

            while (taskId != END_HISTORY) {
                Node node = historyList.get(taskId);
                nodesList.add(node.getTask());
                taskId = node.getPrevId();
            }
            return nodesList;
        }
    }

    @Override
    public void add(Task task) {
        historyListLog.linkLast(task);
    }

    @Override
    public void remove(int id) {
        historyListLog.removeNode(id);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<Task>(historyListLog.getTasks());
    }

}
