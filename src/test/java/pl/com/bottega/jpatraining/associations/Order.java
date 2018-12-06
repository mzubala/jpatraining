package pl.com.bottega.jpatraining.associations;

import java.util.LinkedList;
import java.util.List;

public class Order {

    private Long id;

    private List<LineItem> items = new LinkedList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<LineItem> getItems() {
        return items;
    }

    public void setItems(List<LineItem> items) {
        this.items = items;
    }
}
