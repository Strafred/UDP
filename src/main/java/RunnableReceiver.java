import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class RunnableReceiver implements Runnable {
    private static final int BUFFER_SIZE = 100;
    private static String multicastIpAddress;
    private static int multicastPort;
    private static ConcurrentHashMap<String, Long> copies;

    RunnableReceiver(String multicastIpAddress, int multicastPort, ConcurrentHashMap<String, Long> copies) {
        RunnableReceiver.multicastIpAddress = multicastIpAddress;
        RunnableReceiver.multicastPort = multicastPort;
        RunnableReceiver.copies = copies;
    }

    private static void print(ConcurrentHashMap<String, Long> copies) {
        Iterator<String> iterator = copies.keySet().iterator();

        while (iterator.hasNext()) {
            String currentCopy = iterator.next();
            System.out.println(currentCopy);
        }
    }

    @Override
    public void run() {
        try {
            InetAddress group = InetAddress.getByName(multicastIpAddress);
            MulticastSocket mSocket = new MulticastSocket(multicastPort);
            mSocket.joinGroup(group);
            byte[] buffer = new byte[BUFFER_SIZE];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                mSocket.receive(packet);

                String processName = new String(packet.getData(), 0, packet.getLength());
                Date currentDate = new Date();
                Long receivingTime = currentDate.getTime();

                if (!copies.containsKey(processName)) {
                    copies.put(processName, receivingTime);
                    System.out.println(copies.size() + " copies running");
                    print(copies);
                } else {
                    copies.put(processName, receivingTime);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}