package pl.com.bottega.jpatraining.locking2;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

@Entity
class Room {
    @Id
    private Long id;

    @Version
    private Long version;

    Room() {}

    public Room(Long id) {
        this.id = id;
    }
}
