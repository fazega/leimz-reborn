package com.server.core;

import java.sql.ResultSet;


import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.gameplay.items.Equipement;
import com.gameplay.items.ObjetQuete;
import com.gameplay.items.SimpleItem;
import com.server.gameplay.PNJ_discours;
import com.server.gameplay.QueteObjectif;
import com.server.map.Map;
import com.server.map.Tile;
import com.server.map.managers.MapManager;
import com.server.entities.Orientation;
import com.server.entities.PNJ;
import com.server.entities.managers.EntitiesManager;
import com.server.entities.managers.PNJsManager;
import com.server.gameplay.Quete;
import com.server.gameplay.managers.ItemsManager;

public class Load
{
    @SuppressWarnings("unused")
    private MapManager mapmanager;
    @SuppressWarnings("unused")
    private EntitiesManager entitiesmanager;

    public Load()
    {
        run();
    }

    public void run()
    {
        System.out.println("Loading des donnees en cours (map, entites) ...");
        loadItems();
        loadMap();
        loadEntities();
        System.out.println("Loading termine.");
    }

    private void loadItems()
    {
        new ItemsManager();
        ResultSet rs;
        try {
            String sql = "SELECT item.nom, item.description, item.type, item.categorie, item.id " +
                    "FROM item,inventaire ";
            Statement stmt = ServerSingleton.getInstance().getDbConnexion().getConnexion().createStatement();
            rs = stmt.executeQuery(sql);
            while(rs.next())
            {
                SimpleItem item = null;
                if(rs.getString("categorie").equals("équipement"))
                    item = new Equipement(rs.getString("item.nom"), rs.getString("item.type"), rs.getString("item.description"), null, 0, rs.getInt("item.id"));
                else if(rs.getString("categorie").equals("objet_quete"))
                    item = new ObjetQuete(rs.getString("item.nom"), rs.getString("item.description"), null, 0, rs.getInt("item.id"));
                ItemsManager.instance.getItems().add(item);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadEntities()
    {
        entitiesmanager = new EntitiesManager();
        loadPnjs();
        loadMonsters();
    }

    private void loadPnjs()
    {
        PNJsManager pnjs_manager= new PNJsManager();

        ResultSet rs;
        try {
            String sql = "SELECT pnj.pos_x, pnj.pos_y, pnj.nom, pnj_discours.discours, pnj_discours.id, pnj_discours.id_objectif_en_cours " +
                    "FROM pnj, pnj_discours " +
                    "WHERE pnj.nom=pnj_discours.nom_pnj " +
                    "AND pnj_discours.after_answer IS NULL";
            Statement stmt = ServerSingleton.getInstance().getDbConnexion().getConnexion().createStatement();
            rs = stmt.executeQuery(sql);
            while(rs.next())
            {
                PNJ_discours discours = new PNJ_discours(rs.getString("pnj_discours.discours"), null, new ArrayList<PNJ_discours>(), rs.getInt("pnj_discours.id"), rs.getInt("pnj_discours.id_objectif_en_cours"));
                discours.setReponses(getPnjAnswer(discours, rs.getInt("pnj_discours.id"), 0));
                PNJ p = pnjs_manager.getPnj(rs.getString("pnj.nom"));
                if(p!=null)
                {
                    p.getDiscours().add(discours);
                }
                else
                {
                    pnjs_manager.add(
                            new PNJ(rs.getString("pnj.nom"),
                                    discours,
                                    Orientation.BAS,
                                    MapManager.instance.getEntire_map().getGrille()[rs.getInt("pnj.pos_x")][rs.getInt("pnj.pos_y")])
                                    );
                }
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        EntitiesManager.instance.setPnjs_manager(pnjs_manager);
        for(PNJ pnj : EntitiesManager.instance.getPnjs_manager().getPnjs())
        {
            for(Integer id : pnj.getQuetes().keySet())
            {
                for(QueteObjectif obj : pnj.getQuetes().get(id).getObjectifs())
                {
                    obj.initObj();
                }
            }
        }

    }


    private ArrayList<PNJ_discours> getPnjAnswer(PNJ_discours parent, int id_discours, int depth) throws SQLException {
        ArrayList<PNJ_discours> list = new ArrayList<PNJ_discours>();

        ResultSet rs;
        String sql = "SELECT pnj_discours.discours, pnj_discours.id, pnj_discours.id_objectif_en_cours " +
                "FROM pnj_discours, pnj " +
                "WHERE pnj.nom=pnj_discours.nom_pnj "+
                "AND pnj_discours.after_answer=" + id_discours;
        Statement stmt = ServerSingleton.getInstance().getDbConnexion().getConnexion().createStatement();
        rs = stmt.executeQuery(sql);
        depth++;
        while(rs.next())
        {
            PNJ_discours d = new PNJ_discours(rs.getString("pnj_discours.discours"), parent, new ArrayList<PNJ_discours>(), rs.getInt("pnj_discours.id"), null, rs.getInt("pnj_discours.id_objectif_en_cours"));
            d.setReponses(getPnjAnswer(d,rs.getInt("pnj_discours.id"), depth));
            list.add(d);

            ResultSet rs_quete;
            String sql_quete = "SELECT quete.nom, quete.id, quetes_objectifs.description, quetes_objectifs.type, quetes_objectifs.objectif, quetes_objectifs.id_ordre, quetes_objectifs.id " +
                        "FROM quete, quetes_objectifs, quetes_pnj_discours " +
                        "WHERE quete.id=quetes_pnj_discours.id_quete "+
                        "AND "+rs.getInt("pnj_discours.id")+"=quetes_pnj_discours.id_discours " +
                        "AND quetes_objectifs.id_quete = quete.id ";
            Statement stmt_quete = ServerSingleton.getInstance().getDbConnexion().getConnexion().createStatement();
            rs_quete = stmt_quete.executeQuery(sql_quete);
            Quete quete = null;
            boolean defined = false;
            while(rs_quete.next())
            {
                if(!defined)
                {
                    quete = new Quete(rs_quete.getInt("quete.id"),null);
                    quete.setNom(rs_quete.getString("quete.nom"));
                    defined = true;
                }
                QueteObjectif obj = new QueteObjectif(rs_quete.getString("quetes_objectifs.description"), rs_quete.getString("quetes_objectifs.type"), rs_quete.getString("quetes_objectifs.objectif"), rs_quete.getInt("quetes_objectifs.id"));
                ResultSet rs_recomp;
                String sql_recomp = "SELECT quetes_objectifs_recompenses.id_objectif, quetes_objectifs_recompenses.id_item, quetes_objectifs_recompenses.argent  " +
                            "FROM quetes_objectifs, quetes_objectifs_recompenses " +
                            "WHERE quetes_objectifs_recompenses.id_objectif = "+rs_quete.getInt("quetes_objectifs.id");
                Statement stmt_recomp = ServerSingleton.getInstance().getDbConnexion().getConnexion().createStatement();
                rs_recomp = stmt_recomp.executeQuery(sql_recomp);
                while(rs_recomp.next())
                {
                    obj.getRecompenses().add(ItemsManager.instance.getItem(rs_recomp.getInt("quetes_objectifs_recompenses.id_item")));
                }
                quete.getObjectifs().add(obj);
            }
            d.setQuete(quete);
        }
        return list;
    }

    private void loadMonsters()
    {

    }

    private void loadMap()
    {
        Map entire_map = null;
        Tile[][] grille = null;
        ResultSet rs;
        //Chargement des informations de la map
        try {
            int max_x = 0, max_y = 0;
            Statement stmt = ServerSingleton.getInstance().getDbConnexion().getConnexion().createStatement();
            String sql = "SELECT MAX(map.x), MAX(map.y)" +
                    "FROM map ";
            rs = stmt.executeQuery(sql);
            rs.next();
            max_x = rs.getInt(1);
            max_y = rs.getInt(2);
            grille=new Tile[max_x+1][max_y+1];
            for(int i = 0; i < max_x+1 ; i++)
            {
                for(int j = 0; j < max_y+1; j++)
                {
                    grille[i][j]=new Tile(i,j);
                }
            }
            sql = "SELECT x, y, collidable " +
                    "FROM map ";
            rs = stmt.executeQuery(sql);
            while(rs.next())
            {
                grille[rs.getInt("map.x")][rs.getInt("map.y")].setCollidable(rs.getBoolean("collidable"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        entire_map = new Map(grille, null);


        mapmanager = new MapManager(entire_map);
    }

}
