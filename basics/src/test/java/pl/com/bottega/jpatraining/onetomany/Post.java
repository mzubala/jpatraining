package pl.com.bottega.jpatraining.onetomany;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.util.*;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "post")
    @BatchSize(size = 20)
    Collection<Like> likes = new LinkedList<>();
    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "post_id")
    @BatchSize(size = 20)
    Collection<Share> shares = new LinkedList<>();
    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "post")
    @OrderColumn
    List<Comment> comments = new LinkedList<>();
    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "post")
    Set<Tag> tags = new HashSet<>();


}
