public class Boat extends Vehicle{
    private String registeredPortCity;
    public Boat(int volume,String name,String vehicleType,String brand,String registeredPortCity) {
        super(volume,name,vehicleType,brand);
        this.registeredPortCity = registeredPortCity;
    }

    public String getRegisteredPortCity() {
        return registeredPortCity;
    }
}
