package pl.com.bottega.jpatraining.onetomany;

import com.sun.xml.bind.v2.model.core.ID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    Post post;

    public Comment(Post post) {
        this.post = post;
    }

    public Comment() {
    }
}
