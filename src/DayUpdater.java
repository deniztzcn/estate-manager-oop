import java.time.LocalDate;

public class DayUpdater extends Thread{
    public static LocalDate current = LocalDate.now();

    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(5000);
                up();
            } catch (InterruptedException e) {
                break;
            }
        }
    }
    public static synchronized void up(){
        current = current.plusDays(1);
    }
}
