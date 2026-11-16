package hotelmanagement;

import hotelmanagement.enums.Permission;
import hotelmanagement.enums.ReservationStatus;
import hotelmanagement.enums.RoomLevel;
import hotelmanagement.enums.RoomStatus;
import hotelmanagement.enums.UserType;
import hotelmanagement.model.Administrator;
import hotelmanagement.model.Guest;
import hotelmanagement.model.Hotel;
import hotelmanagement.model.Receptionist;
import hotelmanagement.model.Reservation;
import hotelmanagement.model.Room;
import hotelmanagement.model.Stay;
import hotelmanagement.model.User;
import hotelmanagement.persistence.GuestRepository;
import hotelmanagement.persistence.RoomRepository;
import hotelmanagement.persistence.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Console menu of the hotel system. It only reads input and shows results:
 * every rule and validation lives in Hotel. Everything shown to the user is in Spanish.
 */
public class Main {

    private static final double BASE_PRICE = 50000.0;
    private static final String DEFAULT_BACKUP_FOLDER = "backup";

    private static final Scanner scanner = new Scanner(System.in);
    private static Hotel hotel;

    public static void main(String[] args) {
        hotel = new Hotel("Hotel Management", BASE_PRICE,
                new RoomRepository(), new GuestRepository(), new UserRepository());

        try {
            hotel.loadData();
        } catch (Exception e) {
            System.out.println("No se pudieron cargar los datos: " + e.getMessage());
            return;
        }

        if (!hotel.hasUsers()) {
            createFirstAdministrator();
        }

        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("=== " + hotel.getName() + " ===");
            System.out.println("1. Iniciar sesión");
            System.out.println("0. Salir");
            int option = readInt("Opción: ");

            switch (option) {
                case 1:
                    if (login()) {
                        mainMenu();
                    }
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
        System.out.println("Hasta luego.");
    }

    // ------------------------------------------------------------------ session

    /**
     * Asks for the data of the first administrator. It only runs when there are no users,
     * and it keeps asking until the administrator is created.
     */
    private static void createFirstAdministrator() {
        System.out.println("Todavía no hay usuarios. Creá el primer administrador.");
        boolean created = false;
        while (!created) {
            try {
                String firstName = readText("Nombre: ");
                String lastName = readText("Apellido: ");
                String dni = readText("DNI: ");
                String username = readText("Usuario: ");
                String password = readText("Contraseña: ");

                hotel.createFirstAdministrator(new Administrator(firstName, lastName, dni, username, password));
                System.out.println("Administrador " + username + " creado. Ya podés iniciar sesión.");
                created = true;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static boolean login() {
        String username = readText("Usuario: ");
        String password = readText("Contraseña: ");
        try {
            hotel.login(username, password);
            System.out.println("Bienvenido/a, " + hotel.getCurrentUser().getFullName() + ".");
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * Shows the options of the logged-in user until they log out. Only the sections
     * the user has permission for are shown. Hotel checks the permission again in
     * every operation, so the menu is not the only protection.
     */
    private static void mainMenu() {
        boolean loggedIn = true;
        while (loggedIn) {
            User user = hotel.getCurrentUser();

            boolean reservations = user.hasPermission(Permission.MANAGE_RESERVATIONS);
            boolean checkInOut = user.hasPermission(Permission.DO_CHECK_IN_OUT);
            boolean rooms = user.hasPermission(Permission.MANAGE_ROOMS);
            boolean users = user.hasPermission(Permission.MANAGE_USERS);
            boolean backup = user.hasPermission(Permission.PERFORM_BACKUP);

            System.out.println();
            System.out.println("=== Menú principal (" + user.getUsername() + ") ===");
            if (reservations) {
                System.out.println("1. Reservas");
            }
            if (checkInOut) {
                System.out.println("2. Check-in y check-out");
            }
            if (rooms) {
                System.out.println("3. Habitaciones");
            }
            System.out.println("4. Huéspedes");
            if (users) {
                System.out.println("5. Usuarios y permisos");
            }
            if (backup) {
                System.out.println("6. Backup");
            }
            System.out.println("0. Cerrar sesión");
            int option = readInt("Opción: ");

            switch (option) {
                case 1:
                    if (reservations) {
                        reservationsMenu();
                    } else {
                        System.out.println("Opción inválida.");
                    }
                    break;
                case 2:
                    if (checkInOut) {
                        checkInOutMenu();
                    } else {
                        System.out.println("Opción inválida.");
                    }
                    break;
                case 3:
                    if (rooms) {
                        roomsMenu();
                    } else {
                        System.out.println("Opción inválida.");
                    }
                    break;
                case 4:
                    guestsMenu();
                    break;
                case 5:
                    if (users) {
                        usersMenu();
                    } else {
                        System.out.println("Opción inválida.");
                    }
                    break;
                case 6:
                    if (backup) {
                        performBackup();
                    } else {
                        System.out.println("Opción inválida.");
                    }
                    break;
                case 0:
                    hotel.logout();
                    loggedIn = false;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    // ------------------------------------------------------------------ reservations

    private static void reservationsMenu() {
        System.out.println();
        System.out.println("--- Reservas ---");
        System.out.println("1. Buscar habitaciones disponibles");
        System.out.println("2. Crear reserva");
        System.out.println("3. Cancelar reserva");
        System.out.println("4. Reservas activas");
        System.out.println("0. Volver");
        int option = readInt("Opción: ");

        switch (option) {
            case 1:
                searchAvailableRooms();
                break;
            case 2:
                createReservation();
                break;
            case 3:
                cancelReservation();
                break;
            case 4:
                printReservations(hotel.listActiveReservations());
                break;
            case 0:
                break;
            default:
                System.out.println("Opción inválida.");
        }
    }

    private static void searchAvailableRooms() {
        try {
            LocalDate from = readDate("Fecha de ingreso (aaaa-mm-dd): ");
            LocalDate to = readDate("Fecha de salida (aaaa-mm-dd): ");
            int guests = readInt("Cantidad de huéspedes: ");

            List<Room> rooms = hotel.findAvailableRooms(from, to, guests);
            printRooms(rooms);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * The receptionist picks the room from the available ones, so a room that is
     * not available for those dates can never be chosen.
     */
    private static void createReservation() {
        try {
            Guest holder = hotel.findGuestByDni(readText("DNI del titular: "));
            LocalDate from = readDate("Fecha de ingreso (aaaa-mm-dd): ");
            LocalDate to = readDate("Fecha de salida (aaaa-mm-dd): ");
            int guests = readInt("Cantidad de huéspedes: ");

            List<Room> available = hotel.findAvailableRooms(from, to, guests);
            if (available.isEmpty()) {
                System.out.println("No hay habitaciones disponibles para esas fechas y esa cantidad de huéspedes.");
                return;
            }
            printRooms(available);

            int number = readInt("Número de habitación: ");
            Room room = null;
            for (Room r : available) {
                if (r.getNumber() == number) {
                    room = r;
                }
            }
            if (room == null) {
                System.out.println("La habitación " + number + " no está en la lista de habitaciones disponibles.");
                return;
            }

            int id = hotel.createReservation(holder, room, from, to, guests).getId();
            System.out.println("Reserva " + id + " creada.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void cancelReservation() {
        try {
            int id = readInt("Id de la reserva: ");
            hotel.cancelReservation(id);
            System.out.println("Reserva " + id + " cancelada.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // ------------------------------------------------------------------ check-in / check-out

    private static void checkInOutMenu() {
        System.out.println();
        System.out.println("--- Check-in y check-out ---");
        System.out.println("1. Check-in");
        System.out.println("2. Check-out");
        System.out.println("3. Habitaciones ocupadas");
        System.out.println("0. Volver");
        int option = readInt("Opción: ");

        switch (option) {
            case 1:
                checkIn();
                break;
            case 2:
                checkOut();
                break;
            case 3:
                printStays(hotel.listOccupiedRooms());
                break;
            case 0:
                break;
            default:
                System.out.println("Opción inválida.");
        }
    }

    /**
     * Asks for the DNI of every occupant. Each one must already be registered as a guest,
     * and the holder of the reservation must be among them.
     */
    private static void checkIn() {
        try {
            int reservationId = readInt("Id de la reserva: ");

            List<Guest> occupants = new ArrayList<>();
            System.out.println("Ingresá el DNI de cada ocupante. Dejalo vacío para terminar.");
            String dni = readText("DNI: ");
            while (!dni.isBlank()) {
                occupants.add(hotel.findGuestByDni(dni));
                dni = readText("DNI: ");
            }

            int stayId = hotel.checkIn(reservationId, occupants).getId();
            System.out.println("Check-in realizado. Se inició la estadía " + stayId + ".");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void checkOut() {
        try {
            List<Stay> occupied = hotel.listOccupiedRooms();
            if (occupied.isEmpty()) {
                System.out.println("No hay habitaciones ocupadas.");
                return;
            }
            printStays(occupied);

            int stayId = readInt("Id de la estadía: ");
            hotel.checkOut(stayId);
            System.out.println("Check-out realizado para la estadía " + stayId + ".");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // ------------------------------------------------------------------ rooms

    private static void roomsMenu() {
        System.out.println();
        System.out.println("--- Habitaciones ---");
        System.out.println("1. Habitaciones no disponibles");
        System.out.println("2. Cambiar estado de una habitación");
        System.out.println("3. Agregar habitación");
        System.out.println("4. Todas las habitaciones");
        System.out.println("0. Volver");
        int option = readInt("Opción: ");

        switch (option) {
            case 1:
                printRooms(hotel.listUnavailableRooms());
                break;
            case 2:
                changeRoomStatus();
                break;
            case 3:
                addRoom();
                break;
            case 4:
                printRooms(hotel.listRooms());
                break;
            case 0:
                break;
            default:
                System.out.println("Opción inválida.");
        }
    }

    private static void addRoom() {
        try {
            int number = readInt("Número de habitación: ");
            int floor = readInt("Piso: ");
            int capacity = readInt("Capacidad: ");
            RoomLevel level = readRoomLevel();

            Room room = new Room(number, floor, capacity, level);
            hotel.addRoom(room);
            System.out.println("Habitación " + number + " agregada con id " + room.getId() + ".");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void changeRoomStatus() {
        try {
            int roomId = readInt("Id de la habitación: ");
            RoomStatus status = readRoomStatus();
            String reason = "";
            if (status != RoomStatus.AVAILABLE) {
                reason = readText("Motivo: ");
            }

            hotel.changeRoomStatus(roomId, status, reason);
            System.out.println("Estado de la habitación cambiado a: " + label(status) + ".");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // ------------------------------------------------------------------ guests

    private static void guestsMenu() {
        System.out.println();
        System.out.println("--- Huéspedes ---");
        System.out.println("1. Registrar huésped");
        System.out.println("2. Informe de huésped");
        System.out.println("0. Volver");
        int option = readInt("Opción: ");

        switch (option) {
            case 1:
                registerGuest();
                break;
            case 2:
                guestReport();
                break;
            case 0:
                break;
            default:
                System.out.println("Opción inválida.");
        }
    }

    private static void registerGuest() {
        try {
            String firstName = readText("Nombre: ");
            String lastName = readText("Apellido: ");
            String dni = readText("DNI: ");
            String country = readText("País: ");
            String address = readText("Domicilio: ");

            Guest guest = new Guest(firstName, lastName, dni, country, address);

            hotel.registerGuest(guest);
            System.out.println("Huésped registrado con id " + guest.getId() + ".");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void guestReport() {
        try {
            Guest guest = hotel.findGuestByDni(readText("DNI: "));
            System.out.println(hotel.getGuestReport(guest.getId()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // ------------------------------------------------------------------ users

    private static void usersMenu() {
        System.out.println();
        System.out.println("--- Usuarios y permisos ---");
        System.out.println("1. Crear usuario");
        System.out.println("2. Desactivar usuario");
        System.out.println("3. Otorgar permiso");
        System.out.println("4. Quitar permiso");
        System.out.println("5. Listar usuarios");
        System.out.println("0. Volver");
        int option = readInt("Opción: ");

        switch (option) {
            case 1:
                createUser();
                break;
            case 2:
                deactivateUser();
                break;
            case 3:
                changePermission(true);
                break;
            case 4:
                changePermission(false);
                break;
            case 5:
                listUsers();
                break;
            case 0:
                break;
            default:
                System.out.println("Opción inválida.");
        }
    }

    private static void createUser() {
        try {
            System.out.println("1. Administrador");
            System.out.println("2. Recepcionista");
            int type = readInt("Tipo: ");
            if (type != 1 && type != 2) {
                System.out.println("Tipo inválido.");
                return;
            }

            String firstName = readText("Nombre: ");
            String lastName = readText("Apellido: ");
            String dni = readText("DNI: ");
            String username = readText("Usuario: ");
            String password = readText("Contraseña: ");

            User user;
            if (type == 1) {
                user = new Administrator(firstName, lastName, dni, username, password);
            } else {
                user = new Receptionist(firstName, lastName, dni, username, password);
            }

            hotel.createUser(user);
            System.out.println("Usuario " + username + " creado con id " + user.getId() + ".");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void listUsers() {
        try {
            for (User user : hotel.listUsers()) {
                String line = "Usuario " + user.getId()
                        + " - " + user.getUsername()
                        + " - " + user.getFullName()
                        + " - " + label(user.getType());
                if (!user.isActive()) {
                    line += " (inactivo)";
                }
                System.out.println(line);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void deactivateUser() {
        try {
            int userId = readInt("Id del usuario: ");
            hotel.deactivateUser(userId);
            System.out.println("Usuario " + userId + " desactivado.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Grants or revokes a permission, depending on the parameter.
     *
     * @param grant true to grant the permission, false to revoke it
     */
    private static void changePermission(boolean grant) {
        try {
            int userId = readInt("Id del usuario: ");
            Permission permission = readPermission();

            if (grant) {
                hotel.grantPermission(userId, permission);
                System.out.println("Permiso \"" + label(permission) + "\" otorgado al usuario " + userId + ".");
            } else {
                hotel.revokePermission(userId, permission);
                System.out.println("Permiso \"" + label(permission) + "\" quitado al usuario " + userId + ".");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // ------------------------------------------------------------------ backup

    private static void performBackup() {
        try {
            String folder = readText("Carpeta del backup (vacío para \"" + DEFAULT_BACKUP_FOLDER + "\"): ");
            if (folder.isBlank()) {
                folder = DEFAULT_BACKUP_FOLDER;
            }
            hotel.performBackup(folder);
            System.out.println("Backup guardado en la carpeta " + folder + ".");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // ------------------------------------------------------------------ output

    private static void printRooms(List<Room> rooms) {
        if (rooms.isEmpty()) {
            System.out.println("No se encontraron habitaciones.");
            return;
        }
        for (Room room : rooms) {
            String line = "Habitación " + room.getNumber()
                    + " (id " + room.getId() + ")"
                    + " - piso " + room.getFloor()
                    + " - " + room.getCapacity() + " huéspedes"
                    + " - " + label(room.getLevel())
                    + " - " + label(room.getStatus());
            if (room.getUnavailableReason() != null) {
                line += ": " + room.getUnavailableReason();
            }
            System.out.println(line);
        }
    }

    private static void printReservations(List<Reservation> reservations) {
        if (reservations.isEmpty()) {
            System.out.println("No se encontraron reservas.");
            return;
        }
        for (Reservation reservation : reservations) {
            System.out.println("Reserva " + reservation.getId()
                    + " - habitación " + reservation.getRoom().getNumber()
                    + " - " + reservation.getHolder().getFullName()
                    + " - del " + reservation.getStartDate() + " al " + reservation.getEndDate()
                    + " - " + reservation.getNumberOfGuests() + " huéspedes"
                    + " - " + label(reservation.getStatus()));
        }
    }

    private static void printStays(List<Stay> stays) {
        if (stays.isEmpty()) {
            System.out.println("No se encontraron estadías.");
            return;
        }
        for (Stay stay : stays) {
            String occupants = "";
            for (Guest guest : stay.getOccupants()) {
                if (!occupants.isEmpty()) {
                    occupants += ", ";
                }
                occupants += guest.getFullName();
            }
            System.out.println("Estadía " + stay.getId()
                    + " - habitación " + stay.getRoom().getNumber()
                    + " - desde el " + stay.getCheckInDate().toLocalDate()
                    + " - " + occupants);
        }
    }

    // ------------------------------------------------------------------ labels
    // The enums stay in English like the rest of the code. These methods return
    // the text shown to the user for each value.

    private static String label(RoomStatus status) {
        switch (status) {
            case AVAILABLE:
                return "Disponible";
            case CLEANING:
                return "En limpieza";
            case DISINFECTION:
                return "En desinfección";
            case REPAIR:
                return "En reparación";
            case CLOSED:
                return "Clausurada";
            default:
                return status.name();
        }
    }

    private static String label(RoomLevel level) {
        switch (level) {
            case STANDARD:
                return "Estándar";
            case SUPERIOR:
                return "Superior";
            case SUITE:
                return "Suite";
            case PRESIDENTIAL_SUITE:
                return "Suite presidencial";
            default:
                return level.name();
        }
    }

    private static String label(ReservationStatus status) {
        switch (status) {
            case ACTIVE:
                return "Activa";
            case IN_PROGRESS:
                return "En curso";
            case COMPLETED:
                return "Cumplida";
            case CANCELLED:
                return "Cancelada";
            default:
                return status.name();
        }
    }

    private static String label(Permission permission) {
        switch (permission) {
            case MANAGE_USERS:
                return "Gestionar usuarios";
            case PERFORM_BACKUP:
                return "Realizar backup";
            case MANAGE_RESERVATIONS:
                return "Gestionar reservas";
            case DO_CHECK_IN_OUT:
                return "Hacer check-in y check-out";
            case MANAGE_ROOMS:
                return "Gestionar habitaciones";
            default:
                return permission.name();
        }
    }

    private static String label(UserType type) {
        switch (type) {
            case ADMINISTRATOR:
                return "Administrador";
            case RECEPTIONIST:
                return "Recepcionista";
            default:
                return type.name();
        }
    }

    // ------------------------------------------------------------------ input

    private static String readText(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    /**
     * Reads a whole number, asking again until the input is valid.
     */
    private static int readInt(String message) {
        while (true) {
            String text = readText(message);
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                System.out.println("\"" + text + "\" no es un número. Probá de nuevo.");
            }
        }
    }

    /**
     * Reads a date in the format yyyy-mm-dd, asking again until the input is valid.
     */
    private static LocalDate readDate(String message) {
        while (true) {
            String text = readText(message);
            try {
                return LocalDate.parse(text);
            } catch (Exception e) {
                System.out.println("\"" + text + "\" no es una fecha válida. Usá el formato aaaa-mm-dd.");
            }
        }
    }

    /**
     * Reads a number between 1 and max, asking again until the input is valid.
     */
    private static int readChoice(String message, int max) {
        while (true) {
            int option = readInt(message);
            if (option >= 1 && option <= max) {
                return option;
            }
            System.out.println("Elegí un número entre 1 y " + max + ".");
        }
    }

    private static RoomStatus readRoomStatus() {
        RoomStatus[] values = RoomStatus.values();
        for (int i = 0; i < values.length; i++) {
            System.out.println((i + 1) + ". " + label(values[i]));
        }
        return values[readChoice("Nuevo estado: ", values.length) - 1];
    }

    private static RoomLevel readRoomLevel() {
        RoomLevel[] values = RoomLevel.values();
        for (int i = 0; i < values.length; i++) {
            System.out.println((i + 1) + ". " + label(values[i]));
        }
        return values[readChoice("Nivel: ", values.length) - 1];
    }

    private static Permission readPermission() {
        Permission[] values = Permission.values();
        for (int i = 0; i < values.length; i++) {
            System.out.println((i + 1) + ". " + label(values[i]));
        }
        return values[readChoice("Permiso: ", values.length) - 1];
    }
}
