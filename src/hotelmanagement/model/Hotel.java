package hotelmanagement.model;
import java.util.List;
import hotelmanagement.exceptions.*;


public class Hotel {

    private String name;
    private Double basePrice;
    private User currentUser;

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

    public Hotel(String name, Double basePrice,
                 RoomRepository roomRepository,
                 GuestRepository guestRepository,
                 UserRepository userRepository) {

        this.name = name;
        this.basePrice = basePrice;
        this.currentUser = null;

        this.rooms = new Registry<>();
        this.guests = new Registry<>();
        this.users = new Registry<>();
        this.reservations = new Registry<>();
        this.stays = new Registry<>();

        this.roomRepository = roomRepository;
        this.guestRepository = guestRepository;
        this.userRepository = userRepository;

        this.reservationRepository =
                new ReservationRepository(guests, rooms, users);

        this.stayRepository =
                new StayRepository(reservations, guests);
    }

    ///-------------------------------------------------------------------GETTERS CLASE HOTEL
    public String getName() {
        return name;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public User getCurrentUser() {
        return currentUser;
    }
    ///-------------------------------------------------------------------SETTERS CLASE HOTEL
    public void setName(String name) {
        this.name = name;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    /* currentUser representa el usuario que tiene la sesión iniciada, y queremos controlar cuándo cambia.
    Que login() y logout() sean los únicos métodos que modifiquen ese atributo. La idea es que otras clases
    puedan consultar quién está conectado, pero no puedan cambiarlo arbitrariamente.*/

    ////>!!!!!!!!!!!!!!!!!!!!///
    /*Importante: primero cargamos habitaciones, huéspedes y usuarios porque reservas y estadías necesitan esos objetos para reconstruir sus relaciones.*/
    ///!!!!!!!!!!!!!!!!!!!!///

    ///-------------------------------------------------------------------METODOS

    public void loadData(){
        List<Room> roomList = roomRepository.findAll();
        for (Room room : roomList) {
            rooms.add(room);
        }

        List<Guest> guestList = guestRepository.findAll();
        for (Guest guest : guestList) {
            guests.add(guest);
        }

        List<User> userList = userRepository.findAll();
        for (User user : userList) {
            users.add(user);
        }

        /*recuperamos todas las reservas desde la base de datos:*/
        List<Reservation> reservationList = reservationRepository.findAll();
        for (Reservation reservation : reservationList) {
            reservations.add(reservation);
        }

        /*StayRepository necesita los registros de reservas y huéspedes, que ya cargamos anteriormente.*/
        List<Stay> stayList = stayRepository.findAll();
        for (Stay stay : stayList) {
            stays.add(stay);
        }
    }

    //////-------------------------------------------------------------------METODOS permisos y backups
    public void performBackup(String folder) throws AccessDeniedException {
        if (!checkPermission(Permission.PERFORM_BACKUP)) {
            throw new AccessDeniedException();
        }

        roomRepository.exportToCsv(folder);
        guestRepository.exportToCsv(folder);
        userRepository.exportToCsv(folder);
        reservationRepository.exportToCsv(folder);
        stayRepository.exportToCsv(folder);
    }

    /*Para que funcione checkPermission, se debe implementar en User estos métodos:
        public boolean isActive() {
            return active;
        }
        public Set<Permission> getPermissions() {
            return permissions;
        }*/
    public boolean checkPermission(Permission permission) {
        if (currentUser == null || !currentUser.isActive()) {
            return false;
        }
        return currentUser.getPermissions().contains(permission);
    }

    //////------METODOS USERS >> login / logout / createUser/ deactivateUser / revokePermission / grantPermission
    public void login(String username, String password) throws InvalidCredentialsException {
        for (User user : users.list()) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password) && user.isActive()) {
                currentUser = user;
                return;
            }
        }
        throw new InvalidCredentialsException();
    }

    public void logout() {
        currentUser = null;
    }

    public void createUser(User u) throws AccessDeniedException, IllegalArgumentException {

        if (!checkPermission(Permission.MANAGE_USERS)) {
            throw new AccessDeniedException();
        }

        if (u == null) {
            throw new IllegalArgumentException("El usuario no puede ser null");
        }

        userRepository.insert(u);
        users.add(u);
    }

    /*este código supone que User tiene el metodo setActive(boolean) y que
    EntityNotFoundExceptio tiene un constructor sin argumentos.*/
    public void deactivateUser(int userId) throws AccessDeniedException, EntityNotFoundException {

        if (!checkPermission(Permission.MANAGE_USERS)) {
            throw new AccessDeniedException();
        }

        User user = users.findById(userId);

        if (user == null) {
            throw new EntityNotFoundException();
        }

        user.setActive(false);
        userRepository.update(user);

        if (currentUser == user) {
            logout();
        }
    }

    /*Importante: ambos métodos suponen que getPermissions() devuelve un Set<Permission>
    modificable y que userRepository.update(user) también guarda los cambios de permisos
    en MySQL. Esto último hay que verificarlo con tu compañero.*/
    public void grantPermission(int userId, Permission p)
            throws AccessDeniedException, EntityNotFoundException, IllegalArgumentException {

        if (!checkPermission(Permission.MANAGE_USERS)) {
            throw new AccessDeniedException();
        }

        if (p == null) {
            throw new IllegalArgumentException("El permiso no puede ser null");
        }

        User user = users.findById(userId);

        if (user == null) {
            throw new EntityNotFoundException();
        }

        user.getPermissions().add(p);
        userRepository.update(user);
    }

    public void revokePermission(int userId, Permission p)
            throws AccessDeniedException, EntityNotFoundException, IllegalArgumentException {

        if (!checkPermission(Permission.MANAGE_USERS)) {
            throw new AccessDeniedException();
        }

        if (p == null) {
            throw new IllegalArgumentException("El permiso no puede ser null");
        }

        User user = users.findById(userId);

        if (user == null) {
            throw new EntityNotFoundException();
        }

        user.getPermissions().remove(p);
        userRepository.update(user);
    }

    ///------METODOS HUESPEDES >> registerGuest / findGuestByDni /

    /// registerGuest
    public void registerGuest(Guest g) throws IllegalArgumentException {

        if (g == null) {
            throw new IllegalArgumentException("El huésped no puede ser null");
        }

        guestRepository.insert(g);
        guests.add(g);
    }


    /// findGuestByDni
    public Guest findGuestByDni(String dni) throws IllegalArgumentException, EntityNotFoundException {

        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI no puede estar vacío");
        }

        for (Guest guest : guests.list()) {
            if (dni.equals(guest.getDni())) {
                return guest;
            }
        }

        throw new EntityNotFoundException();
    }









}



