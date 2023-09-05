package pl.com.bottega.jpatraining.locking2;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.time.LocalDate;

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
public class Reservation {

    static final String COLLISIONS_EXIST_QUERY = "COLLISIONS_EXIST_QUERY";
    static final String COUNT_RESERVATIONS = "COUNT_RESERVATIONS";
    static final String IS_RESERVED_QUERY = "IS_RESERVED_QUERY";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Room room;

    private Long customerId;

    private LocalDate fromInclusive;
    private LocalDate untilExclusive;

    Reservation() {
    }

    public Reservation(Room room, MakeReservationCommand command) {
        this.room = room;
        this.customerId = command.customerId;
        this.fromInclusive = command.fromInclusive;
        this.untilExclusive = command.untilExclusive;
    }
}
