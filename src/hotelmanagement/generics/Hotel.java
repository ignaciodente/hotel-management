package hotelmanagement.generics;

public class Hotel {

    private String name;
    private Double basePrice;

    private Registry<Room> rooms;
    private Registry<Guest> guests;
    private Registry<User> users;
    private Registry<Reservation> reservations;
    private Registry<Stay> stays;

    private RoomRepository roomRepository;
    private GuestRepository guestRepository;
    private UserRepository userRepository;
    private ReservationRepository reservationRepository;
    private StayRepository stayRepository;

    public Hotel(String name, Double basePrice, Registry<Room> rooms, Registry<Guest> guests, Registry<User> users, Registry<Reservation> reservations, Registry<Stay> stays, RoomRepository roomRepository, GuestRepository guestRepository, UserRepository userRepository, ReservationRepository reservationRepository, StayRepository stayRepository) {
        this.name = name;
        this.basePrice = basePrice;
        this.rooms = rooms;
        this.guests = guests;
        this.users = users;
        this.reservations = reservations;
        this.stays = stays;
        this.roomRepository = roomRepository;
        this.guestRepository = guestRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.stayRepository = stayRepository;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public Registry<Room> getRooms() {
        return rooms;
    }

    public void setRooms(Registry<Room> rooms) {
        this.rooms = rooms;
    }

    public Registry<Guest> getGuests() {
        return guests;
    }

    public void setGuests(Registry<Guest> guests) {
        this.guests = guests;
    }

    public Registry<User> getUsers() {
        return users;
    }

    public void setUsers(Registry<User> users) {
        this.users = users;
    }

    public Registry<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(Registry<Reservation> reservations) {
        this.reservations = reservations;
    }

    public Registry<Stay> getStays() {
        return stays;
    }

    public void setStays(Registry<Stay> stays) {
        this.stays = stays;
    }

    public RoomRepository getRoomRepository() {
        return roomRepository;
    }

    public void setRoomRepository(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public GuestRepository getGuestRepository() {
        return guestRepository;
    }

    public void setGuestRepository(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ReservationRepository getReservationRepository() {
        return reservationRepository;
    }

    public void setReservationRepository(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public StayRepository getStayRepository() {
        return stayRepository;
    }

    public void setStayRepository(StayRepository stayRepository) {
        this.stayRepository = stayRepository;
    }

    public void loadData(){

    }


}
