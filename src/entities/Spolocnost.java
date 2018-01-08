package entities;

public class Spolocnost {

    private int id;
    private Integer idTypu;
    private String nazov;
    private String adresa;
    private String kontakt;

    public Spolocnost() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getIdTypu() {
        return idTypu;
    }

    public void setIdTypu(Integer idTypu) {
        this.idTypu = idTypu;
    }

    public String getNazov() {
        return nazov;
    }

    public void setNazov(String nazov) {
        this.nazov = nazov;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public String getKontakt() {
        return kontakt;
    }

    public void setKontakt(String kontakt) {
        this.kontakt = kontakt;
    }

}
