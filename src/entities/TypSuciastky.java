package entities;

import java.sql.Array;

public class TypSuciastky {

    private int id;
    private Array historiaCeny;
    private String typ;

    public TypSuciastky() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Array getHistoriaCeny() {
        return historiaCeny;
    }

    public void setHistoriaCeny(Array historiaCeny) {
        this.historiaCeny = historiaCeny;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }
}
