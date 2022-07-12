package pl.com.bottega.jpatraining.associations;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class LineItem {

    @EmbeddedId
    private LineItemPK lineItemPK = new LineItemPK();

    @ManyToOne
    @MapsId("productId")
    private Product product;

    @ManyToOne
    @MapsId("orderId")
    private Order order;

    private Integer count;

    public LineItem() {
    }

    public LineItem(Order order, Product product, int count) {
        this.product = product;
        this.count = count;
        this.order = order;
        this.lineItemPK.orderId = order.getId();
        this.lineItemPK.productId = product.getId();
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Embeddable
    static class LineItemPK implements Serializable {
        private Long orderId;
        private Long productId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LineItemPK that = (LineItemPK) o;
            return Objects.equals(orderId, that.orderId) &&
                Objects.equals(productId, that.productId);
        }

        @Override
        public int hashCode() {

            return Objects.hash(orderId, productId);
        }
    }
}
