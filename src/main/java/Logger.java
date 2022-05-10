import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Logger {
    private static final Queue<String> messages = new LinkedList<>();

    private static Logger logger;

    private Logger() {

    }

    public static synchronized Logger getInstance() {
        if (logger == null) {
            logger = new Logger();
        }

        return logger;
    }

    public static void main(String[] args) {
        for (int index = 0; index < 10; ++index) {
            int finalIndex = index;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Logger logger = Logger.getInstance();

                    logger.log(String.valueOf(finalIndex));
                }
            });

            thread.start();
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Semaphore semaphore = new Semaphore(10);

        for (int index = 0; index < 10; ++index) {
            int finalIndex = index;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Logger logger = Logger.getInstance();

                    logger.writeToFile(semaphore);
                }
            });

            thread.start();
        }
    }

    synchronized public void log(String message) {
        System.out.println("Begin: " + message);
        messages.add(message);
        System.out.println("End: " + message);
    }

    public void writeToFile(Semaphore semaphore) {

        try {
            semaphore.acquire(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        synchronized (this) {
            System.out.println(messages.poll());
        }

        semaphore.release();
    }
}
