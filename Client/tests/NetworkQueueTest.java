import com.client.network.NetworkManager;
import java.io.BufferedReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

/** Queued packets must be consumed without another blocking socket read. */
public final class NetworkQueueTest {
    private static Field field(String name) throws Exception {
        Field field = NetworkManager.class.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    public static void main(String[] args) throws Exception {
        try (ServerSocket server = new ServerSocket(0, 1, InetAddress.getLoopbackAddress())) {
            NetworkManager network =
                    new NetworkManager(InetAddress.getLoopbackAddress(), server.getLocalPort());
            try (Socket peer = server.accept()) {
                HashMap<String, ArrayList<String>> queue = new HashMap<>();
                ArrayList<String> packets = new ArrayList<>();
                packets.add("first");
                packets.add("second");
                queue.put("map", packets);
                field("message_recu_serveur").set(network, queue);
                field("br")
                        .set(
                                network,
                                new BufferedReader(new StringReader("")) {
                                    @Override
                                    public String readLine() {
                                        throw new AssertionError(
                                                "Read socket despite queued map data");
                                    }
                                });
                for (String expected : new String[] {"first", "second"}) {
                    network.waitForNewMessage("map");
                    if (!expected.equals(network.receiveFromServer("map"))) {
                        throw new AssertionError("Packet order changed");
                    }
                }
                if (queue.containsKey("map")) throw new AssertionError("Queue not drained");
                System.out.println(
                        "PASS: queued loading packets consumed immediately and in order.");
            } finally {
                ((Timer) field("t").get(network)).cancel();
                ((Socket) field("s").get(network)).close();
            }
        }
    }
}
