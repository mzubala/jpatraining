package pl.com.bottega.jpatraining.idgen;

import jakarta.persistence.*;

import java.rmi.server.UID;
import java.util.UUID;

@Entity
@Table(name = "auction_with_uuid")
public class AuctionWithUUID {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
