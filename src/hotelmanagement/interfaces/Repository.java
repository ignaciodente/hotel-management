package hotelmanagement.interfaces;
import hotelmanagement.exceptions.PersistenceException;

import java.util.List;

public interface Repository<T> {


    public void insert (T entity) throws PersistenceException;
    public void update(T entity) throws PersistenceException;
    public List<T> findAll() throws PersistenceException;
    public void exportToCsv(String folder) throws PersistenceException;

}
