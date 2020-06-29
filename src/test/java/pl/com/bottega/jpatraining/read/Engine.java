package pl.com.bottega.jpatraining.read;

import javax.persistence.Embeddable;

@Embeddable
class Engine {
    Integer capacity;
    Integer horsePower;
}
