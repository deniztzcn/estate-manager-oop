public class OffRoadCar extends Vehicle{
    private boolean isFourWheelDrive;
    public OffRoadCar(int volume, String name, String vehicleType, String brand,boolean isFourWheelDrive) {
        super(volume, name, vehicleType, brand);
        this.isFourWheelDrive = isFourWheelDrive;
    }
}
