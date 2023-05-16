package pl.com.bottega.jpatraining.onetomany;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import org.hibernate.annotations.BatchSize;

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
    List<Comment> comments = new LinkedList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    @BatchSize(size = 50)
    Set<Tag> tags = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    @BatchSize(size = 50)
    List<Share> shares;

}
