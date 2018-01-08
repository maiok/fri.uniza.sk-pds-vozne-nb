package entities;

public class Oprava {

    private int id;
    private Integer idKontroly;
    private Integer idTypu;
    private String popis;

    public Oprava() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getIdKontroly() {
        return idKontroly;
    }

    public void setIdKontroly(Integer idKontroly) {
        this.idKontroly = idKontroly;
    }

    public Integer getIdTypu() {
        return idTypu;
    }

    public void setIdTypu(Integer idTypu) {
        this.idTypu = idTypu;
    }

    public String getPopis() {
        return popis;
    }

    public void setPopis(String popis) {
        this.popis = popis;
    }
}
