package pl.com.bottega.jpatraining.onetomany;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Post {

    @Id
    @GeneratedValue
    Long id;

    Collection<Like> likes = new LinkedList<>();
    List<Comment> comments = new LinkedList<>();
    Set<Tag> tags = new HashSet<>();


}
