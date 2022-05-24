package pl.com.bottega.jpatraining.locking2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.com.bottega.jpatraining.BaseJpaTest;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RoomReservationTest extends BaseJpaTest {

    Long user1 = 1L;
    Long user2 = 2L;
    Long room1 = 1L;
    Long room2 = 2L;
    LocalDate firstDay = LocalDate.ofYearDay(2022, 1);
    LocalDate fifthDay = LocalDate.ofYearDay(2022, 5);

    RoomReservationService sut = new RoomReservationService(this.template);

    @BeforeEach
    public void setup() {
        template.executeInTx((em) -> {
            em.persist(new Room(room1));
            em.persist(new Room(room2));
        });
    }

    @Test
    public void makesSingleReservation() {
        MakeReservationCommand command = new MakeReservationCommand(room1, user1, firstDay, fifthDay);

        sut.makeReservation(command);
        template.close();

        assertThat(sut.isReserved(new ReservationQuery(room1, user1, firstDay))).isTrue();
        assertThat(sut.isReserved(new ReservationQuery(room1, user1, firstDay.plusDays(1L)))).isTrue();
        assertThat(sut.isReserved(new ReservationQuery(room1, user1, firstDay.plusDays(2L)))).isTrue();
        assertThat(sut.isReserved(new ReservationQuery(room1, user1, firstDay.plusDays(3L)))).isTrue();
        assertThat(sut.isReserved(new ReservationQuery(room1, user1, fifthDay))).isFalse();
        assertThat(sut.reservationsCount()).isEqualTo(1);
    }

    @Test
    public void makesMultipleReservations() {
        sut.makeReservation(new MakeReservationCommand(room1, user1, firstDay, firstDay.plusDays(1)));
        sut.makeReservation(new MakeReservationCommand(room2, user2, firstDay, firstDay.plusDays(1)));
        template.close();

        assertThat(sut.isReserved(new ReservationQuery(room1, user1, firstDay))).isTrue();
        assertThat(sut.isReserved(new ReservationQuery(room1, user2, firstDay))).isFalse();
        assertThat(sut.isReserved(new ReservationQuery(room2, user2, firstDay))).isTrue();
        assertThat(sut.isReserved(new ReservationQuery(room2, user1, firstDay))).isFalse();
        assertThat(sut.reservationsCount()).isEqualTo(2);
    }

    @Test
    public void doesNotAllowReservingOccupiedRooms() {
        sut.makeReservation(new MakeReservationCommand(user1, room1, firstDay, fifthDay));
        template.close();

        assertThatThrownBy(() -> sut.makeReservation(new MakeReservationCommand(room1, user2, firstDay, fifthDay)))
                .isInstanceOf(RoomNotAvailableException.class);
        assertThatThrownBy(() -> sut.makeReservation(new MakeReservationCommand(room1, user2, firstDay, firstDay.plusDays(1))))
                .isInstanceOf(RoomNotAvailableException.class);
        assertThatThrownBy(() -> sut.makeReservation(new MakeReservationCommand(room1, user2, firstDay.minusDays(1), firstDay.plusDays(1))))
                .isInstanceOf(RoomNotAvailableException.class);
        assertThatThrownBy(() -> sut.makeReservation(new MakeReservationCommand(room1, user2, firstDay.plusDays(1), fifthDay.plusDays(1))))
                .isInstanceOf(RoomNotAvailableException.class);
        assertThatThrownBy(() -> sut.makeReservation(new MakeReservationCommand(room1, user2, firstDay.plusDays(1), fifthDay)))
                .isInstanceOf(RoomNotAvailableException.class);
        assertThat(sut.reservationsCount()).isEqualTo(1);
    }

    @Test
    public void allowsNonColidingReservations() {
        sut.makeReservation(new MakeReservationCommand(room1, user1, firstDay, fifthDay));
        sut.makeReservation(new MakeReservationCommand(room1, user1, firstDay.minusDays(2), firstDay));
        sut.makeReservation(new MakeReservationCommand(room1, user1, fifthDay, fifthDay.plusDays(2)));
        template.close();

        assertThat(sut.isReserved(new ReservationQuery(user1, room1, firstDay))).isTrue();
        assertThat(sut.isReserved(new ReservationQuery(user1, room1, firstDay.minusDays(1)))).isTrue();
        assertThat(sut.isReserved(new ReservationQuery(user1, room1, fifthDay))).isTrue();
        assertThat(sut.isReserved(new ReservationQuery(user1, room1, fifthDay.plusDays(1)))).isTrue();
        assertThat(sut.reservationsCount()).isEqualTo(3);
    }

    Callable<Void> userReservationMaker(Long userId, int reservationsCount, int reservationLength) {
        return () -> {
            IntStream.rangeClosed(1, reservationsCount).forEach((i) -> {
                LocalDate from = firstDay.plusDays((i - 1) * reservationLength);
                LocalDate until = firstDay.plusDays(i * reservationLength);
                try {
                    sut.makeReservation(new MakeReservationCommand(room1, userId, from, until));
                    System.out.printf("thread=%s made reservation from=%s until=%s%n", Thread.currentThread().getName(), from, until);
                } catch (RoomNotAvailableException roomNotAvailableException) {
                    System.out.printf("thread=%s failed to reserve from=%s until=%s%n", Thread.currentThread().getName(), from, until);
                }
            });
            return null;
        };
    }

    @Test
    public void makesReservationsInMultithreadedEnvironment() throws InterruptedException {
        int reservationsCount = 30;
        int reservationLength = 2;

        int usersCount = 5;
        Collection<Callable<Void>> work = IntStream.rangeClosed(1, usersCount)
                .mapToObj(i -> userReservationMaker((long) i, reservationsCount, reservationLength))
                .collect(Collectors.toList());
        ExecutorService executorService = Executors.newFixedThreadPool(usersCount);
        List<Future<Void>> results = executorService.invokeAll(work);
        executorService.shutdown();

        results.forEach(this::checkResult);
        assertThat(sut.reservationsCount()).isEqualTo(reservationsCount);
    }

    private void checkResult(Future<Void> f) {
        assertThatCode(f::get).doesNotThrowAnyException();
    }
}
