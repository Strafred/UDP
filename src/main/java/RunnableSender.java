import java.lang.management.ManagementFactory;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class RunnableSender implements Runnable {
    private final String processName = ManagementFactory.getRuntimeMXBean().getName();
    private final String multicastIpAddress;
    private final int multicastPort;
    private final int threadSleepTime;

    public RunnableSender(String multicastIpAddress, int multicastPort, int threadSleepTime) {
        this.multicastIpAddress = multicastIpAddress;
        this.multicastPort = multicastPort;
        this.threadSleepTime = threadSleepTime;
    }

    @Override
    public void run() {
        try {
            InetAddress group = InetAddress.getByName(multicastIpAddress);
            if (!group.isMulticastAddress()) {
                return;
            }

            MulticastSocket socket = new MulticastSocket();
            DatagramPacket packet = new DatagramPacket(processName.getBytes(), processName.length(), group, multicastPort);

            while (true) {
                socket.send(packet);
                Thread.sleep(threadSleepTime);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}