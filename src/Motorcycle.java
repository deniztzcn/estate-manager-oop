public class Motorcycle extends Vehicle{
    private int tankCapacity;

    public Motorcycle(int volume,String name,String vehicleType,String brand,int tankCapacity) {
        super(volume, name, vehicleType, brand);
        this.tankCapacity = tankCapacity;
    }

    public int getTankCapacity() {
        return tankCapacity;
    }
}
