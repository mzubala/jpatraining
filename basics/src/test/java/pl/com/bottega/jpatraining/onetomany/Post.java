package pl.com.bottega.jpatraining.onetomany;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.util.*;

@Entity
public class Post {

    @Id
    @GeneratedValue
    Long id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    @BatchSize(size = 50)
    Collection<Like> likes = new LinkedList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    @OrderColumn
    @BatchSize(size = 50)
    List<Comment> comments = new LinkedList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    @BatchSize(size = 50)
    Set<Tag> tags = new HashSet<>();

}
