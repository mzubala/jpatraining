package pl.com.bottega.jpatraining.cache;

import org.hibernate.annotations.Cache;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

import static org.hibernate.annotations.CacheConcurrencyStrategy.READ_WRITE;

@Entity
@Cache(usage = READ_WRITE)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    @Cache(usage = READ_WRITE)
    Set<Photo> photos = new HashSet<>();

    public User() {
    }

    public User(String name) {
        this.name = name;
    }

    public void addPhoto(String url) {
        this.photos.add(new Photo(url));
    }
}
