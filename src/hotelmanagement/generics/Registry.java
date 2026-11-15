package hotelmanagement.generics;
import hotelmanagement.exceptions.EntityNotFoundException;
import hotelmanagement.interfaces.Identifiable;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Registry<T extends Identifiable> {

    private HashMap<Integer, T> elements;

    public Registry() {
        this.elements = new HashMap<>();
    }

    public boolean add(T element) {
        if (element != null) {
            elements.put(element.getId(), element);
            return true;
        } return false;
    }

    public T findById(int id) throws EntityNotFoundException {

        T element = elements.get(id);

        if (element == null) {
            throw new EntityNotFoundException("No se encontró ninguna entidad con ID: " + id);
        }

        return element;
    }

    public boolean contains(int id) {
        return elements.containsKey(id);
    }

    public List<T> list() {
        return new ArrayList<>(elements.values());
    }
}
