package pl.com.bottega.jpatraining.onetomany;

import jakarta.persistence.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
    @OneToMany(cascade = CascadeType.ALL,
        mappedBy = "post",
        orphanRemoval = true,
        fetch = FetchType.EAGER
    )
    Set<Tag> tags = new HashSet<>();


}
