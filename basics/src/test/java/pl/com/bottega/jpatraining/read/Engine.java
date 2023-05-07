package pl.com.bottega.jpatraining.read;

import jakarta.persistence.Embeddable;

@Embeddable
class Engine {
    Integer capacity;
    Integer horsePower;
}
