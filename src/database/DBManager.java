package database;

import constants.AppConstants;
import entities.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBManager {

    private Connection conn;
    private Statement stmt;
    private PreparedStatement ps;
    private String query = "";

    // zoznamy objektov
    // Osoba
    private ArrayList<Osoba> arrOsoby = new ArrayList<>();

    public DBManager() {
    }

    public void connect() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(AppConstants.DB_CONNECTION_STRING, AppConstants.DB_USER, AppConstants.DB_PASSW);
            stmt = conn.createStatement();
            System.out.println("Prihlasenie do DB prebehlo uspesne.");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.toString());
        }
    }

    // VYSTUPY ******************************************************************************************************************
    public ResultSet select1(int kriterium, String podmienka, String podmienka2) {
        try {
            query = "select id_vozna, typ_vozna, vlastnik.nazov_spolocnosti, vyrobca.nazov_spolocnosti, nazov_stanice, vozen.datum_nadobudnutia\n"
                    + "from vozen\n"
                    + "join typ_vozna using(id_typu_vozna)\n"
                    + "join spolocnost vlastnik on (vozen.id_vlastnika = vlastnik.id_spolocnosti)\n"
                    + "join spolocnost vyrobca on (vozen.id_vyrobcu = vyrobca.id_spolocnosti)\n"
                    + "join stanica on (vozen.id_domovskej_stanice = stanica.id_stanice)\n"
                    + "where ";
            switch (kriterium) {
                case 0:
                    query = query + "id_typu_vozna = " + podmienka;
                    break;
                case 1:
                    query = query + "vlastnik.nazov_spolocnosti = '" + podmienka+"'";
                    break;
                case 2:
                    query = query + "datum_nadobudnutia >= to_date('" + podmienka + "','DD/MM/YYYY')";
                    break;
                case 3:
                    query = "select id_vozna,odpis_percenta, cena,\n"
                            + "func_aktCena(cena, FLOOR(months_between(sysdate,datum_nadobudnutia)/12), odpis_percenta) \n"
                            + "from vozen join typ_vozna using(id_typu_vozna)\n"
                            + "where func_aktCena(cena, FLOOR(months_between(sysdate,datum_nadobudnutia)/12), odpis_percenta) >= " + podmienka + " \n"
                            + "and func_aktCena(cena, FLOOR(months_between(sysdate,datum_nadobudnutia)/12), odpis_percenta) <= " + podmienka2;
                    break;
            }
            ps = conn.prepareStatement(query);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet select2(String stanica, String dat_od, String dat_do) {
        try {
            query = "select id_vozna from vozen_stanica\n"
                    + "join stanica using(id_stanice)\n"
                    + "where nazov_stanice = '" + stanica + "'\n"
                    + "and v_stanici_od >= to_date('" + dat_od + "','DD/MM/YYYY') or v_stanici_do <= to_date('" + dat_do + "','DD/MM/YYYY')";

            ps = conn.prepareStatement(query);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int select2cena(String stanica, String dat_od, String dat_do) {
        try {
            CallableStatement proc = conn.prepareCall("{? = call func_stavMajetku(" + stanica + ",to_date(" + dat_od + ",'DD/MM/YYYY'),to_date(" + dat_do + ",'DD/MM/YYYY'))}");
            proc.execute();
            int cena = proc.getInt(1);

            return cena;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public ResultSet select3(int kriterium, String spolocnost) {
        try {
            switch (kriterium) {
                case 0:
                    query = "select id_typu_vozna, count(*) from vozen\n"
                            + "group by id_typu_vozna\n"
                            + "order by id_typu_vozna";
                    break;
                case 1:
                    query = "select id_typu_vozna, count(*), vlastnik.nazov_spolocnosti from vozen\n"
                            + "join spolocnost vlastnik on (vlastnik.id_spolocnosti = vozen.id_vlastnika)\n"
                            + "where vlastnik.nazov_spolocnosti = '" + spolocnost + "'\n"
                            + "group by vlastnik.nazov_spolocnosti, id_typu_vozna";
                    break;
            }

            ps = conn.prepareStatement(query);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet select4(int kriterium, String id_vozna) {
        try {
            query = "select XMLROOT(\n"
                    + "XMLELEMENT(\"VOZEN\",\n"
                    + "    XMLELEMENT(\"Informacie_o_vozni\",\n"
                    + "    XMLFOREST(ID_VOZNA as \"id_vozna\",\n"
                    + "              vz.ID_TYPU_VOZNA as \"typ_vozna\",\n"
                    + "              vz.ID_VLASTNIKA as \"vlastnik_vozna\",\n"
                    + "              vz.ID_VYROBCU as \"vyrobca_vozna\",\n"
                    + "              vz.ID_DOMOVSKEJ_STANICE as \"domovska_stanica\",\n"
                    + "              vz.DATUM_NADOBUDNUTIA as \"zakupeny\")\n"
                    + "    ),XMLELEMENT(\"KONTORLY\",\n"
                    + "    XMLAGG(\n"
                    + "    XMLELEMENT(\"KONTROLA\", XMLATTRIBUTES(id_kontroly as \"id\"),\n"
                    + "    XMLFOREST(kontrola.ID_TYPU_KONTROLY as \"id_typu_kontroly\",\n"
                    + "              kontrola.POPIS_KONTROLY as \"popis_kontroly\"\n"
                    + "              ))\n"
                    + "    )\n"
                    + "    ),XMLELEMENT(\"OPRAVY\",\n"
                    + "              XMLAGG(\n"
                    + "              XMLELEMENT(\"OPRAVA\",XMLATTRIBUTES(id_opravy as \"id\"),\n"
                    + "              XMLFOREST(ID_KONTROLY as \"id_kontroly\",\n"
                    + "                        oprava.ID_TYPU_OPRAVY as \"id_typu_opravy\",\n"
                    + "                        oprava.POPIS_OPRAVY as \"popis_opravy\"\n"
                    + "              ))\n"
                    + "              )),\n"
                    + "              XMLELEMENT(\"VYRADENIE\", XMLFOREST(\n"
                    + "              datum_vyradenia as \"datum_vyradenia\",\n"
                    + "              vyradeny_vozen.POPIS_VYRADENIA as \"popis_vyradenia\"))\n"
                    + "              ),version '1.0') as XML\n"
                    + "from vozen vz\n"
                    + "left join kontrola using(id_vozna)\n"
                    + "LEFT join oprava using(id_kontroly)\n"
                    + "left join vyradeny_vozen using(id_vozna)\n"
                    + "where id_vozna = " + id_vozna + "\n"
                    + "group by ID_VOZNA,ID_TYPU_VOZNA,ID_VLASTNIKA,ID_VYROBCU,ID_DOMOVSKEJ_STANICE,DATUM_NADOBUDNUTIA,datum_vyradenia,POPIS_VYRADENIA";

            ps = conn.prepareStatement(query);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<ResultSet> select5(String id_vozna) {
        try {
            ArrayList<ResultSet> arrRS = new ArrayList<>();

            query = "select id_vozna, to_char(oprava_od, 'MM') as mesiac, sum(cena_suciastky) as cena \n"
                    + "from vozen\n"
                    + "join kontrola using(id_vozna)\n"
                    + "join oprava using (id_kontroly)\n"
                    + "join oprava_suciastka using(id_opravy)\n"
                    + "join suciastka using(id_suciastky)\n"
                    + "where id_vozna = " + id_vozna + "\n"
                    + "group by id_vozna, to_char(oprava_od, 'MM')\n"
                    + "order by id_vozna, to_char(oprava_od, 'MM')";
            ps = conn.prepareStatement(query);
            arrRS.add(ps.executeQuery());

            query = "select id_vozna,to_char(oprava_od, 'MM') as mesiac, sum(cena_opravy) as cena \n"
                    + "from vozen\n"
                    + "join kontrola using(id_vozna)\n"
                    + "join oprava using (id_kontroly)\n"
                    + "join oprava_suciastka using(id_opravy)\n"
                    + "join typ_opravy using(id_typu_opravy)\n"
                    + "where id_vozna = " + id_vozna + "\n"
                    + "group by id_vozna, to_char(oprava_od, 'MM')\n"
                    + "order by id_vozna, to_char(oprava_od, 'MM')";
            ps = conn.prepareStatement(query);
            arrRS.add(ps.executeQuery());

            query = "Select id_vozna,to_char(oprava_od, 'MM') as mesiac, sum(cena_kontroly) as cena\n"
                    + "from vozen\n"
                    + "join kontrola using(id_vozna)\n"
                    + "join typ_kontroly using(id_typu_kontroly)\n"
                    + "join oprava using(id_kontroly)\n"
                    + "join oprava_suciastka using(id_opravy)\n"
                    + "where id_vozna = " + id_vozna + "\n"
                    + "group by id_vozna, to_char(oprava_od, 'MM')\n"
                    + "order by id_vozna, to_char(oprava_od, 'MM')";
            ps = conn.prepareStatement(query);
            arrRS.add(ps.executeQuery());

            return arrRS;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<ResultSet> select6(String id_vozna) {
        try {
            ArrayList<ResultSet> arrRS = new ArrayList<>();

            query = "select id_typu_vozna,to_char(oprava_od, 'MM') as mesiac, sum(cena_suciastky) as cena \n"
                    + "from vozen\n"
                    + "join kontrola using(id_vozna)\n"
                    + "join oprava using (id_kontroly)\n"
                    + "join oprava_suciastka using(id_opravy)\n"
                    + "join suciastka using(id_suciastky)\n"
                    + "group by id_typu_vozna,to_char(oprava_od, 'MM')\n"
                    + "order by id_typu_vozna,to_char(oprava_od, 'MM')";
            ps = conn.prepareStatement(query);
            arrRS.add(ps.executeQuery());

            query = "select id_typu_vozna,to_char(oprava_od, 'MM') as mesiac, sum(cena_opravy) as cena \n"
                    + "from vozen\n"
                    + "join kontrola using(id_vozna)\n"
                    + "join oprava using (id_kontroly)\n"
                    + "join oprava_suciastka using(id_opravy)\n"
                    + "join typ_opravy using(id_typu_opravy)\n"
                    + "group by id_typu_vozna,to_char(oprava_od, 'MM')\n"
                    + "order by id_typu_vozna,to_char(oprava_od, 'MM')";
            ps = conn.prepareStatement(query);
            arrRS.add(ps.executeQuery());

            query = "Select id_typu_vozna,to_char(oprava_od, 'MM') as mesiac, sum(cena_kontroly) as cena\n"
                    + "from vozen\n"
                    + "join kontrola using(id_vozna)\n"
                    + "join typ_kontroly using(id_typu_kontroly)\n"
                    + "join oprava using(id_kontroly)\n"
                    + "join oprava_suciastka using(id_opravy)\n"
                    + "group by id_typu_vozna,to_char(oprava_od, 'MM')\n"
                    + "order by id_typu_vozna,to_char(oprava_od, 'MM')";
            ps = conn.prepareStatement(query);
            arrRS.add(ps.executeQuery());

            return arrRS;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet select7(int kriterium, String id, String id2) {
        try {
            query = "select * from(\n"
                    + "select rank() over(order by count(id_opravy) desc) as poradie, count(id_opravy),\n"
                    + "id_vozna\n"
                    + "from kontrola\n"
                    + "join oprava using(id_kontroly)\n"
                    + "join vozen using(id_vozna)\n"
                    + "join oprava_suciastka using(id_opravy)\n"
                    + "join typ_opravy using(id_typu_opravy)\n"
                    + "where ";
            switch (kriterium) {
                case 0:
                    query = query + "id_typu_vozna = " + id + "\n"
                            + "group by id_vozna\n"
                            + ") where poradie <=3";
                    break;
                case 1:
                    query = query + "id_vlastnika = " + id + "\n"
                            + "group by id_vozna\n"
                            + ") where poradie <=3";
                    break;
                case 2:
                    query = query + "id_vyrobcu = " + id + "\n"
                            + "group by id_vozna\n"
                            + ") where poradie <=3";
                    break;
                case 3:
                    query = query + "id_typu_opravy = " + id + "\n"
                            + "group by id_vozna\n"
                            + ") where poradie <=3";
                    break;
                case 4:
                    query = query + "oprava_od <= to_date('" + id2 + "','DD/MM/YYYY') and oprava_do >= to_date('" + id + "','DD/MM/YYYY')\n"
                            + "group by id_vozna\n"
                            + ") where poradie <=3";
                    break;
            }

            ps = conn.prepareStatement(query);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet select8() {
        try {
            query = "select v.id_vozna, v.id_vlastnika\n"
                    + "from vozen v\n"
                    + "join kontrola k on (v.id_vozna = k.id_vozna)\n"
                    + "join kontrola_zamestnanec kz on (k.id_kontroly = kz.id_kontroly)\n"
                    + "where kz.kontrola_od >= v.datum_nadobudnutia \n"
                    + "and kz.kontrola_od <= add_months(v.datum_nadobudnutia,60) \n"
                    + "and not exists (\n"
                    + "    select 'x' from oprava o\n"
                    + "    where o.id_kontroly = k.id_kontroly\n"
                    + ")\n"
                    + "group by v.id_vlastnika, v.id_vozna";

            ps = conn.prepareStatement(query);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet select9() {
        try {
            query = "Select id_vozna\n"
                    + "from vozen \n"
                    + "join kontrola using(id_vozna)\n"
                    + "join kontrola_zamestnanec using(id_kontroly)\n"
                    + "where kontrola_od >= sysdate and kontrola_od <= add_months(sysdate,12)";

            ps = conn.prepareStatement(query);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<ResultSet> select10() {
        try {
            ArrayList<ResultSet> arrRS = new ArrayList<>();

            query = "Select id_vozna, sum(cena_suciastky) as suma_za_opravy\n"
                    + "from kontrola\n"
                    + "left join oprava using (id_kontroly)\n"
                    + "left join oprava_suciastka using(id_opravy)\n"
                    + "left join suciastka using(id_suciastky)\n"
                    + "left join typ_suciastky using(id_typu_suciastky)\n"
                    + "group by id_vozna\n"
                    + "order by id_vozna";
            ps = conn.prepareStatement(query);
            arrRS.add(ps.executeQuery());

            query = "select id_vozna, sum(cena_opravy) from kontrola \n"
                    + "left join oprava using (id_kontroly)\n"
                    + "left join typ_opravy using(id_typu_opravy)\n"
                    + "group by id_vozna\n"
                    + "order by id_vozna";
            ps = conn.prepareStatement(query);
            arrRS.add(ps.executeQuery());

            query = "Select id_vozna, sum(cena_kontroly)\n"
                    + "from vozen\n"
                    + "join kontrola using(id_vozna)\n"
                    + "join typ_kontroly using(id_typu_kontroly)\n"
                    + "group by id_vozna\n"
                    + "order by id_vozna";
            ps = conn.prepareStatement(query);
            arrRS.add(ps.executeQuery());

            return arrRS;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String select11(String id, String dat_od, String dat_do) {
        try {
            CallableStatement proc = conn.prepareCall("{? = call func11(" + id + ",to_date('" + dat_od + "','DD/MM/YYYY'),to_date('" + dat_do + "','DD/MM/YYYY'))}");
            proc.registerOutParameter(1, Types.VARCHAR);
            proc.execute();
            String vysledok = proc.getString(1);

            return vysledok;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet select12(String id_vlastnika, String popis_vyradenia) {
        try {
            query = "select * from VYRADENY_VOZEN\n"
                    + "join vozen using(id_vozna)\n"
                    + "join spolocnost m on(vozen.id_vlastnika = m.id_spolocnosti)\n"
                    + "where nazov_spolocnosti = '" + id_vlastnika + "'\n"
                    + "and popis_vyradenia is " + popis_vyradenia + "";

            ps = conn.prepareStatement(query);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet select13(String datum) {
        try {
            query = "select id_vlastnika, count(id_vozna)\n"
                    + "from vozen\n"
                    + "where datum_nadobudnutia >= to_date('" + datum + "','DD/MM/YYYY')\n"
                    + "and not exists (\n"
                    + "     select 'x' from vyradeny_vozen\n"
                    + "     where vyradeny_vozen.id_vozna = vozen.id_vozna\n"
                    + ")\n"
                    + "group by id_vlastnika\n"
                    + "order by id_vlastnika";

            ps = conn.prepareStatement(query);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet select14(int kriterium) {
        try {
            switch (kriterium) {
                case 0:
                    query = "select id_vlastnika, id_domovskej_stanice, count(id_vozna)\n"
                            + "from vozen\n"
                            + "group by id_vlastnika, id_domovskej_stanice\n"
                            + "having count(id_vozna)=(\n"
                            + "    select max(count(id_vozna)) from vozen group by id_vlastnika, id_domovskej_stanice\n"
                            + ")";
                    break;
                case 1:
                    query = "select * from(\n"
                            + "select rank() over(order by sum(func_aktCena(cena, FLOOR(months_between(sysdate,datum_nadobudnutia)/12), odpis_percenta)) desc) as poradie, id_vlastnika, id_domovskej_stanice,  sum(func_aktCena(cena, FLOOR(months_between(sysdate,datum_nadobudnutia)/12), odpis_percenta))\n"
                            + "from vozen\n"
                            + "join typ_vozna using(id_typu_vozna)\n"
                            + "group by id_vlastnika, id_domovskej_stanice)\n"
                            + "where poradie <= 1";
                    break;
            }

            ps = conn.prepareStatement(query);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet select15(int kriterium, String id) {
        try {
            switch (kriterium) {
                case 0:
                    query = "select rod_cislo, meno, priezvisko, count(id_opravy) \n"
                            + "from zamestnanec\n"
                            + "join osoba using(rod_cislo)\n"
                            + "join oprava_suciastka using(id_zamestnanca)\n"
                            + "group by rod_cislo, meno, priezvisko\n"
                            + "order by count(id_opravy) desc";
                    break;
                case 1:
                    query = "select rod_cislo, meno, priezvisko, count(id_opravy) \n"
                            + "from zamestnanec\n"
                            + "join osoba using(rod_cislo)\n"
                            + "join oprava_suciastka using(id_zamestnanca)\n"
                            + "where id_spolocnosti = " + id + "\n"
                            + "group by rod_cislo, meno, priezvisko\n"
                            + "order by count(id_opravy) desc";
                    break;
                case 2:
                    query = "select rod_cislo, meno, priezvisko, count(id_opravy) \n"
                            + "from zamestnanec\n"
                            + "join osoba using(rod_cislo)\n"
                            + "join oprava_suciastka using(id_zamestnanca)\n"
                            + "join oprava using(id_opravy)\n"
                            + "join kontrola using(id_kontroly)\n"
                            + "join vozen using(id_vozna)\n"
                            + "where it_typu_vozna = " + id + "\n"
                            + "group by rod_cislo, meno, priezvisko\n"
                            + "order by count(id_opravy) desc";
                    break;
            }

            ps = conn.prepareStatement(query);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public ResultSet selectBlob(String id){
        try {
            query = "select foto_vozna from typ_vozna\n"
                    + "where id_typu_vozna = "+id;
            ps = conn.prepareStatement(query);
            return ps.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    // VYSTUPY ******************************************************************************************************************
    public boolean insertOsoba(Osoba osoba) {

        query = "insert into osoba (rod_cislo, meno, priezvisko, dat_narodenia, adresa_osoby,"
                + "kontakt_osoby) values(?,?,?,?,?,?)";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, osoba.getRodCislo());
            ps.setString(2, osoba.getMeno());
            ps.setString(3, osoba.getPriezvisko());
            ps.setDate(4, osoba.getDatNarodenia());
            ps.setString(5, osoba.getAdresaOsoby());
            ps.setString(6, osoba.getKontaktOsoby());

            int response = ps.executeUpdate();
            // pokial vrati ine ako 0, insert sa vykonal
            if (response != 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertZamestnanec(Zamestnanec zam) {

        query = "insert into zamestnanec (rod_cislo, id_spolocnosti, datum_prijatia, datum_prepustenia) "
                + "values(?,?,?,?)";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, zam.getRodCislo());
            // Takto sa definuje FK typu Integer, pretoze narozdiel od typu int
            // Integer moze byt NULL
            ps.setObject(2, zam.getIdSpolocnosti(), Types.INTEGER);
            ps.setDate(3, zam.getDatumPrijatia());
            ps.setDate(4, zam.getDatumPrepustenia());

            int response = ps.executeUpdate();
            if (response != 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertSpolocnost(Spolocnost spolocnost) {

        query = "insert into spolocnost (id_typu_spolocnosti, nazov_spolocnosti, adresa_spolocnosti, kontakt_spolocnosti) "
                + "values(?,?,?,?)";
        try {
            ps = conn.prepareStatement(query);
            ps.setObject(1, spolocnost.getIdTypu(), Types.INTEGER);
            ps.setString(2, spolocnost.getNazov());
            ps.setString(3, spolocnost.getAdresa());
            ps.setString(4, spolocnost.getKontakt());

            int response = ps.executeUpdate();
            if (response != 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertVozen(Vozen vozen) {

        query = "insert into vozen (id_typu_vozna, id_vlastnika, id_vyrobcu, id_domovskej_stanice, datum_nadobudnutia) "
                + "values(?,?,?,?,?)";
        try {
            ps = conn.prepareStatement(query);
            ps.setObject(1, vozen.getIdTypuVozna(), Types.INTEGER);
            ps.setObject(2, vozen.getIdVlastnika(), Types.INTEGER);
            ps.setObject(3, vozen.getIdVyrobcu(), Types.INTEGER);
            ps.setObject(4, vozen.getIdDomovskejStanice(), Types.INTEGER);
            ps.setDate(5, vozen.getDatumNadobudnutia());

            int response = ps.executeUpdate();
            if (response != 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertVyradenyVozen(VyradenyVozen vyradenyVozen) {

        query = "insert into vyradeny_vozen (id_vozna, datum_vyradenia, popis_vyradenia) "
                + "values(?,?,?)";
        try {
            ps = conn.prepareStatement(query);
            ps.setObject(1, vyradenyVozen.getIdVozna(), Types.INTEGER);
            ps.setDate(2, vyradenyVozen.getDatumVyradenia());
            ps.setString(3, vyradenyVozen.getPopis());

            int response = ps.executeUpdate();
            if (response != 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertOprava(Oprava oprava) {

        query = "insert into oprava (id_kontroly, id_typu_opravy, popis_opravy) "
                + "values(?,?,?)";
        try {
            ps = conn.prepareStatement(query);
            ps.setObject(1, oprava.getIdKontroly(), Types.INTEGER);
            ps.setObject(2, oprava.getIdTypu(), Types.INTEGER);
            ps.setString(3, oprava.getPopis());

            int response = ps.executeUpdate();
            if (response != 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertSuciastka(Suciastka suciastka) {

        query = "insert into suciastka (id_typu_suciastky, id_dodavatela, cena_suciastky) "
                + "values(?,?,?)";
        try {
            ps = conn.prepareStatement(query);
            ps.setObject(1, suciastka.getIdTypu(), Types.INTEGER);
            ps.setObject(2, suciastka.getIdDodavatela(), Types.INTEGER);
            ps.setDouble(3, suciastka.getCena());

            int response = ps.executeUpdate();
            if (response != 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void generujBlobyTypVozna() {

        query = "update typ_vozna set foto_vozna = ? where id_typu_vozna = ?";

        try {
            for (int i = 1; i <= 11; i++) {
                PreparedStatement pstmt = conn.prepareStatement(query);
                File blob = new File(System.getProperty("user.dir") + "/bloby/blob_" + i + ".jpg");
                FileInputStream in = new FileInputStream(blob);
                pstmt.setBinaryStream(1, in, (int) blob.length());
                pstmt.setInt(2, i);
                pstmt.executeUpdate();
                pstmt.close();
            }
        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void generujHistoriuTypSuciastky() {

        query = "update typ_suciastky set historia_ceny = ? where id_typu_suciastky = ?";

        try {
            for (int i = 1; i <= 50; i++) {
                PreparedStatement pstmt = conn.prepareStatement(query);

                pstmt.setInt(2, i);

                pstmt.executeUpdate();
                pstmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet selectTypyVoznov() {

        query = "SELECT * FROM TYP_VOZNA";
        try {
            stmt = conn.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet selectDodavatelia() {

        query = "SELECT * FROM SPOLOCNOST JOIN TYP_SPOLOCNOSTI USING(id_typu_spolocnosti) WHERE TYP_SPOLOCNOSTI = \'Dodavatel\'";
        try {
            stmt = conn.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet selectSpolocnosti() {

        query = "SELECT * FROM SPOLOCNOST";
        try {
            stmt = conn.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet selectSpolocnotiPodlaNazvu(String nazov) {

        query = "SELECT * FROM spolocnost WHERE nazov_spolocnosti = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, nazov);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet selectTypVoznaPodlaTypu(String typ) {

        query = "SELECT * FROM TYP_VOZNA WHERE TYP_VOZNA = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, typ);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet selectStanice() {

        query = "SELECT * FROM STANICA";
        try {
            stmt = conn.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet selectStanicePodlaNazvu(String nazov) {

        query = "SELECT * FROM stanica WHERE nazov_stanice = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, nazov);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    len nevyradene vozne
     */
    public ResultSet selectVoznov() {

        query = "SELECT * FROM Vozen v where not exists (select 'x' from vyradeny_vozen vv where vv.id_vozna = v.id_vozna )";
        try {
            stmt = conn.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void odoberVozen(int idVozna, String popis) {

        VyradenyVozen vyradenyVozen = new VyradenyVozen();
        vyradenyVozen.setIdVozna(idVozna);
        vyradenyVozen.setDatumVyradenia(getTimestamp());
        vyradenyVozen.setPopis(popis);

        insertVyradenyVozen(vyradenyVozen);
    }

    public Date getTimestamp() {
        java.util.Date utilDate = new java.util.Date();
        return new java.sql.Date(utilDate.getTime());
    }

    public ResultSet selectOsoby() {

        query = "SELECT * FROM Osoba";
        try {
            stmt = conn.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet selectTypySuciastok() {

        query = "SELECT * FROM typ_suciastky";
        try {
            stmt = conn.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet selectTypuSuciastkyPodlaNazvu(String typSuciastky) {
        query = "SELECT * FROM typ_suciastky WHERE typ_suciastky = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, typSuciastky);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet selectTypyOprav() {

        query = "SELECT * FROM typ_opravy";
        try {
            stmt = conn.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet selectTypOpravyPodlaTypu(String typOpravy) {
        query = "SELECT * FROM typ_opravy WHERE typ_opravy = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, typOpravy);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet selectSuciastky() {

        query = "SELECT * FROM suciastka";
        try {
            stmt = conn.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet selectZamestnancov() {

        query = "SELECT * FROM zamestnanec order by id_zamestnanca";
        try {
            stmt = conn.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertKontrola(Kontrola kontrola) {
        query = "insert into kontrola (id_typu_kontroly, id_vozna, popis_kontroly) "
                + "values(?,?,?)";
        try {
            ps = conn.prepareStatement(query);
            ps.setObject(1, kontrola.getIdTypu(), Types.INTEGER);
            ps.setObject(2, kontrola.getIdVozna(), Types.INTEGER);
            ps.setString(3, kontrola.getPopis());

            int response = ps.executeUpdate();
            if (response != 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Integer getLastInsertedIdKontrola() {

        query = "SELECT * FROM kontrola order by id_kontroly desc";
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertOpravaSuciastky(OpravaSuciastky opravaSuciastky) {
        query = "insert into oprava_suciastka (id_opravy, id_zamestnanca, id_suciastky, oprava_od, oprava_do) "
                + "values(?,?,?,?,?)";
        try {
            ps = conn.prepareStatement(query);
            ps.setObject(1, opravaSuciastky.getIdOpravy(), Types.INTEGER);
            ps.setObject(2, opravaSuciastky.getIdZamestnanca(), Types.INTEGER);
            ps.setObject(3, opravaSuciastky.getIdSuciastky(), Types.INTEGER);
            ps.setDate(4, opravaSuciastky.getOpravaOd());
            ps.setDate(5, opravaSuciastky.getOpravaDo());

            int response = ps.executeUpdate();
            if (response != 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean insertKontrolaZamestnanec(KontrolaZamestnanec kz) {
        query = "insert into kontrola_zamestnanec (id_zamestnanca, id_kontroly, kontrola_od, kontrola_do) "
                + "values(?,?,?,?)";
        try {
            ps = conn.prepareStatement(query);
            ps.setObject(1, kz.getIdZamestnanca(), Types.INTEGER);
            ps.setObject(2, kz.getIdKontroly(), Types.INTEGER);
            ps.setDate(3, kz.getKontrolaOd());
            ps.setDate(4, kz.getKontrolaDo());

            int response = ps.executeUpdate();
            if (response != 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void insertServis(int vozenId, int idTypuOpravy, int suciastkaId, int zamestnanecId,
            Date datumOd, Date datumDo) {

        Kontrola kontrola = new Kontrola();
        kontrola.setIdVozna(vozenId);
        kontrola.setPopis("Servis");
        insertKontrola(kontrola);
        int idKontroly = getLastInsertedIdKontrola();

        Oprava oprava = new Oprava();
        oprava.setIdKontroly(idKontroly);
        oprava.setIdTypu(idTypuOpravy);
        insertOprava(oprava);

        OpravaSuciastky opravaSuc = new OpravaSuciastky();
        opravaSuc.setIdOpravy(idTypuOpravy);
        opravaSuc.setIdSuciastky(suciastkaId);
        opravaSuc.setIdZamestnanca(zamestnanecId);
        opravaSuc.setOpravaOd(datumOd);
        opravaSuc.setOpravaDo(datumDo);
        insertOpravaSuciastky(opravaSuc);

        KontrolaZamestnanec kz = new KontrolaZamestnanec();
        kz.setIdZamestnanca(zamestnanecId);
        kz.setIdKontroly(idKontroly);
        kz.setKontrolaOd(datumOd);
        kz.setKontrolaDo(datumDo);
        insertKontrolaZamestnanec(kz);
    }

}
