import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static String monitor = new String();
    static List<Room> roomList = new ArrayList<>();
    static List<Person> personList = new ArrayList<>();

    public static void main(String[] args) {
        try {
            FileWriter fw = new FileWriter("data.txt");
            fw.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        DayUpdater updater = new DayUpdater();
        updater.start();

        Thread rentalIssuesThread = new Thread(() -> {
            while (true) {
                rentalIssues();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        rentalIssuesThread.start();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            menu();
            int operation = scanner.nextInt();
            switch (operation) {
                case 1:
                    String[] possibleNames = {"Wilda", "Randall", "Godfrey", "Leonard", "Maxwell"};
                    String[] possibleSurnames = {"Broks", "Chapman", "Anderson", "Vasquez", "Feron"};
                    Random random = new Random();

                    for (int i = 0; i < 5; i++) {
                        personList.add(new Person(possibleNames[i], possibleSurnames[i]));
                    }
                    for (int i = 0; i < 5; i++) {
                        roomList.add(new Apartment(random.nextInt(50) + 50, "Address" + i));
                        roomList.add(new ParkingPlace(random.nextInt(50) + 50, "Address" + i));
                    }
                    for (int i = 0; i < 10; i++) {
                        try {
                            while (true) {
                                Person person = personList.get(random.nextInt(personList.size()));
                                Room place = roomList.get(random.nextInt(roomList.size()));
                                if (person.getRentedPlaces().contains(place)) {
                                    continue;
                                }
                                if (place instanceof Apartment && person.getRentedPlaces().isEmpty()) {
                                    person.rent(place, LocalDate.now(), LocalDate.now().plusDays(random.nextInt(5) + 10));
                                    person.setAddress(place.getAddress());
                                } else if (place instanceof ParkingPlace && person.getRentedPlaces().isEmpty()) {
                                    Room tempPlace = roomList.stream()
                                            .filter(n -> n instanceof Apartment && n.getAddress().equals(place.getAddress()))
                                            .findFirst()
                                            .orElse(null);
                                    person.rent(tempPlace, LocalDate.now(), LocalDate.now().plusDays(random.nextInt(5) + 10));
                                    person.setAddress(tempPlace.getAddress());
                                } else if (place instanceof ParkingPlace && ((ParkingPlace) place).getTenant() == null) {
                                    person.rent(place, LocalDate.now(), LocalDate.now().plusDays(random.nextInt(5) + 10));
                                } else if (place instanceof Apartment) {
                                    person.rent(place, LocalDate.now(), LocalDate.now().plusDays(random.nextInt(5) + 10));
                                }
                                break;
                            }
                        } catch (TooManyRentException | ProblematicTenantException e) {
                            e.printStackTrace();
                        }
                    }
                    List<Room> emptyRooms = roomList.stream().filter(n -> n.getTenant() == null).toList();
                    List<Person> emptyPersons = personList.stream().filter(n -> n.getRentedPlaces().isEmpty()).toList();
                    for(Room room : emptyRooms){
                        for(Person person : emptyPersons){
                            try {
                                person.rent(room,LocalDate.now(),LocalDate.now().plusDays(random.nextInt(5)+10));
                            } catch (TooManyRentException | ProblematicTenantException e) {
                                continue;
                            }
                        }
                    }

                    for (Room room : roomList) {
                        if (room instanceof Apartment) {
                            if (room.getTenant() == null) {
                                continue;
                            }
                            for (int i = 0; i < 2; i++) {
                                try {
                                    if (room.getTenant() != null) {
                                        ((Apartment) room).insertItem(createRandomItem(room));
                                    }
                                } catch (TooManyThingsException e) {
                                    e.printStackTrace();
                                    System.out.println(room);
                                }
                            }
                        } else {
                            if (room.getTenant() == null) {
                                continue;
                            }
                            for (int i = 0; i < 2; i++) {
                                try {
                                    if (room.getTenant() != null) {
                                        ((ParkingPlace) room).insert(createRandomInsertable((ParkingPlace) room));
                                    }
                                } catch (TooManyThingsException e) {
                                    e.printStackTrace();
                                    System.out.println(room);
                                }
                            }
                        }
                    }
                    break;
                case 2:
                    for (Person person : personList) {
                        System.out.println(person + " -> " + person.getRentedPlaces());
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    for (Room room : roomList) {
                        if (room instanceof ParkingPlace) {
                            System.out.println(room + "[" + room.getTenant() + "]" + " -> " + ((ParkingPlace) room).getInsertableList());
                        }
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    updater.interrupt();
                    rentalIssuesThread.interrupt();
                    List<Apartment> apartmentList = roomList.stream()
                            .filter(n -> n instanceof Apartment)
                            .map(n -> (Apartment) n).toList();

                    for (Apartment apartment : apartmentList) {
                        System.out.println(apartment + " -> " + apartment.getTenantList());
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    rentalIssuesThread = new Thread(() -> {
                        while (true) {
                            rentalIssues();
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    });
                    updater = new DayUpdater();
                    updater.start();
                    rentalIssuesThread.start();
                    break;
                case 5:
                    updater.interrupt();
                    rentalIssuesThread.interrupt();
                    System.out.println("Please choose what kind of room you are looking for:");
                    System.out.println("1-Apartment\n2-Parking Place");
                    System.out.print("-> ");
                    operation = scanner.nextInt();

                    if (operation == 1) {
                        showApartments();
                        System.out.println("Please enter an ID from given list");
                        System.out.print("-> ");
                        operation = scanner.nextInt();
                        showItemsForApartment(operation);
                    } else if (operation == 2) {
                        showParkingPlaces();
                        System.out.println("Please enter an ID from given list");
                        System.out.print("-> ");
                        operation = scanner.nextInt();
                        showItemsForParkingPlace(operation);
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updater = new DayUpdater();
                    rentalIssuesThread = new Thread(() -> {
                        while (true) {
                            rentalIssues();
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    });
                    updater.start();
                    rentalIssuesThread.start();
                    break;
                case 6: {
                    updater.interrupt();
                    rentalIssuesThread.interrupt();
                    System.out.println(personList);
                    System.out.println("Please choose a person ID from given list");
                    System.out.print("-> ");
                    operation = scanner.nextInt();
                    int personId = operation;
                    Person tmp = getPerson(personId);
                    System.out.println(tmp.getRentedPlaces());

                    System.out.println("1-Apartment\n2-Parking Place");
                    System.out.println("Please choose whether you want to add in an Apartment or Parking Place");
                    System.out.print("-> ");
                    operation = scanner.nextInt();

                    if (operation == 1) {
                        if (tmp.hasApartment()) {
                            showRentedApartments(personId);
                            System.out.println("Please enter Apartment ID");
                            System.out.print("-> ");
                            int aptId = scanner.nextInt();
                            showHowMuchSpaceLeft(getRoom(aptId));
                            System.out.println("Please enter item name and volume");
                            System.out.print("name: ");
                            String name = scanner.nextLine();
                            scanner.nextLine();
                            System.out.print("volume: ");
                            int volume = scanner.nextInt();
                            try {
                                ((Apartment) getRoom(aptId)).insertItem(new Item(volume, name));
                            } catch (TooManyThingsException e) {
                                e.printStackTrace();
                            }
                        } else System.out.println("This person doesn't have apartment.");
                    } else if (operation == 2) {
                        if (tmp.hasParkingPlace()) {
                            showRentedParkingPlaces(personId);
                            System.out.println("Please enter Parking Place ID");
                            int ppId = scanner.nextInt();
                            showHowMuchSpaceLeft(getRoom(ppId));
                            System.out.println("1-Item\n2-Vehicle");
                            System.out.println("Please choose an operation to insert an item or vehicle");
                            System.out.print("-> ");
                            int operation2 = scanner.nextInt();
                            if (operation2 == 1) {
                                insertItemToParkingPlace(ppId, scanner);
                            } else if (operation2 == 2) {
                                insertVehicle(ppId, scanner);
                            }
                        }
                    }
                    updater = new DayUpdater();
                    rentalIssuesThread = new Thread(() -> {
                        while (true) {
                            rentalIssues();
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    });
                    updater.start();
                    rentalIssuesThread.start();
                }
                break;
                case 7: {
                    updater.interrupt();
                    rentalIssuesThread.interrupt();
                    System.out.println("Current date: " + currentDate());
                    System.out.println("1-Rent for existing person\n2-Create a person and rent for him/her");
                    System.out.println("Please choose an operation to rent");
                    System.out.print("-> ");
                    operation = scanner.nextInt();
                    if (operation == 1) {
                        showPersons();
                        System.out.println("Please choose a person by ID from given list");
                        System.out.print("-> ");
                        int id = scanner.nextInt();
                        Person tmp = getPerson(id);

                        System.out.println("1-Apartment\n2-Parking Place");
                        System.out.println("Please choose one of the above options");
                        System.out.print("-> ");
                        int operation2 = scanner.nextInt();

                        if (operation2 == 1) {
                            rentApartment(tmp, scanner);
                        } else if (operation2 == 2) {
                            rentParkingPlace(tmp, scanner);
                        }
                    } else if (operation == 2) {
                        Person newPerson = createPerson(scanner);

                        System.out.println("1-Apartment\n2-Parking Place");
                        System.out.println("Please choose one of the above options");
                        System.out.print("-> ");
                        int operation2 = scanner.nextInt();

                        if (operation2 == 1) {
                            rentApartment(newPerson, scanner);
                        } else if (operation2 == 2) {
                            rentParkingPlace(newPerson, scanner);
                        }
                    }
                    updater = new DayUpdater();
                    rentalIssuesThread = new Thread(() -> {
                        while (true) {
                            rentalIssues();
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    });
                    updater.start();
                    rentalIssuesThread.start();
                }
                break;
                case 8:
                    updater.interrupt();
                    rentalIssuesThread.interrupt();
                    createApartment(scanner);
                    updater = new DayUpdater();
                    rentalIssuesThread = new Thread(() -> {
                        while (true) {
                            rentalIssues();
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    });
                    updater.start();
                    rentalIssuesThread.start();
                    break;
                case 9:
                    updater.interrupt();
                    rentalIssuesThread.interrupt();
                    createParkingPlace(scanner);
                    updater = new DayUpdater();
                    rentalIssuesThread = new Thread(() -> {
                        while (true) {
                            rentalIssues();
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    });
                    updater.start();
                    rentalIssuesThread.start();
                    break;
                case 10:
                    System.out.println(currentDate());
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 11:
                    updater.interrupt();
                    rentalIssuesThread.interrupt();
                    System.out.println("Please choose what kind of room you are looking for:");
                    System.out.println("1-Apartment\n2-Parking Place");
                    System.out.print("-> ");
                    operation = scanner.nextInt();

                    if (operation == 1) {
                        showApartments();
                        System.out.println("Please enter an ID from given list");
                        System.out.print("-> ");
                        int id = scanner.nextInt();
                        showRentalInfo(id);

                    } else if (operation == 2) {
                        showParkingPlaces();
                        System.out.println("Please enter an ID from given list");
                        System.out.print("-> ");
                        int id = scanner.nextInt();
                        showRentalInfo(id);
                    }
                    updater = new DayUpdater();
                    rentalIssuesThread = new Thread(() -> {
                        while (true) {
                            rentalIssues();
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    });
                    updater.start();
                    rentalIssuesThread.start();
                    break;
                case 12:
                    updater.interrupt();
                    rentalIssuesThread.interrupt();
                    showTenantLetters();
                    System.out.println("Please choose a room id to update rental agreement");
                    System.out.print("-> ");
                    int id = scanner.nextInt();
                    System.out.println("1-Renew agreement(by default 6 months)\n" +
                            "2-Renew agreement by entering the number of months\n" +
                            "3-Cancel agreement");
                    System.out.println("Please choose operation for this agreement");
                    System.out.print("-> ");
                    operation = scanner.nextInt();
                    Room tmpRoom = getRoom(id);
                    Person tmpPerson = tmpRoom.getTenant();
                    synchronized (monitor) {
                        try {
                            PrintWriter pw = new PrintWriter(new FileWriter("data.txt", true),true);
                            if (operation == 1) {
                                tmpRoom.renewRental();
                                pw.println(tmpPerson + " renewed the agreement for " + tmpRoom);
                            } else if (operation == 2) {
                                System.out.print("Please input month: ");
                                int month = scanner.nextInt();
                                tmpRoom.renewRental(month);
                                pw.println(tmpPerson + " renewed the agreement for " + tmpRoom);
                            } else if (operation == 3) {
                                tmpRoom.cancelRental();
                                pw.println(tmpPerson + " cancelled the agreement for " + tmpRoom);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    updater = new DayUpdater();
                    rentalIssuesThread = new Thread(() -> {
                        while (true) {
                            rentalIssues();
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    });
                    updater.start();
                    rentalIssuesThread.start();
                    break;
                case 13:
                    showRoomsAndResponsiblePerson();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 14:
                    showTenantLetters();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 15:
                    updater.interrupt();
                    rentalIssuesThread.interrupt();

                    System.out.println("1-Apartment\n2-Parking Place");
                    System.out.println("Please choose operation");
                    System.out.print("-> ");
                    operation = scanner.nextInt();
                    scanner.nextLine();
                    if(operation == 1){
                        showApartments();
                        System.out.println("Please enter apartment id");
                        System.out.print("-> ");
                        id = scanner.nextInt();
                        Room tmp = getRoom(id);
                        showItemsVolume(tmp);
                        System.out.println("Please enter item id");
                        System.out.print("-> ");
                        int id2 = scanner.nextInt();
                        removeItem(tmp,id2);
                    } else if(operation == 2){
                        showParkingPlaces();
                        System.out.println("Please enter parking place id");
                        System.out.print("-> ");
                        id = scanner.nextInt();
                        scanner.nextLine();
                        Room tmp = getRoom(id);
                        showItemsVolume(tmp);
                        System.out.println("Please enter item id");
                        System.out.print("-> ");
                        int id2 = scanner.nextInt();
                        removeItem(tmp,id2);
                    }
                    updater = new DayUpdater();
                    rentalIssuesThread = new Thread(() -> {
                        while (true) {
                            rentalIssues();
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    });
                    updater.start();
                    rentalIssuesThread.start();
                    break;
            }
        }
    }

    public static void menu() {
        System.out.println("1-Start the program.\t\t\t\t\t\t\t\t2-Show the people and their rented places");
        System.out.println("3-Show the parking places and its items.\t\t\t4-Show the apartments and its tenants");
        System.out.println("5-Show the items for a specific room\t\t\t\t6-Insert an item or vehicle for a specific room");
        System.out.println("7-Rent a place for a person\t\t\t\t\t\t\t8-Create an Apartment");
        System.out.println("9-Create a Parking Place\t\t\t\t\t\t\t10-Show current date");
        System.out.println("11-Show rental information about a specific room\t12-Renew or cancel rent");
        System.out.println("13-Show rooms and its responsible tenat\t\t\t\t14-Show tenant letters");
        System.out.println("15-Remove item from a room");
        System.out.println("Please choose an operation:");
        System.out.print("-> ");
    }

    public static Item createRandomItem(Room rentable) {
        Random random = new Random();
        ItemNames[] itemNames = ItemNames.values();
        String[] items = new String[itemNames.length];
        for (int i = 0; i < itemNames.length; i++) {
            items[i] = itemNames[i].toString();
        }
        return new Item(random.nextInt(rentable.getVolume() / 10) + 5, items[random.nextInt(items.length)]);
    }

    public static Insertable createRandomInsertable(ParkingPlace parkingPlace) {
        Random random = new Random();

        int volume = random.nextInt(parkingPlace.getVolume() / 10) + 10;
        int rand = random.nextInt(4);
        switch (rand) {
            case 0:
                CityCarBrands[] cityCarBrands = CityCarBrands.values();
                String brand = cityCarBrands[random.nextInt(cityCarBrands.length)].toString();
                double engineCapacity = random.nextDouble();
                return new CityCar(volume, "cityCar", "Car", brand, engineCapacity);
            case 1:
                MotorcycleBrands[] motorcycleBrands = MotorcycleBrands.values();
                brand = motorcycleBrands[random.nextInt(motorcycleBrands.length)].toString();
                int tankCapacity = random.nextInt(5) + 5;
                return new Motorcycle(volume, "motorcycle", "Motorcycle", brand, tankCapacity);
            case 2:
                BoatBrands[] boatBrands = BoatBrands.values();
                PortNames[] portNames = PortNames.values();
                brand = boatBrands[random.nextInt(boatBrands.length)].toString();
                String portName = portNames[random.nextInt(portNames.length)].toString();
                return new Boat(volume, "boat", "Boat", brand, portName);
            case 3:
                AmphibiousBrands[] amphibiousBrands = AmphibiousBrands.values();
                brand = amphibiousBrands[random.nextInt(amphibiousBrands.length)].toString();
                int capacity = random.nextInt(7) + 2;
                return new Amphibious(volume, "boat", "Amphibious", brand, capacity);
            default:
                return createRandomItem(parkingPlace);
        }
    }

    public static LocalDate currentDate() {
        return DayUpdater.current;
    }

    public static void showItemsForApartment(int id) {
        System.out.println(((Apartment)getRoom(id)).getItemList());
    }

    public static void showApartments() {
        List<Apartment> aptList = roomList.stream()
                .filter(n -> n instanceof Apartment)
                .map(n -> (Apartment) n)
                .toList();
        System.out.println(aptList);
    }

    public static void showItemsForParkingPlace(int id) {

        System.out.println(((ParkingPlace)getRoom(id)).getInsertableList());
    }

    public static void showParkingPlaces() {
        List<ParkingPlace> ppList = roomList.stream()
                .filter(n -> n instanceof ParkingPlace)
                .map(n -> (ParkingPlace) n)
                .toList();
        System.out.println(ppList);
    }

    public static void showRentedApartments(int id) {
        List<Apartment> apartments = getPerson(id).getRentedPlaces().stream()
                        .filter(n -> n instanceof Apartment)
                        .map(n -> (Apartment)n)
                        .collect(Collectors.toList());
        System.out.println(apartments);
    }

    public static void showRentedParkingPlaces(int id) {
        List<ParkingPlace> parkingPlaces = getPerson(id).getRentedPlaces().stream()
                .filter(n -> n instanceof ParkingPlace)
                .map(n -> (ParkingPlace) n)
                .collect(Collectors.toList());
        System.out.println(parkingPlaces);
    }

    public static Person getPerson(int id) {
        return personList.stream().
                filter(n -> n.getIdNumber() == id)
                .findFirst()
                .orElse(null);
    }

    public static Room getRoom(int id) {
        return roomList.stream().filter(n -> n.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public static void insertVehicle(int ppId, Scanner scanner) {
        System.out.println("1-Amphibious\t2-Boat");
        System.out.println("3-City Car\t4-Motorcycle");
        System.out.println("5-Off-Road Car");
        System.out.println("Please choose vehicle kind");
        System.out.print("-> ");
        int operation3 = scanner.nextInt();
        scanner.nextLine();

        System.out.print("volume: ");
        int volume = scanner.nextInt();
        scanner.nextLine();
        System.out.print("name: ");
        String name = scanner.nextLine();
        System.out.print("vehicle type: ");
        String vehicleType = scanner.nextLine();
        System.out.print("brand: ");
        String brand = scanner.nextLine();


        if (operation3 == 1) {
            System.out.print("passenger capacity: ");
            int capacity = scanner.nextInt();
            try {
                ((ParkingPlace) getRoom(ppId)).insert(new Amphibious(volume, name, vehicleType, brand, capacity));
            } catch (TooManyThingsException e) {
                e.printStackTrace();
            }
        }
        if (operation3 == 2) {
            System.out.print("port name: ");
            String registeredPort = scanner.nextLine();
            try {
                ((ParkingPlace) getRoom(ppId)).insert(new Boat(volume, name, vehicleType, brand, registeredPort));
            } catch (TooManyThingsException e) {
                e.printStackTrace();
            }
        }
        if (operation3 == 3) {
            System.out.print("engine capacity: ");
            double engineCapacity = scanner.nextDouble();
            try {
                ((ParkingPlace) getRoom(ppId)).insert(new CityCar(volume, name, vehicleType, brand, engineCapacity));
            } catch (TooManyThingsException e) {
                e.printStackTrace();
            }
        }
        if (operation3 == 4) {
            System.out.print("tank capacity: ");
            int tankCapacity = scanner.nextInt();
            try {
                ((ParkingPlace) getRoom(ppId)).insert(new Motorcycle(volume, name, vehicleType, brand, tankCapacity));
            } catch (TooManyThingsException e) {
                e.printStackTrace();
            }
        }
        if (operation3 == 5) {
            System.out.print("Is this car has 4 wheel drive? (yes or no)");
            String answer = scanner.nextLine();
            boolean bool = false;
            if (answer.equalsIgnoreCase("yes")) {
                bool = true;
            } else if (answer.equalsIgnoreCase("no")) {
                bool = false;
            }
            try {
                ((ParkingPlace) getRoom(ppId)).insert(new OffRoadCar(volume, name, vehicleType, brand, bool));
            } catch (TooManyThingsException e) {
                e.printStackTrace();
            }
        }
    }

    public static void insertItemToParkingPlace(int ppId, Scanner scanner) {
        System.out.println("Please enter the name and the volume");
        System.out.print("volume: ");
        int volume = scanner.nextInt();
        scanner.nextLine();
        System.out.print("name: ");
        String name = scanner.nextLine();
        try {
            ((ParkingPlace) getRoom(ppId)).insert(new Item(volume, name));
        } catch (TooManyThingsException e) {
            e.printStackTrace();
        }
    }

    public static void createApartment(Scanner scanner) {
        System.out.print("volume: ");
        int volume = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Address: ");
        String address = scanner.nextLine();

        Apartment apartment = new Apartment(volume, address);
        roomList.add(apartment);
    }

    public static void createParkingPlace(Scanner scanner) {
        System.out.print("volume: ");
        int volume = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Address: ");
        String address = scanner.nextLine();

        ParkingPlace ppl = new ParkingPlace(volume, address);
        roomList.add(ppl);
    }

    public static Person createPerson(Scanner scanner) {
        System.out.print("Please enter a name: ");
        String name = scanner.nextLine();
        scanner.nextLine();
        System.out.print("Please enter a surname: ");
        String surname = scanner.nextLine();
        return new Person(name, surname);
    }

    public static void showPersons() {
        System.out.println(personList);
    }

    public static LocalDate createDate(Scanner scanner) {
        System.out.println("year: ");
        int year = scanner.nextInt();
        System.out.println("month: ");
        int month = scanner.nextInt();
        System.out.println("day: ");
        int day = scanner.nextInt();
        return LocalDate.of(year, month, day);
    }

    public static void rentApartment(Person person, Scanner scanner) {
        showApartments();
        System.out.print("Please choose an apartment by ID: ");
        int id = scanner.nextInt();
        if(!roomList.contains(getRoom(id))){
            System.out.println("Given id doesnt exist");
            return;
        }
        System.out.println("Please enter rental start date: ");
        LocalDate rentalStart = createDate(scanner);
        System.out.println("Please enter rental finish date: ");
        LocalDate rentalFinish = createDate(scanner);
        if (!rentalStart.isBefore(rentalFinish)) {
            System.out.println("Rental finish date can not be before the rental start date!!");
            return;
        }
        try {
            person.rent(getRoom(id), rentalStart, rentalFinish);
        } catch (TooManyRentException | ProblematicTenantException e) {
            System.out.println(e);;
        }
    }

    public static void rentParkingPlace(Person person, Scanner scanner) {
        showParkingPlaces();
        System.out.println("Please choose a Parking Place by ID");
        int id = scanner.nextInt();
        if (getRoom(id).getTenant() != null) {
            return;
        }
        System.out.println("Please enter rental start date");
        LocalDate rentalStart = createDate(scanner);
        System.out.println("Please enter rental finish date");
        LocalDate rentalFinish = createDate(scanner);

        try {
            person.rent(getRoom(id), rentalStart, rentalFinish);
        } catch (TooManyRentException | ProblematicTenantException e) {
            e.printStackTrace();
        }
    }

    public static long daysPassedAfterFinishDate(Room room) {
        return ChronoUnit.DAYS.between(room.getFinishDate(), currentDate());
    }

    public static boolean isFinishDatePassed(Room room) {
        return room.getFinishDate().isBefore(currentDate());
    }

    public static synchronized void rentalIssues() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("data.txt", true))) {

            for (Room room : roomList) {
                Person tmpPerson = room.getTenant();
                if (room.getTenant() != null && isFinishDatePassed(room) && (daysPassedAfterFinishDate(room) <= 30)) {
                    room.getTenant().addTenantLetter(room);
                    pw.println(tmpPerson +
                            " got tenant letter for " + room
                            + " | " + (30 - daysPassedAfterFinishDate(room)) + " days left to decide.");
                } else if (room.getTenant() != null && isFinishDatePassed(room) && (daysPassedAfterFinishDate(room) > 30)) {
                    pw.println(tmpPerson + " did not renew or cancel the agreement for " + room);
                    for (Person person : personList) {
                        person.removeRoomById(room.getId());
                    }
                    room.setFinishDate(null);
                    room.setFinishDate(null);
                    room.setTenant(null);
                    if(room instanceof Apartment){
                        ((Apartment) room).getTenantList().clear();
                        ((Apartment) room).getItemList().clear();
                    } else if(room instanceof ParkingPlace){
                        ((ParkingPlace) room).getInsertableList().clear();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showRentalInfo(int id) {
        Room tmp = roomList.stream().filter(n -> n.getId() == id)
                .findFirst().orElse(null);
        System.out.println(tmp + " | " + "start date: " + tmp.getStartDate() + " " + "finish date: " + tmp.getFinishDate());
        System.out.println("Responsible person: " + tmp.getTenant());
    }

    public static void showRoomsAndResponsiblePerson() {
        for (Room room : roomList) {
            System.out.println(room + " ->  " + room.getTenant());
        }
    }
    public static void showTenantLetters(){
        Map<Person, List<Room>> tenantLetterMap = new HashMap<>();
        List<Person> personsHaveLetter = personList.stream()
                .filter(n -> n.getTenantLetterList() != null)
                .toList();

        for (Person person : personsHaveLetter) {
            for (TenantLetter letter : person.getTenantLetterList()) {
                tenantLetterMap.computeIfAbsent(person, n -> new ArrayList<>()).add(letter.getRoom());
            }
        }

        for(Map.Entry<Person,List<Room>> entry : tenantLetterMap.entrySet()){
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
    public static void removeItem(Room room, int id){
        if(room instanceof Apartment){
            ((Apartment) room).getItemList().removeIf(n -> n.getId() == id);
        } else if(room instanceof ParkingPlace){
            ((ParkingPlace) room).getInsertableList().removeIf(n -> n.getId() == id);
        }
    }

    public static void showHowMuchSpaceLeft(Room room){
        System.out.println("space left: " + (room.getVolume() - room.currentVolume()));
    }
    public static void showItemsVolume(Room room){
        if(room instanceof Apartment){
            for(Insertable item : ((Apartment) room).getItemList()){
                System.out.println(item + " -> " + item.getVolume());
            }
        } else if(room instanceof ParkingPlace){
            for(Insertable insertable : ((ParkingPlace) room).getInsertableList()){
                System.out.println(insertable + " -> " + insertable.getVolume());
            }
        }
    }
}
