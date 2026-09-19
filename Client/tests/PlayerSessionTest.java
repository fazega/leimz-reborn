import com.client.network.PlayerSession;

public final class PlayerSessionTest {
    public static void main(String[] args) {
        String[] character = PlayerSession.parseCharacter("new;FaZeGa;Groz;barbare;7;8;h;");
        if (!character[0].equals("FaZeGa") || !character[3].equals("7"))
            throw new AssertionError("Existing character data changed");
        reject(null);
        reject("");
        reject("new;incomplete;");
        reject("new;A;Groz;barbare;7;8;h;new;B;Groz;barbare;7;8;h;");
        reject("new;A;Groz;barbare;bad;8;h;");
        System.out.println("PASS: single-character login protocol and malformed responses.");
    }

    private static void reject(String message) {
        try {
            PlayerSession.parseCharacter(message);
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("Accepted malformed character response");
    }
}
