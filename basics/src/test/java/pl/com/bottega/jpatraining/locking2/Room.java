package pl.com.bottega.jpatraining.locking2;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
class Room {
    @Id
    private Long id;

    Room() {}

    public Room(Long id) {
        this.id = id;
    }
}
