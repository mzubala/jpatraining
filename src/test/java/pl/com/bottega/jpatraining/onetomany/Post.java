package pl.com.bottega.jpatraining.onetomany;

import org.hibernate.annotations.BatchSize;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
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

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "post")
    Collection<Like> likes = new LinkedList<>();

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "post")
    @OrderColumn
    List<Comment> comments = new LinkedList<>();

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "post", orphanRemoval = true)
    Set<Tag> tags = new HashSet<>();


}
