package pl.com.bottega.jpatraining.onetomany;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.util.*;

@Entity
@NamedEntityGraph(
        name = "only-comments",
        attributeNodes = {
                @NamedAttributeNode("comments"),
        }
)
@NamedEntityGraph(
        name = "all-dependencies",
        attributeNodes = {
                @NamedAttributeNode("comments"),
                @NamedAttributeNode("likes"),
                @NamedAttributeNode("tags"),
        }
)
public class Post {

    @Id
    @GeneratedValue
    Long id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    @BatchSize(size = 20)
    Collection<Like> likes = new LinkedList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    @OrderColumn(name = "comment_index")
    List<Comment> comments = new LinkedList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post", orphanRemoval = true)
    Set<Tag> tags = new HashSet<>();

    @OneToMany
    @JoinColumn
    Collection<Share> shares = new LinkedList<>();

}
