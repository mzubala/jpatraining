package pl.com.bottega.jpatraining.onetomany;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    Long id;

    @ManyToOne
    Post post;

    public Tag(Post post) {
        this.post = post;
    }

    Tag() {}

}
