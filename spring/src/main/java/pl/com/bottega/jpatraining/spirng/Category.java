package pl.com.bottega.jpatraining.spirng;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Product product;

}
