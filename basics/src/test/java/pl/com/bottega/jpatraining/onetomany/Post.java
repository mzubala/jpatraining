package pl.com.bottega.jpatraining.onetomany;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.util.*;

@Entity
public class Post {
    public static final int COMMENTS_BATCH = 20;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    Collection<Like> likes = new LinkedList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    @OrderColumn
    @BatchSize(size = COMMENTS_BATCH)
    List<Comment> comments = new LinkedList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post", orphanRemoval = true)
    Set<Tag> tags = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn
    List<Share> shares = new LinkedList<>();
}
