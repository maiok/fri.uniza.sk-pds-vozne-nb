
import database.DBManager;
import entities.Zamestnanec;
import java.sql.Date;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Mário Keméň
 */
public class appmain {
    public static void main(String[] args) {
        DBManager dbManager = new DBManager();
        dbManager.connect();

//        Zamestnanec zam = new Zamestnanec();
//        zam.setRodCislo("4948937478");
//        zam.setIdSpolocnosti(1);
//        zam.setDatumPrijatia(new Date(2005, 3, 25));
//        dbManager.insertZamestnanec(zam);

//        dbManager.generujBlobyTypVozna();
    }
}
