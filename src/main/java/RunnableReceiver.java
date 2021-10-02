import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class RunnableReceiver implements Runnable {
    private final long TIMEOUT = 5000;
    private final int BUFFER_SIZE = 100;
    private final String CORRECT_FORMAT = "^[1-9]+[0-9]*@(.)*$";
    private final String multicastIpAddress;
    private final int multicastPort;
    private final int threadSleepTime;
    private final ConcurrentHashMap<String, Long> copies = new ConcurrentHashMap<>();

    public RunnableReceiver(String multicastIpAddress, int multicastPort, int threadSleepTime) {
        this.multicastIpAddress = multicastIpAddress;
        this.multicastPort = multicastPort;
        this.threadSleepTime = threadSleepTime;
    }

    private void update(ConcurrentHashMap<String, Long> copies) {
        for (String eachCopy : copies.keySet()) {
            Long currentTime = System.currentTimeMillis();

            if (copies.get(eachCopy) + threadSleepTime + TIMEOUT < currentTime) {
                copies.remove(eachCopy);
                System.out.println(copies.size() + " copies running");
                print(copies);
            }
        }
    }

    private static void print(ConcurrentHashMap<String, Long> copies) {
        copies.keySet().forEach(System.out::println);
    }

    @Override
    public void run() {
        try {
            InetAddress group = InetAddress.getByName(multicastIpAddress);
            if (!group.isMulticastAddress()) {
                return;
            }

            MulticastSocket mSocket = new MulticastSocket(multicastPort);
            mSocket.joinGroup(group);
            byte[] buffer = new byte[BUFFER_SIZE];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                mSocket.receive(packet);

                String processName = new String(packet.getData(), 0, packet.getLength());

                if (Pattern.matches(CORRECT_FORMAT, processName)) {
                    Long receivingTime = System.currentTimeMillis();

                    if (!copies.containsKey(processName)) {
                        copies.put(processName, receivingTime);
                        System.out.println(copies.size() + " copies running");
                        print(copies);
                    } else {
                        copies.put(processName, receivingTime);
                    }
                }
                this.update(copies);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}