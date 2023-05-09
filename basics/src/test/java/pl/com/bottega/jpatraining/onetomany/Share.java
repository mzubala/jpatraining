package pl.com.bottega.jpatraining.onetomany;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Share {

    @Id
    private Long id;

    @ManyToOne
    Post post;

}
