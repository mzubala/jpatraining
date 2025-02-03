package pl.com.bottega.jpatraining.onetomany;

import jakarta.persistence.*;

@Entity
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    Post post;

    public Like() {
    }

    public Like(Post post) {
        this.post = post;
    }
}
