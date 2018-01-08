package entities;

import java.sql.Date;

public class Vozen {

    private int id;
    private Integer idTypuVozna;
    private Integer idVlastnika;
    private Integer idVyrobcu;
    private Integer idDomovskejStanice;
    private Date datumNadobudnutia;

    public Vozen() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getIdTypuVozna() {
        return idTypuVozna;
    }

    public void setIdTypuVozna(Integer idTypuVozna) {
        this.idTypuVozna = idTypuVozna;
    }

    public Integer getIdVlastnika() {
        return idVlastnika;
    }

    public void setIdVlastnika(Integer idVlastnika) {
        this.idVlastnika = idVlastnika;
    }

    public Integer getIdVyrobcu() {
        return idVyrobcu;
    }

    public void setIdVyrobcu(Integer idVyrobcu) {
        this.idVyrobcu = idVyrobcu;
    }

    public Integer getIdDomovskejStanice() {
        return idDomovskejStanice;
    }

    public void setIdDomovskejStanice(Integer idDomovskejStanice) {
        this.idDomovskejStanice = idDomovskejStanice;
    }

    public Date getDatumNadobudnutia() {
        return datumNadobudnutia;
    }

    public void setDatumNadobudnutia(Date datumNadobudnutia) {
        this.datumNadobudnutia = datumNadobudnutia;
    }
}
