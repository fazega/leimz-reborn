package com.server.core.functions;

import com.server.gameplay.Caracteristique;
import com.server.core.Client;
import com.server.core.ClientsManager;

public class CombatFunction implements Functionable {

    public CombatFunction() {}

    @Override
    public void doSomething(String[] args, Client c) {
        switch (args[1]) {
            case "ask":
                askingFunction(c, args);
                break;
            case "can":
                cancelAskingFunction(c, args);
                break;
            case "an":
                answerAskingFunction(c, args);
                break;
            default:
                throw new RuntimeException("Unimplemented");
        }
    }

    private void answerAskingFunction(Client c, String[] args) {
        Client receiver = ClientsManager.instance.getClient(args[3]);

        if (args[2].equals("y")) {
            receiver.sendMessage(
                    "s;j;"
                            + c.getCompte().getCurrent_joueur().getPerso().getNom()
                            + ";vie;"
                            + c.getCompte()
                                    .getCurrent_joueur()
                                    .getPerso()
                                    .getCaracs_values()
                                    .get(Caracteristique.VIE));
            c.sendMessage(
                    "s;j;"
                            + receiver.getCompte().getCurrent_joueur().getPerso().getNom()
                            + ";vie;"
                            + receiver.getCompte()
                                    .getCurrent_joueur()
                                    .getPerso()
                                    .getCaracs_values()
                                    .get(Caracteristique.VIE));
            receiver.sendMessage(
                    "fi;an;y;" + c.getCompte().getCurrent_joueur().getPerso().getNom());
        } else if (args[2].equals("n")) {
            receiver.sendMessage(
                    "fi;an;n;" + c.getCompte().getCurrent_joueur().getPerso().getNom());
        }
    }

    private void askingFunction(Client c, String[] args) {
        Client receiver = ClientsManager.instance.getClient(args[2]);

        receiver.sendMessage("fi;ask;" + c.getCompte().getCurrent_joueur().getPerso().getNom());
    }

    private void cancelAskingFunction(Client c, String[] args) {
        Client receiver = ClientsManager.instance.getClient(args[2]);

        receiver.sendMessage("fi;can;" + c.getCompte().getCurrent_joueur().getPerso().getNom());
    }
}
