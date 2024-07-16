package pl.com.bottega.jpatraining.onetomany;

import jakarta.persistence.*;

@Entity
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    Post post;

    public Tag(Post post) {
        this.post = post;
    }

    public Tag() {}
}
