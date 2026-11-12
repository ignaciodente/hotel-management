package hotelmanagement.model;

import hotelmanagement.enums.RoomLevel;
import hotelmanagement.enums.RoomStatus;
import hotelmanagement.interfaces.Identifiable;

public class Room implements Identifiable {

    private int roomId;
    private int number;
    private int floor;
    private int capacity;
    private RoomLevel level;
    private RoomStatus status;
    private String unavailableReason;

    public Room(int number, int floor, int capacity, RoomLevel level) {
        this.number = number;
        this.floor = floor;
        this.capacity = capacity;
        this.level = level;
        this.status = RoomStatus.AVAILABLE;
        this.unavailableReason = null;
    }

    public Room(int roomId, int number, int floor, int capacity, RoomLevel level,
                RoomStatus status, String unavailableReason) {
        this(number, floor, capacity, level);
        this.roomId = roomId;
        this.status = status;
        this.unavailableReason = unavailableReason;
    }

    @Override
    public int getId() {
        return roomId;
    }

    public void setId(int roomId) {
        this.roomId = roomId;
    }

    public int getNumber() {
        return number;
    }

    public int getFloor() {
        return floor;
    }

    public int getCapacity() {
        return capacity;
    }

    public RoomLevel getLevel() {
        return level;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public String getUnavailableReason() {
        return unavailableReason;
    }

    public void changeStatus(RoomStatus status, String reason) {
        this.status = status;

        if (status == RoomStatus.AVAILABLE) {
            this.unavailableReason = null;
        } else {
            this.unavailableReason = reason;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;
        return roomId == ((Room) o).roomId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(roomId);
    }
}