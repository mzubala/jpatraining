package pl.com.bottega.jpatraining.onetomany;

import jakarta.persistence.*;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    Post post;

    public Comment(Post post) {
        this.post = post;
    }

    public Comment() {

    }
}
