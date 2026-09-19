package com.server.core.functions;

import com.server.core.Client;
import com.server.db.AccountCharacter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class AccountCharacterFunction implements Functionable {
    @Override
    public void doSomething(String[] args, Client client) {
        try (Connection db =
                DriverManager.getConnection(
                        System.getProperty("leimz.db.url"),
                        System.getProperty("leimz.db.user"),
                        System.getProperty("leimz.db.password"))) {
            db.setAutoCommit(false);
            try {
                AccountCharacter character =
                        AccountCharacter.loadOrCreate(db, client.getCompte().getName());
                db.commit();
                client.getCompte().setCharacter(character);
                client.sendMessage(character.toMessage());
            } catch (SQLException | RuntimeException exception) {
                db.rollback();
                throw exception;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Cannot load account character", exception);
        }
    }
}
