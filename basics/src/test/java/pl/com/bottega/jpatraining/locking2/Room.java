package pl.com.bottega.jpatraining.locking2;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
class Room {
    @Id
    private Long id;

    Room() {}

    public Room(Long id) {
        this.id = id;
    }
}

@Entity
@Table(
    name = "reservations",
    indexes = {
        @Index(columnList = "fromInclusive"),
        @Index(columnList = "untilExclusive"),
        @Index(columnList = "customerId")
    })
@NamedQuery(
    name = Reservation.COLLISIONS_EXIST_QUERY, // makeResrvation
    query = "SELECT count(r) > 0 " +
        "FROM Reservation r " +
        "WHERE r.room.id = :roomId AND ((:start <= r.fromInclusive AND :end > r.fromInclusive) " +
        "OR (:start >= r.fromInclusive AND :end <= r.untilExclusive) " +
        "OR (:start < r.untilExclusive AND :end >= r.untilExclusive))"
)
@NamedQuery(
    name = Reservation.IS_RESERVED_QUERY, // isReserved
    query = "SELECT count(r) > 0 " +
        "FROM Reservation r " +
        "WHERE r.room.id = :roomId AND :at >= r.fromInclusive AND :at < r.untilExclusive AND r.customerId = :customerId"
)
@NamedQuery(
    name = Reservation.COUNT_RESERVATIONS, // count
    query = "SELECT count(r) FROM Reservation r"
)
class Reservation {

    static final String COLLISIONS_EXIST_QUERY = "COLLISIONS_EXIST_QUERY";
    static final String COUNT_RESERVATIONS = "COUNT_RESERVATIONS";
    static final String IS_RESERVED_QUERY = "IS_RESERVED_QUERY";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    Room room;

    Long customerId;

    LocalDate fromInclusive;

    LocalDate untilExclusive;

    public Reservation(Room room, Long customerId, LocalDate fromInclusive, LocalDate untilExclusive) {
        this.room = room;
        this.customerId = customerId;
        this.fromInclusive = fromInclusive;
        this.untilExclusive = untilExclusive;
    }
}