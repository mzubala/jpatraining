package pl.com.bottega.jpatraining.associations;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import java.io.Serializable;
import java.util.Objects;

public class LineItem {

    private LineItemPK lineItemPK = new LineItemPK();
    private Product product;
    private Order order;
    private Integer count;

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
