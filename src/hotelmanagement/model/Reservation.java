package hotelmanagement.model;

import hotelmanagement.enums.ReservationStatus;
import hotelmanagement.interfaces.Identifiable;

import java.time.LocalDate;

public class Reservation implements Identifiable {

    private int reservationId;
    private Guest holder;
    private Room room;
    private User createdBy;
    private LocalDate startDate;
    private LocalDate endDate;
    private int numberOfGuests;
    private ReservationStatus status;

    public Reservation(Guest holder, Room room, User createdBy, LocalDate startDate,
                       LocalDate endDate, int numberOfGuests) {
        this.holder = holder;
        this.room = room;
        this.createdBy = createdBy;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberOfGuests = numberOfGuests;
        this.status = ReservationStatus.ACTIVE;
    }

    public Reservation(int reservationId, Guest holder, Room room, User createdBy, LocalDate startDate,
                       LocalDate endDate, int numberOfGuests, ReservationStatus status) {
        this(holder, room, createdBy, startDate, endDate, numberOfGuests);
        this.reservationId = reservationId;
        this.status = status;
    }

    @Override
    public int getId() {
        return reservationId;
    }

    public void setId(int reservationId) {
        this.reservationId = reservationId;
    }

    public Guest getHolder() {
        return holder;
    }

    public Room getRoom() {
        return room;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public boolean overlapsWith(LocalDate from, LocalDate to) {
        return startDate.isBefore(to) && from.isBefore(endDate);
    }

    public void cancel() throws InvalidReservationException {
        if (status != ReservationStatus.ACTIVE) {
            throw new InvalidReservationException("Reservation " + reservationId
                    + " cannot be cancelled because it is " + status);
        }
        status = ReservationStatus.CANCELLED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reservation)) return false;
        return reservationId == ((Reservation) o).reservationId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(reservationId);
    }
}