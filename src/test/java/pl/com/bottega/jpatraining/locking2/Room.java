package pl.com.bottega.jpatraining.locking2;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
class Room {
    @Id
    private Long id;

    Room() {}

    public Room(Long id) {
        this.id = id;
    }
}
