import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public abstract class Room implements Comparable<Room> {
    private final int id;
    private static int counter = 1;
    private final int volume;
    private Person mainTenant;
    private LocalDate startDate;
    private LocalDate finishDate;
    private final String address;

    public Room(int volume, String address) {
        this.volume = volume;
        this.address = address;
        this.id = counter++;
        this.mainTenant = null;
        this.startDate = null;
        this.finishDate = null;
    }

    public int getVolume(){
        return volume;
    }
    public void setStartDate(LocalDate startDate){
        this.startDate = startDate;
    }
    public void setFinishDate(LocalDate finishDate){
        this.finishDate = finishDate;
    }
    public LocalDate getStartDate(){
        return startDate;
    }
    public LocalDate getFinishDate(){
        return finishDate;
    }
    public void setTenant(Person person){
        mainTenant = person;
    }
    public Person getTenant(){
        return mainTenant;
    }
    public String getAddress(){
        return address;
    }

    @Override
    public int compareTo(Room o){
        return this.volume - o.volume;
    }

    public int getId() {
        return id;
    }
    abstract int currentVolume();
    abstract void cancelRental();
    public void renewRental(){
        setFinishDate(getFinishDate().plusMonths(6));
        getTenant().getTenantLetterList().removeIf(n -> n.getRoom() == this);
    }
    public void renewRental(int months){
        setFinishDate(getFinishDate().plusMonths(months));
        getTenant().getTenantLetterList().removeIf(n -> n.getRoom() == this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return id == room.id && volume == room.volume && Objects.equals(mainTenant, room.mainTenant) && Objects.equals(startDate, room.startDate) && Objects.equals(finishDate, room.finishDate) && address.equals(room.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, volume, mainTenant, startDate, finishDate, address);
    }
}
