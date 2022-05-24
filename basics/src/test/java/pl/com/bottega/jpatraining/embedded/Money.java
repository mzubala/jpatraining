package pl.com.bottega.jpatraining.embedded;

import java.math.BigDecimal;
import java.util.Objects;

public class Money {

    private final Integer cents;
    private final String currency;

    private Money(Integer cents, String currency) {
        this.cents = cents;
        this.currency = currency;
    }

    public static Money of(Integer val, String currency) {
        return new Money(val * 100, currency);
    }

    public static Money of(BigDecimal val, String currency) {
        return new Money(val.multiply(new BigDecimal(100)).intValue(), currency);
    }

    public Money add(Money other) {
        if(!other.currency.equals(currency)) {
            throw new IllegalArgumentException();
        }
        return new Money(cents + other.cents, currency);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(cents, money.cents) &&
            Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {

        return Objects.hash(cents, currency);
    }
}
