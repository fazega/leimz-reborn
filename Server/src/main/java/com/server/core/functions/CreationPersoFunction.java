package com.server.core.functions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.server.core.Client;
import com.server.core.ServerSingleton;

public class CreationPersoFunction implements Functionable
{

    @Override
    public void doSomething(String[] args, Client c)
    {
        ResultSet rs = null;
        Statement stmt;
        try {
            stmt = ServerSingleton.getInstance().getDbConnexion().getConnexion().createStatement();
            rs = stmt.executeQuery("SELECT * FROM personnage WHERE name='"+args[1]+"'");
            if(!rs.next())
            {
                String query = "INSERT INTO personnage values('"+c.getCompte().getName()+"' , '"+args[2]
                        +"', 'barbare', '"+args[1]+"', 20, 'h', 20)";
                System.out.println(query);
                stmt.executeUpdate(query);

                stmt.executeUpdate("INSERT INTO caracteristiques_joueur values('"+args[1]+"' , 'deplacement', 10, 10)");
                stmt.executeUpdate("INSERT INTO caracteristiques_joueur values('"+args[1]+"' , 'dommages_cac', 10, 10)");
                stmt.executeUpdate("INSERT INTO caracteristiques_joueur values('"+args[1]+"' , 'dommages_magie', 10, 10)");
                stmt.executeUpdate("INSERT INTO caracteristiques_joueur values('"+args[1]+"' , 'endurance', 2500, 2500)");
                stmt.executeUpdate("INSERT INTO caracteristiques_joueur values('"+args[1]+"' , 'energie', 8000, 8000)");
                stmt.executeUpdate("INSERT INTO caracteristiques_joueur values('"+args[1]+"' , 'precision', 60, 60)");
                stmt.executeUpdate("INSERT INTO caracteristiques_joueur values('"+args[1]+"' , 'vie', 150, 150)");
            }

            else
                c.sendMessage("cp;ALREADY_EXIST");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Perso ajouté !");
    }

}
