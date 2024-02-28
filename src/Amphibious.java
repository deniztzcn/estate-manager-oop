public class Amphibious extends Vehicle{
    private int passengerCapacity;

    public Amphibious(int volume,String name,String vehicleType,String brand,int passengerCapacity) {
        super(volume, name, vehicleType, brand);
        this.passengerCapacity = passengerCapacity;
    }

    public int getPassengerCapacity() {
        return passengerCapacity;
    }
}
