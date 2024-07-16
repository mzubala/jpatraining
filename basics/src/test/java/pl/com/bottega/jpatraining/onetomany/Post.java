package pl.com.bottega.jpatraining.onetomany;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.util.*;

@Entity
public class Post {

    static final int BATCH_SIZE = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    @BatchSize(size = BATCH_SIZE)
    Collection<Like> likes = new LinkedList<>();

    @OneToMany
    @JoinColumn
    Collection<Share> shares = new LinkedList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    @OrderColumn(name = "index")
    List<Comment> comments = new LinkedList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post", orphanRemoval = true)
    Set<Tag> tags = new HashSet<>();


}
