import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ParkingPlace extends Room{
    private List<Insertable> insertableList;

    public ParkingPlace(int volume,String address) {
        super(volume,address);
        this.insertableList = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Parking Place[" + getId()+ "]";
    }
    @Override
    int currentVolume(){
        int sum = 0;
        for(Insertable insertable : insertableList){
            sum += insertable.getVolume();
        }
        return sum;
    }

    public void insert(Insertable insertable) throws TooManyThingsException{
        if(insertable.getVolume() + currentVolume() <= getVolume()){
            insertableList.add(insertable);
        } else throw new TooManyThingsException();
    }

    public List<Insertable> getInsertableList() {
        return insertableList;
    }

    @Override
    void cancelRental() {
        this.setFinishDate(null);
        this.setStartDate(null);
        this.setTenant(null);
        insertableList.clear();
        getTenant().removeRoomById(getId());
        getTenant().getTenantLetterList().removeIf(n -> n.getRoom() == this);
    }
}
