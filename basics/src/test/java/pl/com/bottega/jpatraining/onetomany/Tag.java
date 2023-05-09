package pl.com.bottega.jpatraining.onetomany;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Tag {
    @Id
    @GeneratedValue
    Long id;

    @ManyToOne
    Post post;

    Tag(Post post) {
        this.post = post;
    }

    Tag() {

    }
}
