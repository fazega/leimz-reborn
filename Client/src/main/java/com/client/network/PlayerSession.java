package com.client.network;

import com.client.entities.Joueur;
import com.client.entities.MainJoueur;
import com.client.gameplay.entities.Personnage;
import com.client.map.Tile;
import org.newdawn.slick.geom.Vector2f;

/** Opens the account's sole character without a character-selection screen. */
public final class PlayerSession {
    private PlayerSession() {}

    public static void enterWorld(NetworkManager network) {
        network.sendToServer("ci;");
        network.waitForNewMessage("ci");
        String[] fields = parseCharacter(network.receiveFromServer("ci"));
        new MainJoueur(
                new Personnage(fields[0], fields[1], fields[2]),
                null,
                Joueur.parseStringOrientation(fields[5]));
        MainJoueur.instance.setTile(
                new Tile(
                        new Vector2f(Integer.parseInt(fields[3]), Integer.parseInt(fields[4])),
                        null));
        network.sendToServer("lo;j;i;");
    }

    public static String[] parseCharacter(String message) {
        if (message == null || !message.startsWith("new;"))
            throw new IllegalArgumentException("No character returned for this account");
        String[] fields = message.substring(4).split(";");
        if (fields.length != 6) throw new IllegalArgumentException("Expected one character");
        Integer.parseInt(fields[3]);
        Integer.parseInt(fields[4]);
        return fields;
    }
}
