package pl.com.bottega.jpatraining.locking2;

import pl.com.bottega.jpatraining.EntityManagerTemplate;

import java.time.LocalDate;

class RoomReservationService {

    private final EntityManagerTemplate entityManagerTemplate;

    public RoomReservationService(EntityManagerTemplate entityManagerTemplate) {
        this.entityManagerTemplate = entityManagerTemplate;
    }

    public void makeReservation(MakeReservationCommand command) throws RoomNotAvailableException {

    }

    public boolean isReserved(ReservationQuery reservationQuery) {
        return false;
    }

    public Integer reservationsCount() {
        return 0;
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
