package com.client.load;

import java.util.ArrayList;

import java.util.HashMap;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.LoadingList;
import com.client.entities.Joueur;
import com.client.entities.MainJoueur;
import com.client.gameplay.Caracteristique;
import com.client.gameplay.Inventaire;
import com.client.gameplay.Quete;
import com.client.gameplay.QueteObjectif;
import com.client.gameplay.Sort;
import com.client.gameplay.items.Equipement;
import com.client.gameplay.items.ObjetQuete;
import com.client.network.NetworkManager;

/** @author fazega */
public class LoadJoueur implements Runnable {
    private Thread t;
    private int purcent;
    private Joueur joueur;
    private ArrayList<Joueur> list_joueur;

    public LoadJoueur() {
        this.list_joueur = new ArrayList<Joueur>();
        this.purcent = 0;
        t = new Thread(this);
    }

    public void start() {
        t.start();
    }

    @Override
    public void run() {

        // -------------------GESTION DE LA RACE-----------------------

        MainJoueur.instance.getPerso().getRace().setSorts(getSorts("lo;j;rs"));
        MainJoueur.instance.getPerso().getRace().setCarac(getCaracteristic("lo;j;rc"));

        // -------------------GESTION DE LA CLASSE-----------------------

        MainJoueur.instance.getPerso().getClasse().setSorts(getSorts("lo;j;rs"));
        MainJoueur.instance.getPerso().getClasse().setCaracs(getCaracteristic("lo;j;rc"));

        // ---------------GESTION DU PERSONNAGE------------------

        MainJoueur.instance.getPerso().setCaracs_values(getCaracteristic("lo;j;jcv"));
        MainJoueur.instance.getPerso().setCaracs(getCaracteristic("lo;j;jc"));

        // ----------------GESTION DE L'INVENTAIRE---------------------

        LoadingList.setDeferredLoading(true);

        Inventaire inventaire = new Inventaire();

        NetworkManager.instance.sendToServer("lo;j;in"); // load joueur, joueur caracteristiques
        NetworkManager.instance.waitForNewMessage("in");
        String[] str_i = NetworkManager.instance.receiveFromServer("in").split(";");

        // Do nothing if inventaire is empty
        if (str_i.length > 1) {
            for (int i = 0; i < str_i.length; i += 4) {
                switch (str_i[i + 2]) {
                    case "equipement":
                        try {
                            inventaire.addItem(
                                    new Equipement(
                                            str_i[i],
                                            str_i[i + 3],
                                            str_i[i + 1],
                                            new Image(
                                                    "data/Images/Objets/"
                                                            + str_i[i].toLowerCase()
                                                                    .replace(" ", "_")
                                                            + ".png"),
                                            new Image(
                                                    "data/Images/Objets/"
                                                            + str_i[i].toLowerCase()
                                                                    .replace(" ", "_")
                                                            + ".png"),
                                            null,
                                            20));
                        } catch (SlickException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "objet_quete":
                        try {
                            inventaire.addItem(
                                    new ObjetQuete(
                                            str_i[i],
                                            str_i[i + 1],
                                            new Image(
                                                    "data/Images/Objets/"
                                                            + str_i[i].toLowerCase()
                                                                    .replace(" ", "_")
                                                            + ".png"),
                                            new Image(
                                                    "data/Images/Objets/"
                                                            + str_i[i].toLowerCase()
                                                                    .replace(" ", "_")
                                                            + ".png"),
                                            null,
                                            20));
                        } catch (SlickException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        }
        LoadingList.setDeferredLoading(false);

        MainJoueur.instance.getPerso().setInventaire(inventaire);

        // ----------------GESTION DES QUETES---------------------

        NetworkManager.instance.sendToServer("lo;j;q"); // load joueur, joueur caracteristiques
        NetworkManager.instance.waitForNewMessage("q");
        String message_q = NetworkManager.instance.receiveFromServer("q");
        System.out.println("Message quÃªtes : " + message_q);
        String[] qm = message_q.split("new;");
        ArrayList<Quete> quetes = new ArrayList<Quete>();
        for (int i = 1; i < qm.length; i++) {
            Quete quete = new Quete(null, null);
            ArrayList<QueteObjectif> ql = new ArrayList<>();
            String[] str_q = qm[i].split(";");
            quete.setNom(str_q[1]);
            quete.setCommanditaire(str_q[0]);
            for (int j = 2; j < str_q.length; j += 5) {
                QueteObjectif quete_o = new QueteObjectif(str_q[j + 1], str_q[j + 2], str_q[j + 3]);
                if (str_q[j + 4].equals("1")) quete_o.setAccompli(true);
                ql.add(quete_o);
            }
            quete.setObjectifs(ql);
            quetes.add(quete);
        }
        MainJoueur.instance.getPerso().getQuetes_manager().setQuetes(quetes);

        purcent += 6;
    }

    public int getPurcent() {
        return purcent;
    }

    public void setPurcent(int purcent) {
        this.purcent = purcent;
    }

    @SuppressWarnings("unchecked")
    private HashMap<Caracteristique, Integer> getCaracteristic(String message) {
        NetworkManager.instance.sendToServer(message);
        NetworkManager.instance.waitForNewMessage(message.split(";", 3)[2]);
        String[] caract =
                NetworkManager.instance.receiveFromServer(message.split(";", 3)[2]).split(";");
        if (caract.length < 2)
            throw new RuntimeException("Incorrect caracteristic loading message from server");

        HashMap<Caracteristique, Integer> caracs = new HashMap<Caracteristique, Integer>();
        for (int i = 0; i < caract.length; i += 2)
            caracs.put(
                    Caracteristique.valueOf(caract[i].toUpperCase()),
                    Integer.parseInt(caract[i + 1]));
        return (HashMap<Caracteristique, Integer>) caracs.clone();
    }

    @SuppressWarnings("unchecked")
    private ArrayList<Sort> getSorts(String message) {
        ArrayList<Sort> sorts = new ArrayList<Sort>();
        NetworkManager.instance.sendToServer(message);
        NetworkManager.instance.waitForNewMessage(message.split(";")[2]);
        String[] sort = NetworkManager.instance.receiveFromServer(message.split(";")[2]).split(";");
        if (sort.length < 4)
            throw new RuntimeException("Incorrect sorts loading message from server");

        for (int i = 0; i < sort.length; i += 4)
            sorts.add(
                    new Sort(
                            sort[i],
                            sort[i + 3],
                            Integer.parseInt(sort[i + 1]),
                            Integer.parseInt(sort[i + 2]),
                            null));

        return (ArrayList<Sort>) sorts.clone();
    }

    public Joueur getJoueur() {
        return joueur;
    }

    public void setJoueur(Joueur joueur) {
        this.joueur = joueur;
    }

    public ArrayList<Joueur> getPlayers() {
        return list_joueur;
    }

    public Thread getT() {
        return t;
    }

    public void setT(Thread t) {
        this.t = t;
    }
}
