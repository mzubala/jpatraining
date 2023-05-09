package pl.com.bottega.jpatraining.onetomany;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Like {
    @Id
    @GeneratedValue
    Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    Post post;

    Like(Post post) {
        this.post = post;
    }

    Like() {}
}
