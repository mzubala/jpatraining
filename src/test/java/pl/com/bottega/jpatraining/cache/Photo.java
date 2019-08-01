package pl.com.bottega.jpatraining.cache;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String url;

    Photo() {
    }

    public Photo(String url) {
        this.url = url;
    }
}
