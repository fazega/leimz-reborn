import com.server.db.AccountCharacter;
import java.sql.*;

/** Runs in a rolled-back transaction against the local recovered database. */
public final class AccountCharacterTest {
    public static void main(String[] args) throws Exception {
        try (Connection db =
                DriverManager.getConnection(
                        System.getProperty("leimz.db.url"),
                        System.getProperty("leimz.db.user"),
                        System.getProperty("leimz.db.password"))) {
            db.setAutoCommit(false);
            try {
                AccountCharacter demo = AccountCharacter.loadOrCreate(db, "player");
                require(demo.name.equals("FaZeGa"), "Existing character retained");
                String account = "test_" + Long.toHexString(System.nanoTime());
                try (PreparedStatement insert =
                        db.prepareStatement(
                                "INSERT INTO account(mot_de_passe,nom_de_compte,connected) VALUES('test',?,0)")) {
                    insert.setString(1, account);
                    insert.executeUpdate();
                }
                AccountCharacter first = AccountCharacter.loadOrCreate(db, account);
                AccountCharacter second = AccountCharacter.loadOrCreate(db, account);
                require(first.name.equals(second.name), "Repeated login must reuse sole character");
                try (PreparedStatement select =
                        db.prepareStatement(
                                "SELECT COUNT(*) FROM caracteristiques_joueur WHERE nom_joueur=?")) {
                    select.setString(1, first.name);
                    try (ResultSet rows = select.executeQuery()) {
                        rows.next();
                        require(rows.getInt(1) == 7, "Default character stats created once");
                    }
                }
                try (PreparedStatement extra =
                        db.prepareStatement(
                                "INSERT INTO personnage SELECT compte,race,classe,?,posx,orientation,posy FROM personnage WHERE name=?")) {
                    extra.setString(1, "A_" + account);
                    extra.setString(2, first.name);
                    extra.executeUpdate();
                }
                require(
                        AccountCharacter.loadOrCreate(db, account).name.equals(first.name),
                        "Legacy extras must not replace the account's selected character");
                System.out.println(
                        "PASS: existing, first-login, repeated-login and legacy-extra character policy.");
            } finally {
                db.rollback();
            }
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
