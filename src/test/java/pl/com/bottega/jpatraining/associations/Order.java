package pl.com.bottega.jpatraining.associations;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.util.LinkedList;
import java.util.List;

@NamedQueries(
    {
        @NamedQuery(
            name = "getAll",
            query = "FROM Order"
        )
    }
)
@Table( name = "orders_table")
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
