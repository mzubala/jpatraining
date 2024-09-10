package pl.com.bottega.jpatraining.onetomany;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.util.*;

@Entity
@NamedEntityGraph(
    name = Post.POST_WITH_COMMENTS,
    attributeNodes = {
        @NamedAttributeNode("comments")
    }
)
public class Post {

    static final int LIKES_BATCH_SIZE = 20;
    static final String POST_WITH_COMMENTS = "post_with_comments";

    @Id
    @GeneratedValue
    Long id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    @BatchSize(size = LIKES_BATCH_SIZE)
    Collection<Like> likes = new LinkedList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn
    Collection<Share> shares = new LinkedList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    @OrderColumn(name = "comment_index")
    List<Comment> comments = new LinkedList<>();
    @OneToMany(cascade = CascadeType.ALL,
        mappedBy = "post",
        orphanRemoval = true
    )
    Set<Tag> tags = new HashSet<>();


}
