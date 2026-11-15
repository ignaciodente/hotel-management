package hotelmanagement.enums;

public enum RoomLevel {
    STANDARD(1.0),
    SUPERIOR(1.3),
    SUITE(1.8),
    PRESIDENTIAL_SUITE(3.0);

    private final double multiplier;

    RoomLevel(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getMultiplier() {
        return multiplier;
    }
}