package interfaces;

import model.ManagerReadException;
import model.ManagerSaveException;

public interface FileTaskManager {

    public void save() throws ManagerSaveException;

    public void restore(String fileName) throws ManagerReadException;

    public boolean checkFile(String fileName) throws ManagerReadException;
}
