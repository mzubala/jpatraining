package pl.com.bottega.jpatraining.onetomany;

import org.hibernate.annotations.BatchSize;

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

@Entity
public class Post {

    @Id
    @GeneratedValue
    Long id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    Collection<Like> likes = new LinkedList<>(); // PersistentBag

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    @OrderColumn(name = "index")
    List<Comment> comments = new LinkedList<>(); // PersistentList

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post", orphanRemoval = true)
    Set<Tag> tags = new HashSet<>(); // PersistentSet

}
