package hotelmanagement.model;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import hotelmanagement.enums.*;
import hotelmanagement.exceptions.*;
import hotelmanagement.generics.Registry;
import hotelmanagement.persistence.*;


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

    public Hotel(String name, Double basePrice, RoomRepository roomRepository, GuestRepository guestRepository, UserRepository userRepository) {

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

    ////// METODOS backups
    public void performBackup(String folder) throws AccessDeniedException {

        checkPermission(Permission.PERFORM_BACKUP);

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

    ///METODOS USERS >> login / logout / createUser/ deactivateUser / revokePermission / grantPermission
    ///
    ///login
    public void login(String username, String password) throws InvalidCredentialsException {
        for (User user : users.list()) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password) && user.isActive()) {
                currentUser = user;
                return;
            }
        }
        throw new InvalidCredentialsException("Usuario o contraseña incorrectos, o cuenta desactivada");
    }

    /// logout
    public void logout() {
        currentUser = null;
    }

    ///createUser
    public void createUser(User u) throws AccessDeniedException, UserException {

        checkPermission(Permission.MANAGE_USERS);

        if (u == null) {
            throw new UserException("El usuario no puede ser null");
        }

        userRepository.insert(u);
        users.add(u);
    }

    /// deactivateUser
    /*este código supone que User tiene el metodo setActive(boolean) y que EntityNotFoundExceptio tiene un constructor sin argumentos.*/
    public void deactivateUser(int userId) throws AccessDeniedException, EntityNotFoundException {

        checkPermission(Permission.MANAGE_USERS);

        User user = users.findById(userId);

        if (user == null) {
            throw new EntityNotFoundException("Usuario no encontrado");
        }

        user.setActive(false);
        userRepository.update(user);

        if (currentUser == user) {
            logout();
        }
    }

    /// METODOS PERMISOS
    ///
    /// checkPermission
    /*Para que funcione checkPermission, se debe implementar en User estos métodos:
    public boolean isActive() {
        return active;
    }
    public Set<Permission> getPermissions() {
        return permissions;
    }*/
    private void checkPermission(Permission p) throws AccessDeniedException {

        if (p == null
                || currentUser == null
                || !currentUser.isActive()
                || !currentUser.getPermissions().contains(p)) {

            throw new AccessDeniedException("Acceso denegado: el usuario no tiene permisos para realizar esta operación");
        }
    }

    ///grantPermission
    /*Importante: ambos métodos suponen que getPermissions() devuelve un Set<Permission>
    modificable y que userRepository.update(user) también guarda los cambios de permisos
    en MySQL. Esto último hay que verificarlo con tu compañero.*/
    public void grantPermission(int userId, Permission p)
            throws AccessDeniedException, EntityNotFoundException,
            PermissionException {

        checkPermission(Permission.MANAGE_USERS);

        if (p == null) {
            throw new PermissionException("El permiso no puede ser null");
        }

        User user = users.findById(userId);

        if (user == null) {
            throw new EntityNotFoundException("Usuario no encontrado");
        }

        user.getPermissions().add(p);
        userRepository.update(user);
    }

    ///revokePermission
    public void revokePermission(int userId, Permission p)
            throws AccessDeniedException, EntityNotFoundException,
            PermissionException {

        checkPermission(Permission.MANAGE_USERS);

        if (p == null) {
            throw new PermissionException("El permiso no puede ser null");
        }

        User user = users.findById(userId);

        if (user == null) {
            throw new EntityNotFoundException("Usuario no encontrado");
        }

        user.getPermissions().remove(p);
        userRepository.update(user);
    }

    ///METODOS HUESPEDES >>> registerGuest / findGuestByDni / isAvailable
    ///
    /// registerGuest
    public void registerGuest(Guest g) throws IllegalArgumentException {

        if (g == null) {
            throw new IllegalArgumentException("El huésped no puede ser null");
        }

        guestRepository.insert(g);
        guests.add(g);
    }


    /// findGuestByDni
    public Guest findGuestByDni(String dni)
            throws EntityNotFoundException {

        if (dni == null || dni.isBlank()) {
            throw new EntityNotFoundException("DNI inválido");
        }

        for (Guest guest : guests.list()) {
            if (dni.equals(guest.getDni())) {
                return guest;
            }
        }

        throw new EntityNotFoundException("Huésped no encontrado");
    }

    /// isAvailable
    /*ahora verifica que la habitación esté disponible y que no tenga reservas superpuestas en estado ACTIVE o IN_PROGRESS.
    Las reservas COMPLETED y CANCELLED no bloquean nuevas reservas.*/
    public boolean isAvailable(Room r, LocalDate from, LocalDate to) {

        if (r == null || from == null || to == null
                || !from.isBefore(to)) {
            return false;
        }

        if (r.getStatus() != RoomStatus.AVAILABLE) {
            return false;
        }

        for (Reservation reservation : reservations.list()) {

            if (reservation.getRoom().getId() == r.getId()  && (reservation.getStatus() == ReservationStatus.ACTIVE
                    || reservation.getStatus() == ReservationStatus.IN_PROGRESS)
                    && reservation.overlapsWith(from, to)) {
                return false;
            }
        }
        return true;
    }

    ///METODOS RESERVACIONES >>> createReservation / cancelReservation / checkIn / checkOut / changeRoomStatus
    ///
    /// createReservation
    /*comprueba permisos, valida los datos, verifica disponibilidad, crea la reserva y la guarda en MySQL y en el registro.*/

    public Reservation createReservation(Guest g, Room r, LocalDate from, LocalDate to, int numberOfGuests)
            throws AccessDeniedException, InvalidReservationException, RoomNotAvailableException, CapacityExceededException {

        checkPermission(Permission.MANAGE_RESERVATIONS);

        if (g == null || r == null || from == null || to == null || !from.isBefore(to) || numberOfGuests <= 0) {
            throw new InvalidReservationException("La reserva no puede realizarse");
        }

        if (numberOfGuests > r.getCapacity()) {
            throw new CapacityExceededException("La cantidad de huéspedes supera la capacidad máxima de la habitación");
        }

        if (!isAvailable(r, from, to)) {
            throw new RoomNotAvailableException( "La habitación no está disponible para las fechas solicitadas");
        }

        Reservation reservation = new Reservation(
                g, r, currentUser, from, to, numberOfGuests
        );

        reservationRepository.insert(reservation);
        reservations.add(reservation);

        return reservation;
    }

    /// cancelReservation
    public void cancelReservation(int reservationId) throws AccessDeniedException, EntityNotFoundException, InvalidReservationException {

        checkPermission(Permission.MANAGE_RESERVATIONS);

        Reservation reservation = reservations.findById(reservationId);

        if (reservation == null) {
            throw new EntityNotFoundException("Reserva no encontrada");
        }

        reservation.cancel();
        reservationRepository.update(reservation);
    }

    /// checkIn
    /*valida permisos, reserva, ocupantes y que no exista un check-in activo para esa reserva. Después crea y guarda la estadía.*/
    public Stay checkIn(int reservationId, List<Guest> occupants) throws AccessDeniedException, EntityNotFoundException, InvalidReservationException, CapacityExceededException {

        checkPermission(Permission.DO_CHECK_IN_OUT);

        Reservation reservation = reservations.findById(reservationId);

        if (reservation == null) {
            throw new EntityNotFoundException("Reserva no encontrada");
        }

        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new InvalidReservationException( "No se puede realizar el check-in porque la reserva no está activa");
        }

        if (occupants == null || occupants.isEmpty()) {
            throw new InvalidReservationException("No se puede realizar el check-in porque no se especificaron huéspedes");
        }

        if (occupants.size() > reservation.getNumberOfGuests() || occupants.size() > reservation.getRoom().getCapacity()) {
            throw new CapacityExceededException("La cantidad de ocupantes supera el número de huéspedes reservados o la capacidad máxima de la habitación");
        }

        for (Stay existingStay : stays.list()) {
            if (existingStay.isActive() && existingStay.getReservation().getId() == reservationId) {
                throw new InvalidReservationException( "No se puede realizar el check-in porque la reserva ya tiene una estadía activa");
            }
        }

        Stay stay = new Stay(reservation, occupants, LocalDateTime.now());

        stayRepository.insert(stay);
        stays.add(stay);

        reservation.setStatus(ReservationStatus.IN_PROGRESS);
        reservationRepository.update(reservation);

        return stay;
    }

    /// checkOut
    /*busca la estadía, calcula el importe final mediante close() y guarda el check-out en MySQL
    si la estadía ya está cerrada, lanza InvalidReservationException. Si está activa, realiza el check-out, calcula el importe y actualiza la reserva.*/
    public void checkOut(int stayId)
            throws AccessDeniedException, EntityNotFoundException,
            InvalidReservationException {

        checkPermission(Permission.DO_CHECK_IN_OUT);

        Stay stay = stays.findById(stayId);

        if (stay == null) {
            throw new EntityNotFoundException("Estadía no encontrada");
        }

        if (!stay.isActive()) {
            throw new InvalidReservationException("No se puede realizar el check-out porque la estadía ya está cerrada");
        }

        stay.close(LocalDateTime.now(), basePrice);
        stayRepository.update(stay);

        Reservation reservation = stay.getReservation();
        reservation.setStatus(ReservationStatus.COMPLETED);
        reservationRepository.update(reservation);
    }

    ///changeRoomStatus
    /*¿Qué mejoramos?
    - Verifica el permiso MANAGE_ROOMS.
    - Comprueba que la habitación exista.
    - Exige un motivo para CLEANING, DISINFECTION, REPAIR y CLOSED.
    - Evita bloquear una habitación que tiene una estadía activa.
    - Utiliza room.changeStatus() de tu clase Room, que ya limpia automáticamente el motivo cuando el estado pasa a AVAILABLE.
    - Guarda el cambio en MySQL.*/
    public void changeRoomStatus(int roomId, RoomStatus status, String reason)
            throws AccessDeniedException, EntityNotFoundException,
            IllegalArgumentException, IllegalStateException {

        checkPermission(Permission.MANAGE_ROOMS);

        Room room = rooms.findById(roomId);

        if (room == null) {
            throw new EntityNotFoundException("Habitación no encontrada");
        }

        if (status == null) {
            throw new IllegalArgumentException("El estado no puede ser null");
        }

        if (status != RoomStatus.AVAILABLE
                && (reason == null || reason.isBlank())) {
            throw new IllegalArgumentException(
                    "Debe indicar un motivo para cambiar el estado"
            );
        }

        if (status != RoomStatus.AVAILABLE) {
            for (Stay stay : stays.list()) {
                if (stay.isActive() && stay.getRoom().getId() == roomId) {
                    throw new IllegalStateException(
                            "No se puede bloquear una habitación con huéspedes alojados"
                    );
                }
            }
        }
        room.changeStatus(status, reason);
        roomRepository.update(room);
    }

    /// findAvailableRooms
    /*devuelve las habitaciones disponibles entre dos fechas que tengan capacidad suficiente para los huéspedes solicitados.*/
    public List<Room> findAvailableRooms(LocalDate from, LocalDate to,
                                         int numberOfGuests)
            throws InvalidReservationException {

        if (from == null || to == null || !from.isBefore(to)) {
            throw new InvalidReservationException("Las fechas ingresadas son inválidas. La fecha de inicio debe ser anterior a la fecha de fin");
        }

        if (numberOfGuests <= 0) {
            throw new InvalidReservationException("La cantidad de huéspedes debe ser mayor a cero");
        }

        List<Room> availableRooms = new ArrayList<>();

        for (Room room : rooms.list()) {
            if (room.getCapacity() >= numberOfGuests && isAvailable(room, from, to)) {
                availableRooms.add(room);
            }
        }

        return availableRooms;
    }

    ///listOccupiedRooms
    /*devuelve todas las estadías activas, es decir, habitaciones con huéspedes que todavía no hicieron el check-out.
    No necesita excepciones, porque solamente consultan información y devuelven una lista vacía cuando no encuentran resultados*/
    public List<Stay> listOccupiedRooms() {

        List<Stay> occupiedRooms = new ArrayList<>();

        for (Stay stay : stays.list()) {
            if (stay.isActive()) {
                occupiedRooms.add(stay);
            }
        }

        return occupiedRooms;
    }


    ///listUnavailableRooms
    /*devuelve las habitaciones en limpieza, desinfección, reparación o cerradas.
    No necesita excepciones, porque solamente consultan información y devuelven una lista vacía cuando no encuentran resultados.*/
    public List<Room> listUnavailableRooms() {

        List<Room> unavailableRooms = new ArrayList<>();

        for (Room room : rooms.list()) {
            if (room.getStatus() != RoomStatus.AVAILABLE) {
                unavailableRooms.add(room);
            }
        }

        return unavailableRooms;
    }

    /// getGuestHistory
    /*devuelve todas las estadías en las que participó el huésped, tanto activas como finalizadas. No duplica estadías.
    * El huésped es titular de la reserva.
    El huésped figura como ocupante.
    El huésped es titular y ocupante: la estadía se agrega una sola vez gracias al continue.*/
    public List<Stay> getGuestHistory(Guest g) {

        List<Stay> history = new ArrayList<>();

        if (g == null) {
            return history;
        }

        for (Stay stay : stays.list()) {

            if (stay.getReservation().getHolder().getId() == g.getId()) {
                history.add(stay);
                continue;
            }

            for (Guest occupant : stay.getOccupants()) {

                if (occupant.getId() == g.getId()) {
                    history.add(stay);
                    break;
                }
            }
        }

        return history;
    }

    /// getGuestReport
    /*genera un reporte con los datos del huésped, sus estadías, habitaciones, fechas e importes.*/
    public String getGuestReport(int guestId) throws EntityNotFoundException {

        Guest guest = guests.findById(guestId);

        if (guest == null) {
            throw new EntityNotFoundException("Huésped no encontrado");
        }

        List<Stay> history = getGuestHistory(guest);

        StringBuilder report = new StringBuilder();

        report.append("REPORTE DEL HUÉSPED\n");
        report.append("DNI: ").append(guest.getDni()).append("\n");
        report.append("País: ").append(guest.getCountry()).append("\n");
        report.append("Domicilio: ").append(guest.getHomeAddress()).append("\n");
        report.append("Cantidad de estadías: ").append(history.size()).append("\n");

        for (Stay stay : history) {
            report.append("\nEstadía #").append(stay.getId());
            report.append("\nHabitación: ").append(stay.getRoom().getNumber());
            report.append("\nCheck-in: ").append(stay.getCheckInDate());
            report.append("\nCheck-out: ").append(
                    stay.isActive() ? "En curso" : stay.getCheckOutDate()
            );
            report.append("\nImporte: ").append(
                    stay.isActive() ? "Pendiente" : stay.getTotalAmount()
            );
            report.append("\n");
        }

        return report.toString();
    }
}



