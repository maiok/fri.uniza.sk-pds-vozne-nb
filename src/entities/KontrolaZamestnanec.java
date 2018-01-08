package entities;

import java.sql.Date;

public class KontrolaZamestnanec {

    private Integer idZamestnanca;
    private Integer idKontroly;
    private Date kontrolaOd;
    private Date kontrolaDo;

    public KontrolaZamestnanec() {
    }

    public Integer getIdZamestnanca() {
        return idZamestnanca;
    }

    public void setIdZamestnanca(Integer idZamestnanca) {
        this.idZamestnanca = idZamestnanca;
    }

    public Integer getIdKontroly() {
        return idKontroly;
    }

    public void setIdKontroly(Integer idKontroly) {
        this.idKontroly = idKontroly;
    }

    public Date getKontrolaOd() {
        return kontrolaOd;
    }

    public void setKontrolaOd(Date kontrolaOd) {
        this.kontrolaOd = kontrolaOd;
    }

    public Date getKontrolaDo() {
        return kontrolaDo;
    }

    public void setKontrolaDo(Date kontrolaDo) {
        this.kontrolaDo = kontrolaDo;
    }
}
