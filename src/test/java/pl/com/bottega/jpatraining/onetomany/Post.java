package pl.com.bottega.jpatraining.onetomany;

import org.hibernate.annotations.BatchSize;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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

    @OneToMany(cascade = {CascadeType.PERSIST}, mappedBy = "post", fetch = FetchType.EAGER) // PersistentBag
    Collection<Like> likes = new LinkedList<>();

    @OneToMany(cascade = {CascadeType.PERSIST}, mappedBy = "post") // PersistentList
    @OrderColumn(name = "comment_index")
    List<Comment> comments = new LinkedList<>();

    @OneToMany(cascade = {CascadeType.PERSIST}, mappedBy = "post", orphanRemoval = true)
    Set<Tag> tags = new HashSet<>();
}
