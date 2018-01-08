package entities;

public class Kontrola {

    private int id;
    private Integer idTypu;
    private Integer idVozna;
    private String popis;

    public Kontrola() {
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

    public Integer getIdVozna() {
        return idVozna;
    }

    public void setIdVozna(Integer idVozna) {
        this.idVozna = idVozna;
    }

    public String getPopis() {
        return popis;
    }

    public void setPopis(String popis) {
        this.popis = popis;
    }
}
