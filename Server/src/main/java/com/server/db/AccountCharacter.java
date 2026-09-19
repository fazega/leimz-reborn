package com.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/** One playable character per account; legacy extra rows are preserved, never selected. */
public final class AccountCharacter {
    public final String name, race, characterClass, orientation;
    public final int x, y;

    private AccountCharacter(ResultSet row) throws SQLException {
        name = row.getString("name");
        race = row.getString("race");
        characterClass = row.getString("classe");
        orientation = row.getString("orientation");
        x = row.getInt("posx");
        y = row.getInt("posy");
    }

    public String toMessage() {
        return "ci;new;"
                + name
                + ";"
                + race
                + ";"
                + characterClass
                + ";"
                + x
                + ";"
                + y
                + ";"
                + orientation
                + ";";
    }

    public static AccountCharacter loadOrCreate(Connection db, String account) throws SQLException {
        if (account == null) throw new IllegalArgumentException("Login required");
        synchronized (AccountCharacter.class) {
            // Lock this account until the caller commits character creation and selection.
            try (PreparedStatement lock =
                    db.prepareStatement(
                            "SELECT nom_de_compte FROM account WHERE nom_de_compte=? FOR UPDATE")) {
                lock.setString(1, account);
                try (ResultSet rows = lock.executeQuery()) {
                    if (!rows.next()) throw new SQLException("Account does not exist");
                }
            }
            AccountCharacter character = find(db, account);
            if (character == null) {
                String name = account;
                try (PreparedStatement exists =
                        db.prepareStatement("SELECT name FROM personnage WHERE name=?")) {
                    exists.setString(1, name);
                    try (ResultSet rows = exists.executeQuery()) {
                        if (rows.next())
                            name =
                                    account.substring(0, Math.min(15, account.length()))
                                            + "_"
                                            + UUID.randomUUID().toString().substring(0, 8);
                    }
                }
                try (PreparedStatement insert =
                        db.prepareStatement(
                                "INSERT INTO personnage(compte,race,classe,name,posx,orientation,posy) "
                                        + "VALUES(?,'Groz','barbare',?,20,'b',20)")) {
                    insert.setString(1, account);
                    insert.setString(2, name);
                    insert.executeUpdate();
                }
                String[] stats = {
                    "deplacement",
                    "dommages_cac",
                    "dommages_magie",
                    "endurance",
                    "energie",
                    "precision",
                    "vie"
                };
                int[] values = {10, 10, 10, 2500, 8000, 60, 150};
                try (PreparedStatement insert =
                        db.prepareStatement(
                                "INSERT INTO caracteristiques_joueur VALUES(?,?,?,?)")) {
                    for (int i = 0; i < stats.length; i++) {
                        insert.setString(1, name);
                        insert.setString(2, stats[i]);
                        insert.setInt(3, values[i]);
                        insert.setInt(4, values[i]);
                        insert.addBatch();
                    }
                    insert.executeBatch();
                }
                character = find(db, account);
            }
            try (PreparedStatement update =
                    db.prepareStatement("UPDATE account SET currjoueur=? WHERE nom_de_compte=?")) {
                update.setString(1, character.name);
                update.setString(2, account);
                update.executeUpdate();
            }
            return character;
        }
    }

    private static AccountCharacter find(Connection db, String account) throws SQLException {
        try (PreparedStatement select =
                db.prepareStatement(
                        "SELECT p.* FROM personnage p JOIN account a ON a.nom_de_compte=p.compte "
                                + "WHERE p.compte=? ORDER BY (p.name=COALESCE(a.currjoueur,'')) DESC,p.name LIMIT 1")) {
            select.setString(1, account);
            try (ResultSet rows = select.executeQuery()) {
                return rows.next() ? new AccountCharacter(rows) : null;
            }
        }
    }
}
