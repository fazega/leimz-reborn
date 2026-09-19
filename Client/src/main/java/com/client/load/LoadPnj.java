package com.client.load;

import com.client.entities.Orientation;
import com.client.entities.PNJ;
import com.client.map.managers.MapManager;

/**
 *
 * @author fazega
 * Classe chargeant les informations de classe et de race du joueur (sort, competences ...)
 */
public class LoadPnj
{

    public static PNJ loadPnj(String str)
    {
        PNJ pnj;
        String[] args_pnj = str.split(";");
        pnj = new PNJ(
                    args_pnj[2],
                    null,
                    Orientation.BAS,
                    MapManager.instance.getEntire_map().getGrille()[Integer.parseInt(args_pnj[0])][Integer.parseInt(args_pnj[1])]);
        return pnj;
    }
}
