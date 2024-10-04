package pl.com.bottega.jpatraining.onetomany;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Comment {

    @Id
    @GeneratedValue
    Long id;

    @ManyToOne
    Post post;

    public Comment(Post post) {
        this.post = post;
    }

    public Comment() {
    }
}
