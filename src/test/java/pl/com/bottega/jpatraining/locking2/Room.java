package pl.com.bottega.jpatraining.locking2;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
class Room {
    @Id
    private Long id;

    @Version
    private Long version;

    Room() {
    }

    public Room(Long id) {
        this.id = id;
    }
}
