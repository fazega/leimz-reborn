package com.client.gameplay.managers;

import java.util.ArrayList;
import java.util.HashMap;

import com.client.display.gui.GUI_Manager;
import com.client.entities.Joueur;
import com.client.entities.MainJoueur;
import com.client.entities.managers.EntitiesManager;
import com.client.gameplay.Caracteristique;
import com.client.gameplay.Combat;
import com.client.gameplay.Equipe;
import com.client.gamestates.Base;
import com.client.map.Tile;
import com.client.map.managers.MapManager;
import com.client.network.NetworkListener;
import com.client.network.NetworkManager;
import com.client.utils.gui.PrincipalGui;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.ResizableFrame;


public class CombatManager implements NetworkListener
{
    private ArrayList<Combat> combats;
    private Combat current_combat;

    private ResizableFrame waitingAskFrame, answerFrame;

    public static CombatManager instance;

    public CombatManager()
    {
        instance = this;
        combats = new ArrayList<Combat>();
    }

    public void askCombat(Joueur j)
    {
        final String nom = j.getPerso().getNom();
        NetworkManager.instance.sendToServer("fi;ask;"+nom);

        waitingAskFrame = PrincipalGui.getDialogPane("En attente de rÃ©ponse pour votre demande de combat Ã  "+j.getPerso().getNom(),  PrincipalGui.CANCEL_PANE);
        ((Button)waitingAskFrame.getChild(0).getChild(1)).addCallback(new Runnable() {

            @Override
            public void run() {
                waitingAskFrame.setVisible(false);
                NetworkManager.instance.sendToServer("fi;can;"+nom);
            }
        });
        GUI_Manager.instance.getRoot().add(waitingAskFrame);
        waitingAskFrame.adjustSize();
        waitingAskFrame.setPosition((Base.sizeOfScreen_x/2)-(waitingAskFrame.getWidth()/2), (Base.sizeOfScreen_y/2)-(waitingAskFrame.getHeight()/2));
    }

    public void stopCurrentCombat(Equipe perdante)
    {
        current_combat.stop();
        current_combat = null;

        ResizableFrame resultsFrame = new ResizableFrame();
        resultsFrame.setTheme("/resizableframe");
        resultsFrame.setTitle("RÃ©sultats du combat formel");

        GUI_Manager.instance.getRoot().add(resultsFrame);
    }

    public void refresh()
    {
        if(current_combat != null)
        {
            Equipe perdante = null;
            ArrayList<HashMap<Joueur, Boolean>> checkinglist = new ArrayList<HashMap<Joueur, Boolean>>();
            for(int i = 0; i < current_combat.getEquipes().size(); i++)
            {
                for(int j = 0; j < current_combat.getEquipes().get(i).getMembres().size(); j++)
                {
                    System.out.println(current_combat.getEquipes().get(i).getMembres().get(j).getPerso().getNom()+" : vie;"+current_combat.getEquipes().get(i).getMembres().get(j).getPerso().getCaracs().get(Caracteristique.VIE));
                    checkinglist.add(new HashMap<Joueur, Boolean>());
                    if(current_combat.getEquipes().get(i).getMembres().get(j).getPerso().isDead())
                    {
                        checkinglist.get(checkinglist.size()-1).put(current_combat.getEquipes().get(i).getMembres().get(j), true);
                        System.out.println(current_combat.getEquipes().get(i).getMembres().get(j).getPerso().getNom()+" est mort !");
                    }
                    else
                    {
                        checkinglist.get(checkinglist.size()-1).put(current_combat.getEquipes().get(i).getMembres().get(j), false);
                    }
                }
            }
            for(int i = 0; i < checkinglist.size(); i++)
            {
                if(!checkinglist.get(i).containsValue(false))
                {
                    perdante = current_combat.getEquipes().get(i);
                }
            }
            if(perdante != null)
            {
                System.out.println("Equipe perdante, equipe de : "+perdante.getMembres().get(0).getPerso().getNom());
                stopCurrentCombat(perdante);
            }
            else
            {
                System.out.println("Pas d'Ã©quipe perdante !");
            }
        }

    }

    @Override
    public void receiveMessage(String str)
    {
        String[] temp = str.split(";");
            if(temp[0].contains("ask"))
            {
                final String nom = temp[1];
                answerFrame = PrincipalGui.getDialogPane(nom+" vous propose un combat formel. Acceptez-vous ?", PrincipalGui.YES_NO_PANE);

                ((Button)answerFrame.getChild(0).getChild(1)).addCallback(new Runnable() {

                    @Override
                    public void run()
                    {
                        NetworkManager.instance.sendToServer("fi;an;y;"+nom);
                        answerFrame.setVisible(false);

                        Equipe equipe1 = new Equipe("Equipe 1", MainJoueur.instance);
                        Equipe equipe2 = new Equipe("Equipe 2", EntitiesManager.instance.getPlayers_manager().getJoueur(nom));
                        ArrayList<Equipe> equipes = new ArrayList<Equipe>();
                        equipes.add(equipe1);
                        equipes.add(equipe2);

                        ArrayList<Tile> zone= new ArrayList<Tile>();
                        ArrayList<ArrayList<Tile>> tiles_autour = MapManager.instance.getTilesAutour(MainJoueur.instance.getTile(), 5);
                        for(int k = 0; k < tiles_autour.size(); k++)
                        {
                            zone.addAll(tiles_autour.get(k));
                        }
                        Combat combat = new Combat(equipes, zone);
                        MainJoueur.instance.getPerso().getCombats().add(combat);
                        current_combat = combat;
                        MainJoueur.instance.getPerso().setCurrent_combat(current_combat);
                    }
                });
                ((Button)answerFrame.getChild(0).getChild(2)).addCallback(new Runnable() {

                    @Override
                    public void run() {
                        NetworkManager.instance.sendToServer("fi;an;n;"+nom);
                        answerFrame.setVisible(false);
                    }
                });

                GUI_Manager.instance.getRoot().add(answerFrame);
                answerFrame.adjustSize();
                answerFrame.setPosition((Base.sizeOfScreen_x/2)-(answerFrame.getWidth()/2), (Base.sizeOfScreen_y/2)-(answerFrame.getHeight()/2));
            }
            else if(temp[0].contains("can"))
            {
                answerFrame.setVisible(false);
            }
            else if(temp[0].contains("an"))
            {
                if(temp[1].equals("y"))
                {
                    waitingAskFrame.setVisible(false);

                    String nom_j = temp[2];
                    Equipe equipe1 = new Equipe("Equipe 1", MainJoueur.instance);
                    Equipe equipe2 = new Equipe("Equipe 2", EntitiesManager.instance.getPlayers_manager().getJoueur(nom_j));
                    ArrayList<Equipe> equipes = new ArrayList<Equipe>();
                    equipes.add(equipe1);
                    equipes.add(equipe2);

                    ArrayList<Tile> zone= new ArrayList<Tile>();
                    ArrayList<ArrayList<Tile>> tiles_autour = MapManager.instance.getTilesAutour(MainJoueur.instance.getTile(), 5);
                    for(int k = 0; k < tiles_autour.size(); k++)
                    {
                        zone.addAll(tiles_autour.get(k));
                    }
                    Combat combat = new Combat(equipes, zone);
                    MainJoueur.instance.getPerso().getCombats().add(combat);
                    current_combat = combat;
                    MainJoueur.instance.getPerso().setCurrent_combat(current_combat);
                }
                else if(temp[1].equals("n"))
                {
                    waitingAskFrame.setVisible(false);
                }
            }


    }

    public ArrayList<Combat> getCombats() {
        return combats;
    }

    public void setCombats(ArrayList<Combat> combats) {
        this.combats = combats;
    }

    public Combat getCurrent_combat() {
        return current_combat;
    }

    public void setCurrent_combat(Combat current_combat) {
        this.current_combat = current_combat;
    }




}
