package pl.com.bottega.jpatraining.onetomany;

import jakarta.persistence.*;
import org.hibernate.annotations.Bag;

import java.util.*;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToMany(cascade = CascadeType.PERSIST)
    Collection<Like> likes = new LinkedList<>();
    @OneToMany(cascade = CascadeType.PERSIST)
    @OrderColumn
    List<Comment> comments = new LinkedList<>();
    @OneToMany(cascade = CascadeType.PERSIST)
    Set<Tag> tags = new HashSet<>();


}
