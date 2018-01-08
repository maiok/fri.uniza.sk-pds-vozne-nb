package entities;

public class Suciastka {

    private int id;
    private Integer idTypu;
    private Integer idDodavatela;
    private double cena;

    public Suciastka() {
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

    public Integer getIdDodavatela() {
        return idDodavatela;
    }

    public void setIdDodavatela(Integer idDodavatela) {
        this.idDodavatela = idDodavatela;
    }

    public double getCena() {
        return cena;
    }

    public void setCena(double cena) {
        this.cena = cena;
    }
}
