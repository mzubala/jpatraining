package pl.com.bottega.jpatraining.onetomany;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    Long id;

    @OneToMany(
        cascade = CascadeType.ALL,
        mappedBy = "post"
    )
    Collection<Like> likes = new LinkedList<>();

    @OneToMany(
        cascade = CascadeType.ALL,
        mappedBy = "post"
    )
    @OrderColumn
    List<Comment> comments = new LinkedList<>();

    @OneToMany(
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        mappedBy = "post"
    )
    Set<Tag> tags = new HashSet<>();

}
