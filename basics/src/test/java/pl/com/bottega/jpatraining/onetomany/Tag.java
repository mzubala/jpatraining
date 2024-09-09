package pl.com.bottega.jpatraining.onetomany;

import jakarta.persistence.*;

@Entity
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Post post;

    public Tag() {
    }

    public Tag(Post post) {
        this.post = post;
    }
}
