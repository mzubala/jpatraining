package pl.com.bottega.jpatraining.complexpk;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
public class Payment {

    @EmbeddedId
    private PaymentId id;

    private BigDecimal value;

    public PaymentId getId() {
        return id;
    }

    public void setId(PaymentId id) {
        this.id = id;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}

@Embeddable
class PaymentId implements Serializable {
    private Long orderId;
    private String transactionId;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentId paymentId = (PaymentId) o;
        return orderId.equals(paymentId.orderId) &&
            transactionId.equals(paymentId.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, transactionId);
    }
}
