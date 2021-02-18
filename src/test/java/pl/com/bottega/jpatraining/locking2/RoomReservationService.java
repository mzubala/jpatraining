package pl.com.bottega.jpatraining.locking2;

import pl.com.bottega.jpatraining.EntityManagerTemplate;

import javax.persistence.LockModeType;
import javax.persistence.RollbackException;
import java.time.LocalDate;

class RoomReservationService {

    private static final Integer MAX_TRIES_COUNT = 5;

    private final EntityManagerTemplate entityManagerTemplate;

    public RoomReservationService(EntityManagerTemplate entityManagerTemplate) {
        this.entityManagerTemplate = entityManagerTemplate;
    }

    public void makeReservation(MakeReservationCommand command) throws RoomNotAvailableException {
        int triesCount = 0;
        while(triesCount < MAX_TRIES_COUNT) {
            try {
                tryMakingReservation(command);
                return;
            } catch (RollbackException rollbackException) {
                triesCount++;
            }
        }
        throw new RuntimeException("Failed to make reservation due to too many retries");
    }

    private void tryMakingReservation(MakeReservationCommand command) {
        entityManagerTemplate.executeInTx((em) -> {
            Room room = em.find(Room.class, command.roomId, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            Boolean colistionsExist = em.createNamedQuery(Reservation.COLLISIONS_EXIST_QUERY, Boolean.class)
                    .setParameter("start", command.fromInclusive)
                    .setParameter("end", command.untilExclusive)
                    .setParameter("roomId", command.roomId)
                    .getSingleResult();
            if(colistionsExist) {
                throw new RoomNotAvailableException();
            }
            em.persist(new Reservation(
                    room,
                    command
            ));
        });
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
