package pl.com.bottega.jpatraining.onetomany;

import jakarta.persistence.*;

import java.util.*;

@Entity
public class Post {

    @Id
    @GeneratedValue
    Long id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    Collection<Like> likes = new LinkedList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    @OrderColumn(name = "comment_index")
    List<Comment> comments = new LinkedList<>();
    @OneToMany(cascade = CascadeType.ALL)
    Set<Tag> tags = new HashSet<>();


}
