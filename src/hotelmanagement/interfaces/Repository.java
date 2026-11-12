package hotelmanagement.interfaces;
import java.util.List;

public interface Repository<T> {


    public void insert (T entity);
    public void update(T entity);
    public List<T> findAll();
    public void exportToCsv(String folder);

}
