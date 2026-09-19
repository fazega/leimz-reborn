package com.client.gamestates;

import java.io.File;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.client.display.gui.GUI_Manager;
import com.client.network.NetworkManager;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.EditField;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ResizableFrame;
import de.matthiasmann.twl.DialogLayout.Group;
import de.matthiasmann.twl.ResizableFrame.ResizableAxis;

public class Identification extends BasicGameState {
    // Images diverses
    private Image logo, fond;

    // -------------------------------GUI-----------------------------
    // Gestionnaire de GUI
    private GUI_Manager gui_manager;

    // Creation des elements GUI
    private ResizableFrame loginPanel;
    private EditField ef_password, ef_login;
    private Label l_password, l_login;

    // Recuperation de la base et du game container
    private StateBasedGame test_sbg;
    private GameContainer test_gc;

    // Musique de fond
    // private Music music;

    @Override
    public int getID() {
        return 1;
    }

    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {}

    @Override
    public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
        logo = new Image("data/Images/Logo/Leimz_Logo_Final_HD_Transparent.png");
        fond = new Image("data/Images/fond.png");

        // Un intervalle de 20 entre chaque boucle de rendu et d'update
        gc.setMinimumLogicUpdateInterval(20);

        test_sbg = sbg;
        test_gc = gc;

        // music = new Music("data/Musics/musique1.wav");

        log();
        // music.loop();
    }

    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        // Affichage des images de fond
        fond.draw();
        logo.draw(
                Base.sizeOfScreen_x / 2 - (logo.getWidth() * 0.4f / 2),
                Base.sizeOfScreen_y / 2 - (logo.getHeight() * 0.4f / 2) - 200,
                0.4f);
        // Affichage des elements GUI
        gui_manager.getTwlInputAdapter().render();
    }

    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int arg2) throws SlickException {
        gui_manager.getTwlInputAdapter().update();

        // Quand on appuie sur entree, ca valide le ok de login ou de popup
        if (gc.getInput().isKeyPressed(Input.KEY_ENTER)) {
            if (gui_manager.getRoot().getChild(0).isEnabled()) test();
            else {
                loginPanel.setEnabled(true);
                gui_manager.getRoot().removeChild(gui_manager.getRoot().getChild(1));
                gui_manager.getRoot().focusFirstChild();
                loginPanel.focusFirstChild();
            }
        }
    }

    private void log() {
        try {
            gui_manager =
                    new GUI_Manager(
                            new File(
                                            System.getProperty("user.dir")
                                                    + File.separator
                                                    + "data"
                                                    + File.separator
                                                    + "GUI"
                                                    + File.separator
                                                    + "Theme"
                                                    + File.separator
                                                    + "projet.xml")
                                    .toURI()
                                    .toURL(),
                            test_gc);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        loginPanel = new ResizableFrame();
        loginPanel.setTheme("/resizableframe");
        loginPanel.setResizableAxis(ResizableAxis.NONE);

        DialogLayout panel = new DialogLayout();
        panel.setTheme("/dialoglayout");

        ef_password = new EditField();
        ef_password.setTheme("/editfield");
        ef_password.setText("");
        ef_password.setSize(300, 26);
        ef_password.setPosition(0, 100);
        ef_password.setPasswordMasking(true);
        ef_password.setColumns(5);

        ef_login = new EditField();
        ef_login.setTheme("/editfield");
        ef_login.setText("");
        ef_login.setSize(300, 26);
        ef_login.setPosition(0, 30);

        l_password = new Label();
        l_password.setTheme("/label");
        l_password.setText("Password :");
        l_password.setPosition(0, 70);
        l_password.setSize(150, 25);
        l_password.setCanAcceptKeyboardFocus(false);

        l_login = new Label();
        l_login.setTheme("/label");
        l_login.setText("Nom d'utilisateur :");
        l_login.setPosition(0, 0);
        l_login.setSize(150, 25);
        l_login.setCanAcceptKeyboardFocus(false);

        Button bouton = new Button("Connexion");
        bouton.setTheme("/button");
        bouton.setSize(150, 30);
        bouton.setPosition(75, 170);
        bouton.addCallback(
                new Runnable() {

                    @Override
                    public void run() {
                        test();
                    }
                });

        panel.setVerticalGroup(
                panel.createSequentialGroup(l_login, ef_login, l_password, ef_password, bouton));
        panel.setHorizontalGroup(
                panel.createParallelGroup(l_login, ef_login, l_password, ef_password, bouton));

        loginPanel.add(panel);
        loginPanel.adjustSize();

        gui_manager.getRoot().add(loginPanel);
        loginPanel.setPosition(Base.sizeOfScreen_x / 2 - 150, Base.sizeOfScreen_y / 2 - 40);
        gui_manager.getRoot().focusFirstChild();
        loginPanel.focusFirstChild();
    }

    private void test() {
        NetworkManager network = null;
        try {
            // Adresse locale : 127.0.0.1
            network =
                    new NetworkManager(
                            InetAddress.getByName(System.getProperty("leimz.host", "127.0.0.1")),
                            Integer.getInteger("leimz.port", 1500));
            network.waitForNewMessage("co");
            if (network.receiveFromServer("co").equals("CONNECT_SERVER"))
                network.sendToServer("c;" + ef_login.getText() + ";" + ef_password.getText());
            else {
                openPopup("Temps de communication avec le serveur trop long.");
                return;
            }
        } catch (UnknownHostException ex) {
            openPopup("Serveur inconnu. Videz le cache ou redemarrez l'application.");
            ex.printStackTrace();
            return;
        } catch (IOException ex) {
            openPopup("Serveur deconnecte");
            ex.printStackTrace();
            return;
        }

        network.waitForNewMessage("c");
        String confirm = network.receiveFromServer("c");
        if (confirm.equals("CONNECT_SUCCEED")) {
            System.out.println("Connection OK.");
            // music.stop();
            test_sbg.enterState(Base.CHOIX_PERSO);
        } else {
            String info = confirm.split(";")[1];
            if (info.equals("INCORRECT_NDC_PASS")) {
                openPopup("Nom de compte ou mot de passe incorrect. Veuillez reessayer :p");
            } else {
                openPopup("Compte dÃ©jÃ  connectÃ©.");
            }
        }
    }

    private void openPopup(String text) {
        final ResizableFrame frame;

        frame = new ResizableFrame();
        frame.setTheme("/resizableframe");
        frame.setPosition(350, 300);
        frame.setResizableAxis(ResizableAxis.NONE);

        Label label = new Label(text);
        label.setTheme("/label");
        label.setCanAcceptKeyboardFocus(false);

        Button button = new Button("OK");
        button.setTheme("/button");
        button.addCallback(
                new Runnable() {

                    @Override
                    public void run() {
                        // Pour rendre la main a la fenetre principal et detruire la fenetre
                        gui_manager.getRoot().getChild(0).setEnabled(true);
                        gui_manager.getRoot().removeChild(frame);
                        gui_manager.getRoot().focusFirstChild();
                        loginPanel.focusFirstChild();
                    }
                });

        DialogLayout panel = new DialogLayout();
        panel.setTheme("/dialoglayout");
        Group hbutton = panel.createSequentialGroup().addWidget(button);
        Group hlabel = panel.createSequentialGroup().addWidget(label);
        panel.setHorizontalGroup(panel.createParallelGroup(hbutton, hlabel));
        panel.setVerticalGroup(
                panel.createSequentialGroup().addWidget(label).addGap(25).addWidget(button));

        frame.add(panel);

        // Pour empecher qu'on clic dessous quand il y a un popup.
        gui_manager.getRoot().getChild(0).setEnabled(false);
        gui_manager.getRoot().add(frame);

        frame.setPosition(
                (Base.sizeOfScreen_x / 2) - (frame.getPreferredWidth() / 2),
                (Base.sizeOfScreen_y / 2) - (frame.getPreferredHeight() / 2) + 50);
        panel.adjustSize();
        label.setBackground(null);
        gui_manager.getRoot().focusFirstChild();
    }
}
