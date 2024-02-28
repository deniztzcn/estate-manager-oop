import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Person {
    private final String name;
    private final String surname;
    private String address;
    private final int idNumber;
    private static int counter= 1;
    private List<Room> rentedPlaces;
    private final int max = 5;
    private List<TenantLetter> tenantLetterList;

    public Person(String name, String surname) {
        this.name = name;
        this.surname = surname;
        this.address = null;
        this.idNumber = counter++;
        rentedPlaces = new ArrayList<>();
        this.tenantLetterList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = rentedPlaces.get(0).getAddress();
    }

    public int getIdNumber() {
        return idNumber;
    }

    public void addRentedPlace(Room place){
        rentedPlaces.add(place);
    }
    public void rent(Room place, LocalDate startDate,LocalDate finishDate) throws TooManyRentException,ProblematicTenantException{
        List<Room> roomHasLetter = tenantLetterList.stream().map(TenantLetter::getRoom).toList();
        if(tenantLetterList.size() >= 3){
            throw new ProblematicTenantException("Person " + name + " had already renting rooms: " + roomHasLetter);
        }
        if(rentedPlaces.size() + 1 <= max){
            rentedPlaces.add(place);
        } else
            throw new TooManyRentException();
        if(place.getTenant() == null) {
            place.setStartDate(startDate);
            place.setFinishDate(finishDate);
            if(place instanceof Apartment) {
                ((Apartment) place).addTenant(this);
                place.setTenant(this);
            } else{
                place.setTenant(this);
            }
        } else if(place instanceof Apartment) {
            ((Apartment) place).addTenant(this);
        }
    }
    public void removeRoomById(int id){
        rentedPlaces.removeIf(n -> n.getId() == id);
    }

    public List<Room> getRentedPlaces() {
        return rentedPlaces;
    }

    @Override
    public String toString() {
        return name + " " + surname +"[" + idNumber + "]";
    }

    public void addTenantLetter(Room room){
        for(TenantLetter tl : tenantLetterList){
            if(tl.getRoom() == room){
                return;
            }
        }
        tenantLetterList.add(new TenantLetter(this,room));
    }

    public List<TenantLetter> getTenantLetterList() {
        return tenantLetterList;
    }

    public boolean hasApartment(){
        for(Room room : rentedPlaces){
            if(room instanceof Apartment){
                return true;
            }
        }
        return false;
    }
    public boolean hasParkingPlace(){
        for(Room room : rentedPlaces){
            if(room instanceof ParkingPlace){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return idNumber == person.idNumber && Objects.equals(name, person.name) && Objects.equals(surname, person.surname) && Objects.equals(address, person.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, address, idNumber);
    }
}
