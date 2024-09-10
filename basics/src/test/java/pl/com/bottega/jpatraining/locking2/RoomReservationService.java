package pl.com.bottega.jpatraining.locking2;

import jakarta.persistence.LockModeType;
import jakarta.persistence.RollbackException;
import pl.com.bottega.jpatraining.EntityManagerTemplate;

import java.time.LocalDate;

class RoomReservationService {

    private final EntityManagerTemplate entityManagerTemplate;

    public RoomReservationService(EntityManagerTemplate entityManagerTemplate) {
        this.entityManagerTemplate = entityManagerTemplate;
    }

    public void makeReservation(MakeReservationCommand command) throws RoomNotAvailableException {
        var reservationMade = false;
        while (!reservationMade) {
            try {
                entityManagerTemplate.executeInTx((em) -> {
                    var room = em.find(Room.class, command.roomId, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                    Boolean collisionsExist = em.createNamedQuery(Reservation.COLLISIONS_EXIST_QUERY, Boolean.class)
                            .setParameter("start", command.fromInclusive)
                            .setParameter("end", command.untilExclusive)
                            .setParameter("roomId", command.roomId)
                            .getSingleResult();
                    if (collisionsExist) {
                        throw new RoomNotAvailableException();
                    }
                    em.persist(new Reservation(room, command));
                });
                reservationMade = true;
            } catch (RollbackException ex) {

            }
        }
    }

    public boolean isReserved(ReservationQuery reservationQuery) {
        return entityManagerTemplate.getEntityManager().createNamedQuery(Reservation.IS_RESERVED_QUERY, Boolean.class)
                .setParameter("at", reservationQuery.at)
                .setParameter("roomId", reservationQuery.roomId)
                .setParameter("customerId", reservationQuery.customerId).getSingleResult();
    }

    public Integer reservationsCount() {
        return entityManagerTemplate.getEntityManager().createNamedQuery(Reservation.COUNT_RESERVATIONS, Long.class).getSingleResult().intValue();
    }
}

class RoomNotAvailableException extends RuntimeException {
}

class MakeReservationCommand {
    Long roomId;
    Long customerId;
    LocalDate fromInclusive;
    LocalDate untilExclusive;

    public MakeReservationCommand(Long roomId, Long customerId, LocalDate fromInclusive, LocalDate untilExclusive) {
        this.roomId = roomId;
        this.customerId = customerId;
        this.fromInclusive = fromInclusive;
        this.untilExclusive = untilExclusive;
    }
}

class ReservationQuery {
    Long roomId;
    Long customerId;
    LocalDate at;

    public ReservationQuery(Long roomId, Long customerId, LocalDate at) {
        this.roomId = roomId;
        this.customerId = customerId;
        this.at = at;
    }
}
