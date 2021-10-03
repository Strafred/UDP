public class Main {
    private static final String MULTICAST_IP_ADDRESS = "225.6.7.8";
    private static final int MULTICAST_PORT = 3456;
    private static final int THREAD_SLEEP_TIME = 10000;

    public static void main(String[] args) {
        Runnable sender = new RunnableSender(MULTICAST_IP_ADDRESS, MULTICAST_PORT, THREAD_SLEEP_TIME);
        Thread senderThread = new Thread(sender);

        Runnable receiver = new RunnableReceiver(MULTICAST_IP_ADDRESS, MULTICAST_PORT, THREAD_SLEEP_TIME);
        Thread receiverThread = new Thread(receiver);

        senderThread.start();
        receiverThread.start();
    }
}