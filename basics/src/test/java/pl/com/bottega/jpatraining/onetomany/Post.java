package pl.com.bottega.jpatraining.onetomany;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static jakarta.persistence.CascadeType.ALL;

@Entity
public class Post {

    @Id
    @GeneratedValue
    Long id;

    @OneToMany(cascade = ALL, mappedBy = "post", fetch = FetchType.EAGER)
    Collection<Like> likes = new LinkedList<>();
    @OneToMany(cascade = ALL, mappedBy = "post")
    @OrderColumn(name = "index")
    List<Comment> comments = new LinkedList<>();
    @OneToMany(cascade = ALL, mappedBy = "post")
    //@LazyCollection(LazyCollectionOption.EXTRA)
    Set<Tag> tags = new HashSet<>();

    @OneToMany(cascade = ALL, mappedBy = "post", fetch = FetchType.EAGER)
    List<Share> shares = new LinkedList<>();

}
