package pl.com.bottega.jpatraining.onetomany;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    Collection<Like> likes = new LinkedList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    @OrderColumn(name = "index")
    @LazyCollection(LazyCollectionOption.EXTRA)
    List<Comment> comments = new LinkedList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post", orphanRemoval = true, fetch = FetchType.EAGER)
    @BatchSize(size = 100)
    Set<Tag> tags = new HashSet<>();


}
