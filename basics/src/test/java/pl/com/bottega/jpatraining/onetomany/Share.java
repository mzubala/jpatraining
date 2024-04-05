package pl.com.bottega.jpatraining.onetomany;

import jakarta.persistence.*;

@Entity
class Share {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    Post post;


}
