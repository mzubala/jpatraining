package pl.com.bottega.jpatraining.idgen;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "auction_with_uuid")
class AuctionWithUUID {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    public UUID getId() {
        return id;
    }

}
