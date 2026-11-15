package hotelmanagement.generics;

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

    public T findById(int id) {
        return elements.get(id);
    }

    public boolean contains(int id) {
        return elements.containsKey(id);
    }

    public List<T> list() {
        return new ArrayList<>(elements.values());
    }
}
