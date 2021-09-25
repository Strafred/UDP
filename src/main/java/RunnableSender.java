import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class RunnableSender implements Runnable {
    private static String processName;
    private static String multicastIpAddress;
    private static int multicastPort;
    private static int threadSleepTime;

    RunnableSender(String processName, String multicastIpAddress, int multicastPort, int threadSleepTime) {
        RunnableSender.processName = processName;
        RunnableSender.multicastIpAddress = multicastIpAddress;
        RunnableSender.multicastPort = multicastPort;
        RunnableSender.threadSleepTime = threadSleepTime;
    }

    @Override
    public void run() {
        try {
            InetAddress group = InetAddress.getByName(multicastIpAddress);
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