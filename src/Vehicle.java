public abstract class Vehicle extends Insertable {
    private String vehicleType;
    private String brand;

    public Vehicle(int volume,String name,String vehicleType,String brand) {
        super(volume,name);
        this.vehicleType = vehicleType;
        this.brand = brand;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public String getBrand() {
        return brand;
    }


}
