package pl.com.bottega.jpatraining.cache;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User {

    @Id
    @GeneratedValue
    Long id;

    String name;

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
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
