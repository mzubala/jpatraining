package pl.com.bottega.jpatraining.onetomany;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, mappedBy = "post", orphanRemoval = true)
    Collection<Like> likes = new LinkedList<>(); // OneToMany + Collection -> PersistentBag

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, mappedBy = "post", orphanRemoval = true)
    @OrderColumn
    List<Comment> comments = new LinkedList<>(); // OneToMany + List -> PersistentBag,  OneToMany + List + OrderColumn -> PersistentList

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, mappedBy = "post", orphanRemoval = true) // OneToMany + Set -> PersistentSet
    Set<Tag> tags = new HashSet<>(); // Set
}
