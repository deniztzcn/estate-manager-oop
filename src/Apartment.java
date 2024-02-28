import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Apartment extends Room {
    private List<Person> tenantList;
    private List<Insertable> itemList;

    public Apartment(int volume,String address) {
        super(volume,address);
        this.tenantList = new ArrayList<>();
        this.itemList = new ArrayList<>();
    }


    public List<Person> getTenantList(){
        return this.tenantList;
    }

    @Override
    public String toString() {
        return "Apartment[" + super.getId() + "]";
    }
    @Override
    int currentVolume(){
        int sum = 0;
        for(Insertable item : itemList){
            sum += item.getVolume();
        }
        return sum;
    }
    public void insertItem(Insertable item) throws TooManyThingsException{
        if(currentVolume() + item.getVolume() <= getVolume()) {
            itemList.add(item);
        } else throw new TooManyThingsException();
    }
    public void addTenant(Person person){
        tenantList.add(person);
    }

    public List<Insertable> getItemList() {
        return itemList;
    }
    public boolean isLiveHere(int id){
        for(Person person : tenantList){
            if(person.getIdNumber() == id){
                return true;
            }
        }
        return false;
    }
    public void cancelRental(){
        getTenant().getTenantLetterList().removeIf(n -> n.getRoom() == this);
        this.setFinishDate(null);
        this.setTenant(null);
        this.setFinishDate(null);
        this.itemList.clear();
        this.getTenantList().clear();
        for(Person person : tenantList){
            person.removeRoomById(getId());
        }
    }

}
