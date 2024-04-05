package pl.com.bottega.jpatraining.onetomany;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.util.*;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToMany(cascade = {CascadeType.PERSIST}, mappedBy = "post")
    Collection<Like> likes = new LinkedList<>();
    @OneToMany(cascade = {CascadeType.PERSIST}, mappedBy = "post")
    @OrderColumn(name = "index")
    @BatchSize(size = 20)
    List<Comment> comments = new LinkedList<>();
    @OneToMany(cascade = {CascadeType.PERSIST}, mappedBy = "post", orphanRemoval = true)
    @BatchSize(size = 20)
    Set<Tag> tags = new HashSet<>();

    @OneToMany(cascade = {CascadeType.PERSIST}, mappedBy = "post")
    Collection<Share> shares = new LinkedList<>();

}
