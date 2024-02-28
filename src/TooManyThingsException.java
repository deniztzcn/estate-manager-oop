public class TooManyThingsException extends Exception{
    public TooManyThingsException() {
        super();
    }

    @Override
    public void printStackTrace() {
        System.out.println("Remove some old items to insert a new item");
    }
}
