package com.loop;

import java.io.File;

import java.net.MalformedURLException;
import java.util.ArrayList;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.MouseListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import com.gui.GUI_Manager;
import com.map.Grille;
import com.map.Tile;
import com.map.Type_tile;
import com.map.manager.MapManager;
import com.utils.gui.NewTypeFrame;
import com.utils.gui.Toolbox;
import com.utils.gui.Toolbox.Pointeur;
import com.utils.gui.TypesStore;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.EditField;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.Menu;
import de.matthiasmann.twl.ResizableFrame;
import de.matthiasmann.twl.Widget;

public class Principal extends BasicGameState implements MouseListener {

    static Vector2f absolute;

    // ----------------------------Map-----------------------------------

    // -------------------------Selection--------------------------------
    static Image img;
    ArrayList<Tile> clicked;
    ArrayList<Image> img_clicked;
    Image img_M;
    ArrayList<Image> list_img_M;
    ArrayList<Vector2f> list_pos_img_M;

    private MapManager mapManager;
    private ResizableFrame openMapFrame, insertMapFrame, pointeurFrame;
    private TypesStore typesstore;
    private Toolbox toolbox;
    private Menu mainmenu;

    // Fonctionnalités
    private enum Function {
        SELECTION,
        MOVE_MAP,
        PLUS,
        SUPPR
    };

    private Function current_function;

    private class Selection extends Rectangle {
        Vector2f mouseOld;

        public Vector2f getMouseOld() {
            return mouseOld;
        }

        public void setMouseOld(Vector2f mouseOld) {
            this.mouseOld = mouseOld;
        }

        public Selection(float x, float y, float width, float height) {
            super(x, y, width, height);
        }

        public void draw(Graphics g) {
            g.setColor(new Color(255, 146, 214, 100));
            g.fill(this);
            g.setColor(new Color(183, 255, 0));
            g.setLineWidth(3);
            g.draw(this);
        }
    }

    private Selection selection;

    GUI_Manager gui_manager;

    // boolean clic = false, selec = false, moveScreen = false, gestion_monstres = false;

    @Override
    public int getID() {
        return 1;
    }

    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {

        Type_tile.setOnTypes();

        img = new Image("data/tiles/tile_simple.png");
        clicked = new ArrayList<Tile>();
        img_clicked = new ArrayList<Image>();
        absolute = new Vector2f(100, 100);

        list_img_M = new ArrayList<Image>();
        list_pos_img_M = new ArrayList<Vector2f>();

        nouvelleCarte();

        try {
            gui_manager = new GUI_Manager(new File("data/GUI/Theme/projet.xml").toURL(), gc);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        final NewTypeFrame newTypeFrame = new NewTypeFrame();
        gui_manager.getRoot().add(newTypeFrame);

        typesstore = new TypesStore();
        typesstore.setPosition(300, 300);
        /*typesstore.getNewTypeButton().addCallback(new Runnable()
        {

            @Override
            public void run() {

                newTypeFrame.setPosition(200, 200);
                newTypeFrame.setVisible(!newTypeFrame.isVisible());
                newTypeFrame.requestKeyboardFocus();
            }

        });*/
        gui_manager.getRoot().add(typesstore);
        typesstore.requestKeyboardFocus();

        toolbox = new Toolbox();
        toolbox.setPosition(1300, 100);
        gui_manager.getRoot().add(toolbox);

        openMapFrame = new ResizableFrame();
        Label openFileL = new Label("Chemin :");
        openFileL.setTheme("/label");
        final EditField openFileF = new EditField();
        openFileF.setTheme("/editfield");
        openFileL.setLabelFor(openFileF);
        Button okButton = new Button("Ouvrir la map");
        okButton.setTheme("/button");
        okButton.addCallback(
                new Runnable() {

                    @Override
                    public void run() {
                        openMapFrame.setVisible(false);
                        openCarte(openFileF.getText());
                    }
                });
        DialogLayout openMapLayout = new DialogLayout();
        openMapLayout.setTheme("/dialoglayout");
        openMapLayout.setHorizontalGroup(
                openMapLayout
                        .createParallelGroup(
                                openMapLayout.createSequentialGroup(openFileL, openFileF))
                        .addWidget(okButton));
        openMapLayout.setVerticalGroup(
                openMapLayout
                        .createSequentialGroup()
                        .addGroup(openMapLayout.createParallelGroup(openFileL, openFileF))
                        .addGap(40)
                        .addWidget(okButton));
        openMapFrame.add(openMapLayout);
        openMapFrame.setVisible(false);
        openMapFrame.setPosition(
                (Base.sizeOfScreen_x / 2) - (openMapFrame.getWidth() / 2),
                (Base.sizeOfScreen_y / 2) - (openMapFrame.getHeight() / 2));
        gui_manager.getRoot().add(openMapFrame);

        insertMapFrame = new ResizableFrame();
        Label insertFileL = new Label("Chemin :");
        insertFileL.setTheme("/label");
        final EditField insertFileF = new EditField();
        insertFileF.setTheme("/editfield");
        insertFileL.setLabelFor(openFileF);
        Button okButtonI = new Button("Insérer la map");
        okButtonI.setTheme("/button");
        okButtonI.addCallback(
                new Runnable() {

                    @Override
                    public void run() {
                        insertMapFrame.setVisible(false);
                        insertCarte(insertFileF.getText());
                    }
                });
        DialogLayout insertMapLayout = new DialogLayout();
        insertMapLayout.setTheme("/dialoglayout");
        insertMapLayout.setHorizontalGroup(
                insertMapLayout
                        .createParallelGroup(
                                insertMapLayout.createSequentialGroup(insertFileL, insertFileF))
                        .addWidget(okButtonI));
        insertMapLayout.setVerticalGroup(
                insertMapLayout
                        .createSequentialGroup()
                        .addGroup(insertMapLayout.createParallelGroup(insertFileL, insertFileF))
                        .addGap(40)
                        .addWidget(okButtonI));
        insertMapFrame.add(insertMapLayout);
        insertMapFrame.setVisible(false);
        insertMapFrame.setPosition(
                (Base.sizeOfScreen_x / 2) - (insertMapFrame.getWidth() / 2),
                (Base.sizeOfScreen_y / 2) - (insertMapFrame.getHeight() / 2));
        gui_manager.getRoot().add(insertMapFrame);

        mainmenu = new Menu();
        mainmenu.setTheme("/mainmenu");

        Menu menuFile = new Menu();
        menuFile.setName("Fichier");
        menuFile.add(
                "Nouvelle carte ...",
                new Runnable() {

                    @Override
                    public void run() {
                        nouvelleCarte();
                    }
                });
        menuFile.add(
                "Ouvrir ...",
                new Runnable() {
                    @Override
                    public void run() {
                        openMapFrame.setVisible(true);
                    }
                });
        menuFile.add(
                "Insérer une map",
                new Runnable() {

                    @Override
                    public void run() {
                        insertMapFrame.setVisible(true);
                    }
                });
        menuFile.add(
                "Enregistrer",
                new Runnable() {

                    @Override
                    public void run() {
                        mapManager.saveMapToXML("data/Maps/map2.xml");
                    }
                });
        mainmenu.add(menuFile);

        Widget menuBar = mainmenu.createMenuBar();
        menuBar.setPosition(0, 0);
        menuBar.setSize(1353, 30);

        gui_manager.getRoot().add(menuBar);
    }

    public void openCarte(String path) {
        Document doc = null;
        Element root;

        // On crée une instance de SAXBuilder
        SAXBuilder sxb = new SAXBuilder();
        try {
            doc = sxb.build(new File(path));
        } catch (Exception e) {
        }

        root = doc.getRootElement();

        System.out.println("Chargement du fichier OK");
        Grille grille = new Grille();
        int max_x =
                Integer.parseInt(
                                ((Element) root.getChildren().get(root.getChildren().size() - 1))
                                        .getChild("id_x")
                                        .getText())
                        + 1;
        int max_y =
                Integer.parseInt(
                                ((Element) root.getChildren().get(root.getChildren().size() - 1))
                                        .getChild("id_x")
                                        .getText())
                        + 1;
        for (int i = 0; i < max_x; i++) { // Creation d'une map
            grille.add(new ArrayList<Tile>());
            for (int j = 0; j < max_y; j++) {
                grille.get(i).add(new Tile(i, j, Type_tile.types.get("herbe")));
            }
        }
        System.out.println("Grille OK, taille " + grille.size());
        for (int i = 0; i < root.getChildren().size(); i++) {
            Element e = ((Element) root.getChildren().get(i));
            ArrayList<Type_tile> list = new ArrayList<>();
            for (int u = 0; u < e.getChild("types").getChildren().size(); u++) {
                list.add(
                        Type_tile.types.get(
                                ((Element) e.getChild("types").getChildren().get(u)).getText()));
            }

            grille.get(Integer.parseInt(e.getChild("id_x").getText()))
                    .get(Integer.parseInt(e.getChild("id_y").getText()))
                    .setTypes(list);
        }
        this.mapManager.setGrille(grille);
    }

    public void insertCarte(String path) {
        Document doc = null;
        Element root;

        // On crée une instance de SAXBuilder
        SAXBuilder sxb = new SAXBuilder();
        try {
            doc = sxb.build(new File(path));
        } catch (Exception e) {
        }

        root = doc.getRootElement();

        System.out.println("Chargement du fichier OK");
        Grille grille = mapManager.getGrille();
        System.out.println("Grille OK, taille " + grille.size());
        for (int i = 0; i < root.getChildren().size(); i++) {
            Element e = ((Element) root.getChildren().get(i));
            ArrayList<Type_tile> list = new ArrayList<>();
            for (int u = 0; u < e.getChildren("types").size(); u++) {
                list.add(Type_tile.types.get(((Element) e.getChildren("types").get(u)).getText()));
            }

            grille.get(Integer.parseInt(e.getChild("id_x").getText()))
                    .get(Integer.parseInt(e.getChild("id_y").getText()))
                    .setTypes(list);
        }
        this.mapManager.setGrille(grille);
    }

    public void nouvelleCarte() {
        /** Construction de notre grille de base */
        Grille grille = new Grille();

        for (int i = 0; i < 200; i++) { // Creation d'une map 50*50
            grille.add(new ArrayList<Tile>());
            for (int j = 0; j < 200; j++) {
                grille.get(i).add(new Tile(i, j, Type_tile.types.get("herbe")));
            }
        }

        /**
         * Création de notre MapManger en passant en paramètre la liste de Map créé précédemment.
         */
        this.mapManager = new MapManager(grille);
        this.mapManager.init();
        this.mapManager.setAbsolute(absolute);
    }

    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics gr) throws SlickException {
        gr.setColor(new Color(210, 195, 107));
        gr.fillRect(0, 0, 1353, 700);

        this.mapManager.drawAndRefreshAll();

        for (int i = 0; i < clicked.size(); i++) {
            img.draw(clicked.get(i).getPos_x_real(), clicked.get(i).getPos_y_real());
        }

        if (selection != null) {
            selection.draw(gr);
        }

        gui_manager.getTwlInputAdapter().render();
    }

    @Override
    public void update(GameContainer gc, StateBasedGame arg1, int arg2) throws SlickException {
        gc.setMinimumLogicUpdateInterval(5);
        Input input = gc.getInput();

        if (!gui_manager.isOn_gui_event()) {

            if (input.isKeyDown(Input.KEY_SPACE)) {
                selection = null;
                clicked.removeAll(clicked);
            } /*

              if(input.isKeyPressed(Input.KEY_M))
              {
                  gestion_monstres = !gestion_monstres;
                  selec = false;
                  moveScreen = false;

              }

              if(input.isKeyPressed(Input.KEY_D))
              {
                  moveScreen = !moveScreen;
                  selec = false;
                  gestion_monstres = false;
              }

              if(input.isKeyPressed(Input.KEY_S))
              {
                  selec = !selec;
                  moveScreen = false;
                  gestion_monstres = false;
              }*/
        }

        if (typesstore.getDroppedTile() != null) {
            // Récupération de la tile sur la grille on notre fleche est.
            Tile tileGrille = this.mapManager.getTilePointed(input.getMouseX(), input.getMouseY());

            if (tileGrille != null) { // On remplace la tyle ciblé par le curseur par le type tile
                // selectionné
                if (clicked.contains(tileGrille)) {
                    for (Tile tile : clicked) {
                        if (typesstore.getDroppedTile().getTile().getTypes().size() == 1) {
                            if (tile.getTypes().size() == 0)
                                tile.getTypes()
                                        .add(
                                                typesstore
                                                        .getDroppedTile()
                                                        .getTile()
                                                        .getTypes()
                                                        .get(0));
                            else
                                tile.getTypes()
                                        .set(
                                                0,
                                                typesstore
                                                        .getDroppedTile()
                                                        .getTile()
                                                        .getTypes()
                                                        .get(0));
                            tile.getTypes().get(0).getImg().draw();
                        } else {
                            tile.getTypes()
                                    .add(
                                            typesstore
                                                    .getDroppedTile()
                                                    .getTile()
                                                    .getTypes()
                                                    .get(
                                                            typesstore
                                                                            .getDroppedTile()
                                                                            .getTile()
                                                                            .getTypes()
                                                                            .size()
                                                                    - 1));
                            System.out.println("type ajouté");
                        }
                    }
                } else {
                    if (typesstore.getDroppedTile().getTile().getTypes().size() == 1) {
                        if (tileGrille.getTypes().size() == 0)
                            tileGrille
                                    .getTypes()
                                    .add(typesstore.getDroppedTile().getTile().getTypes().get(0));
                        else
                            tileGrille
                                    .getTypes()
                                    .set(
                                            0,
                                            typesstore
                                                    .getDroppedTile()
                                                    .getTile()
                                                    .getTypes()
                                                    .get(0));
                        tileGrille.getTypes().get(0).getImg().draw();
                    } else {
                        tileGrille
                                .getTypes()
                                .add(
                                        typesstore
                                                .getDroppedTile()
                                                .getTile()
                                                .getTypes()
                                                .get(
                                                        typesstore
                                                                        .getDroppedTile()
                                                                        .getTile()
                                                                        .getTypes()
                                                                        .size()
                                                                - 1));
                        System.out.println("type ajouté");
                    }
                }
            }

            typesstore.getDroppedTile().setDropped(false);
        }

        toolbox.refresh();
        if (toolbox.getSelectedPointeur().equals(Pointeur.MAIN)) {
            current_function = Function.MOVE_MAP;
        } else if (toolbox.getSelectedPointeur().equals(Pointeur.SELEC)) {
            current_function = Function.SELECTION;
        } else if (toolbox.getSelectedPointeur().equals(Pointeur.PLUS)) {
            current_function = Function.PLUS;
        } else if (toolbox.getSelectedPointeur().equals(Pointeur.SUPPR)) {
            current_function = Function.SUPPR;
        }
        this.mapManager.setAbsolute(absolute);

        gui_manager.getTwlInputAdapter().update();
    }

    @Override
    public void mouseDragged(int oldx, int oldy, int newx, int newy) {

        int offx = newx - oldx;
        int offy = newy - oldy;

        if (current_function.equals(Function.MOVE_MAP)) {
            absolute = new Vector2f(absolute.x + offx, absolute.y + offy);
        } else if (current_function.equals(Function.SELECTION)) {
            if (selection == null) {
                selection = new Selection(oldx, oldy, offx, offy);
                selection.setMouseOld(new Vector2f(oldx, oldy));
            } else {
                selection.setSize(
                        newx - selection.getMouseOld().x, newy - selection.getMouseOld().y);
            }

            // Tile tileGrille = this.mapManager.getMap(0).getTilePointed(x, y);

            /*if(calqueSelection.getGrille().get(i).get(j).isPointed(newx, newy))
            {
                calqueSelection.getGrille().get(i).get(j).setState(Tile.CLICKED);
            }
            else if(!calqueSelection.getGrille().get(i).get(j).isPointed(newx, newy) && selec == false)
            {
                calqueSelection.getGrille().get(i).get(j).setState(Tile.NONE);
            }

            if(calqueSelection.getGrille().get(i).get(j).getState() == Tile.CLICKED)
            {
                selection.add(new Vector2f(calqueSelection.getGrille().get(i).get(j).getPos_x_real(), calqueSelection.getGrille().get(i).get(j).getPos_y_real()));
            }*/
        }
    }

    @Override
    public void mouseClicked(int button, int x, int y, int clickcount) {
        // Si la souris est cliquée (1 ou plusieurs clics).
    }

    /**
     * Cette fonction gère le remplacement d'une Tile de la grille si un Tile "type" a été posé sur
     * la grille par l'utilisateur.
     */
    @Override
    public void mouseReleased(int button, int x, int y) {

        if (selection != null) // S'il y a eu une selection
        {
            long timer = System.currentTimeMillis();
            ArrayList<Tile> tiles_selectionnees =
                    new ArrayList<
                            Tile>(); // On crée le tableau qui contiendra les tiles sélectionnées

            for (int i = 0; i < (int) selection.getWidth(); i += 40) {
                for (int j = 0; j < (int) selection.getHeight(); j += 20) {
                    Tile tile =
                            this.mapManager.getTilePointed(
                                    i + (int) selection.getX(), j + (int) selection.getY());
                    if (tile != null
                            && !clicked.contains(tile)
                            && !tiles_selectionnees.contains(tile)) {
                        tiles_selectionnees.add(tile);
                    }
                }
            }
            clicked.addAll(tiles_selectionnees);

            long time = (System.currentTimeMillis() - timer);
            System.out.println("Temps d'exécution : " + time);
        }

        selection = null;
        /*
        if(this.mapManager.getTypeTileCurrent() != null){ //On test si l'utilisateur a selectionné un type de tile sinon, ca ne sert à rien d'aller plus loin.

            //Récupération de la tile sur la grille on notre fleche est.
            Tile tyleGrille = this.mapManager.getMap(0).getTilePointed(x, y);

            if(tyleGrille != null){ //On remplace la tyle ciblé par le curseur par le type tile selectionné
                tyleGrille.setType(this.mapManager.getTypeTileCurrent().getType());
                tyleGrille.getType().getImg().draw();
            }

            int index = this.mapManager.getIndexListeTypeTile(this.mapManager.getTypeTileCurrent());
            this.mapManager.getTypeTileCurrent().setPos_x_real(0);
            this.mapManager.getTypeTileCurrent().setPos_y_real(this.mapManager.getTypeTileCurrent().getType().getImg().getHeight()*index + 50); // 50 représente le décalage "de base"
            this.mapManager.getTypeTileCurrent().drawAtRealPositions();
        }*/
    }

    @Override
    public void mousePressed(int button, int x, int y) {
        if (current_function.equals(Function.SUPPR)) {
            Tile tile = mapManager.getTilePointed(x, y);
            if (tile != null) {
                ArrayList<Type_tile> t = new ArrayList<>();
                t.add(tile.getTypes().get(0));
                tile.setTypes(t);
            }
        }

        // --------------------------------------MAP------------------------------*
        /*if(button == 0)
        {

            Tile tileGrille = this.mapManager.getTilePointed(x, y);
            if(tileGrille != null && !moveScreen && !selec && !gestion_monstres)
            {
                if(clic)
                {
                    clicked.add(tileGrille);
                }
                else
                {
                    clicked.removeAll(clicked);
                    clicked.add(tileGrille);
                }
            }

            if(tileGrille != null && gestion_monstres)
            {
                tileGrille.setMonsterHolder(!tileGrille.isMonsterHolder());
            }

        }*/
        /*if(button == 1)
        {
            for(int i = 0; i < calqueSelection.getGrille().size(); i++)
            {
                for(int j = 0; j <calqueSelection.getGrille().get(i).size(); j++)
                {
                    if(calqueSelection.getGrille().get(i).get(j).isPointed(x, y))
                    {
                        if(calqueSelection.getGrille().get(i).get(j).getState() == Tile.NONE)
                        {
                            calqueSelection.getGrille().get(i).get(j).setState(Tile.CLICKED);
                            selection.add(new Vector2f(calqueSelection.getGrille().get(i).get(j).getPos_x_real(), calqueSelection.getGrille().get(i).get(j).getPos_y_real()));
                        }

                        DialogLayout panel;

                        panel = new DialogLayout();
                        panel.setTheme("login-panel");
                        panel.setSize(150, 150);

                        Label calque = new Label("Calque : 1");

                        Label type = new Label("Type : " + calque1.getGrille().get(i).get(j).getType().getNom());
                        Label collidable = new Label("Collidable : " + calque1.getGrille().get(i).get(j).getType().isCollidable());

                        Button bouton = new Button();
                        bouton.setTheme("fleche_droite");



                        panel.setHorizontalGroup(panel.createParallelGroup()
                                .addGroup(panel.createSequentialGroup(calque))
                                .addGroup(panel.createSequentialGroup(type))
                                .addGroup(panel.createSequentialGroup(collidable)));
                        panel.setVerticalGroup(panel.createSequentialGroup()
                                .addWidget(calque)
                                .addWidget(type)
                                .addWidget(collidable));

                        panel.setPosition(
                                x, y);

                        gui_manager.getRoot().add(panel);
                    }
                }
            }
        }*/
    }

    /*private void nouvelle_map(GameContainer gc)
    {
        final Fenetre fen = new Fenetre(new Vector2f(200,50), new Vector2f(500,400));
        gui_list.add(fen);

        fen.setTitre("Nouvelle Map");
        fen.add("Entrez ici le nom de votre map :", new Vector2f(70, 100));

        TextField nomField = new TextField(new Vector2f(0,0), new Vector2f(350,25), gc);
        fen.add(nomField,new Vector2f(75,120));
        nomField.setText("map_1");

        fen.add("Entrez maintenant la taille de votre map :", new Vector2f(75, 140));
        final TextField xField = new TextField(new Vector2f(0,0), new Vector2f(50,25), gc);
        fen.add(xField,new Vector2f(175,190));
        xField.setText("16");
        fen.add("X", new Vector2f(245,157));
        final TextField yField = new TextField(new Vector2f(0,0), new Vector2f(50,25), gc);
        fen.add(yField,new Vector2f(275,190));
        yField.setText("14");

        Button okB = new Button(new Vector2f(0,0), new Vector2f(0, 0), "OK");
        fen.add(okB, new Vector2f(200, 280));
        okB.addActionListener(ActionListener.MOUSE_PRESSED, new ActionListener() {

            @Override
            public void actionPerformed()
            {

                tiles = new ArrayList<ArrayList<Tile>>();
                for(int i = 0; i < Integer.parseInt(xField.getText()); i++)
                {
                    tiles.add(new ArrayList<Tile>());
                    for(int j = 0; j < Integer.parseInt(yField.getText()); j++)
                    {
                        try {
                            tiles.get(i).add(new Tile(i, j, new Type_tile("herbe", new Image("data/tiles/herbe/tile_herbe.png"), false, 1)));
                        } catch (SlickException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

                calque1 = new Map(tiles);

                calqueSelection = calque1;

                fen.setVisible(false);

            }
        });



    }

    private void modifier_propriete(final ArrayList<Tile> tiles, float x, float y)
    {
        final Fenetre fen = new Fenetre(new Vector2f(300, 375/2), new Vector2f(400,300));
        gui_list.add(fen);

        String type = null;
        boolean collidable = false;
        for(int i = 0; i < calqueSelection.getGrille().size(); i++)
        {
            for(int j = 0; j <calqueSelection.getGrille().get(i).size(); j++)
            {
                if(calqueSelection.getGrille().get(i).get(j).getState() == Tile.CLICKED)
                {
                    type = calqueSelection.getGrille().get(i).get(j).getType().getNom();
                    collidable = calqueSelection.getGrille().get(i).get(j).getType().isCollidable();
                }
            }
        }
        fen.setTitre("Propriétés");
        fen.add("Type :", new Vector2f(75,105));
        final TextField typeField = new TextField(new Vector2f(125,100), new Vector2f(175,25), gui_context.getGc());
        if(type!=null)
            typeField.setText(type);
        else
            typeField.setText("rien");
        fen.add(typeField, typeField.getPos());

        Button annuler = new Button(new Vector2f(50,220), new Vector2f(100,30), "Annuler");
        fen.add(annuler, annuler.getPos());
        Button appliquer = new Button(new Vector2f(200,220), new Vector2f(100,30), "Appliquer");
        fen.add(appliquer, appliquer.getPos());

        annuler.addActionListener(ActionListener.MOUSE_PRESSED, new ActionListener() {
            @Override
            public void actionPerformed() {
                fen.setVisible(false);

            }
        });

        appliquer.addActionListener(ActionListener.MOUSE_PRESSED, new ActionListener() {
            @Override
            public void actionPerformed()
            {

                            String path = null;
                            for(int k = 0; k < racine.getChildren("type").size(); k++)
                            {
                                if(((Element)racine.getChildren("type").get(k)).getChild("nom").getText().equals(typeField.getText()))
                                {
                                    path = ((Element)racine.getChildren("type").get(k)).getChild("img").getText();
                                }
                            }

                            try {
                                for(int i = 0; i< tiles.size(); i++)
                                {
                                    tiles.get(i).setType(new Type_tile(typeField.getText(), new Image(path), false, 1));
                                    calque1.getGrille().get(tiles.get(i).getPos_x()).set(tiles.get(i).getPos_y(), tiles.get(i));
                                }

                            }catch (SlickException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }


                fen.setVisible(false);

            }
        });

    }*/

}
