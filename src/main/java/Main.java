import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    private static final String multicastIpAddress = "225.6.7.8";
    private static final int multicastPort = 3456;
    private static final int threadSleepTime = 1000;
    private static final long timeout = 5000;

    private static void update(ConcurrentHashMap<String, Long> copies) {

        for (String s : copies.keySet()) {
            Date currentDate = new Date();
            Long currentTime = currentDate.getTime();

            String currentCopy = s;

            if (copies.get(currentCopy) + threadSleepTime + timeout < currentTime) {
                copies.remove(currentCopy);
                System.out.println(copies.size() + " copies running");
                print(copies);
            }
        }
    }

    private static void print(ConcurrentHashMap<String, Long> copies) {
        Iterator<String> iterator = copies.keySet().iterator();

        while (iterator.hasNext()) {
            String currentCopy = iterator.next();
            System.out.println(currentCopy);
        }
    }

    public static void main(String[] args) {
        String processName = ManagementFactory.getRuntimeMXBean().getName();
        Runnable sender = new RunnableSender(processName, multicastIpAddress, multicastPort, threadSleepTime);
        Thread sender_thread = new Thread(sender);

        ConcurrentHashMap<String, Long> copies = new ConcurrentHashMap<>();
        Runnable receiver = new RunnableReceiver(multicastIpAddress, multicastPort, copies);
        Thread receiver_thread = new Thread(receiver);

        sender_thread.start();
        receiver_thread.start();

        while (true) {
            update(copies);
        }
    }
}