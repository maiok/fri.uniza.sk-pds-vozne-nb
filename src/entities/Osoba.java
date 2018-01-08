package entities;

import java.sql.Date;

public class Osoba {

    private String rodCislo;
    private String meno;
    private String priezvisko;
    private Date datNarodenia;
    private String adresaOsoby;
    private String kontaktOsoby;

    public Osoba() {
    }

    public String getRodCislo() {
        return rodCislo;
    }

    public void setRodCislo(String rodCislo) {
        this.rodCislo = rodCislo;
    }

    public String getMeno() {
        return meno;
    }

    public void setMeno(String meno) {
        this.meno = meno;
    }

    public String getPriezvisko() {
        return priezvisko;
    }

    public void setPriezvisko(String priezvisko) {
        this.priezvisko = priezvisko;
    }

    public java.sql.Date getDatNarodenia() {
        return datNarodenia;
    }

    public void setDatNarodenia(java.sql.Date datNarodenia) {
        this.datNarodenia = datNarodenia;
    }

    public String getAdresaOsoby() {
        return adresaOsoby;
    }

    public void setAdresaOsoby(String adresaOsoby) {
        this.adresaOsoby = adresaOsoby;
    }

    public String getKontaktOsoby() {
        return kontaktOsoby;
    }

    public void setKontaktOsoby(String kontaktOsoby) {
        this.kontaktOsoby = kontaktOsoby;
    }

}
