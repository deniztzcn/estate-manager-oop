public class TooManyRentException extends Exception {
    public TooManyRentException() {
        super();
    }

    @Override
    public void printStackTrace() {
        System.out.println("Too many rental place.");
    }
}
