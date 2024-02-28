public class CityCar extends Vehicle{
    private double engineCapacity;

    public CityCar(int volume,String name,String vehicleType,String brand,double engineCapacity) {
        super(volume,name,vehicleType,brand);
        this.engineCapacity = engineCapacity;
    }

    public double getEngineCapacity() {
        return engineCapacity;
    }
}
