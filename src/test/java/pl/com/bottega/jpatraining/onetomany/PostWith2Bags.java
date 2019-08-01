package pl.com.bottega.jpatraining.onetomany;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.Collection;
import java.util.LinkedHashSet;

@Entity
public class PostWith2Bags {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany
    @JoinColumn(name = "post_with_2_bags_id")
    private Collection<Tag> tags = new LinkedHashSet<>();

    @OneToMany
    @JoinColumn(name = "post_with_2_bags_id")
    private Collection<Like> likes = new LinkedHashSet<>();

}
