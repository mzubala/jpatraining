package pl.com.bottega.jpatraining.onetomany;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToMany//(fetch = FetchType.EAGER)
    Set<Image> images = new HashSet<>();

    @OneToMany//(fetch = FetchType.EAGER)
    Set<Author> authors = new HashSet<>();

}

@Entity
class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
}

@Entity
class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
}


