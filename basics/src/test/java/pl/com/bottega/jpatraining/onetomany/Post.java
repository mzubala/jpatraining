package pl.com.bottega.jpatraining.onetomany;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.util.*;

@Entity
@NamedEntityGraphs({
    @NamedEntityGraph(
        name = "post-with-likes-and-comments",
        attributeNodes = {
            @NamedAttributeNode("likes"),
            @NamedAttributeNode("comments")
        }
    ),
    @NamedEntityGraph(
        name = "post-with-everything",
        attributeNodes = {
            @NamedAttributeNode("likes"),
            @NamedAttributeNode("comments"),
            @NamedAttributeNode("tags"),
        }
    )
})
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    @BatchSize(size = 20)
    Collection<Like> likes = new LinkedList<>(); // PersistentBag
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    @OrderColumn(name = "comment_index")
    List<Comment> comments = new LinkedList<>(); // PersistentList
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post", orphanRemoval = true)
    Set<Tag> tags = new HashSet<>(); // PersistentSet

    @OneToMany
    @JoinColumn
    List<Share> shares = new LinkedList<>(); // PersistentBag

}
