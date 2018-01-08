package entities;

import java.sql.Date;

public class VozenStanica {

    private Integer idVozna;
    private Integer idStanice;
    private Date vStaniciOd;
    private Date vStaniciDo;

    public VozenStanica() {
    }

    public Integer getIdVozna() {
        return idVozna;
    }

    public void setIdVozna(Integer idVozna) {
        this.idVozna = idVozna;
    }

    public Integer getIdStanice() {
        return idStanice;
    }

    public void setIdStanice(Integer idStanice) {
        this.idStanice = idStanice;
    }

    public Date getvStaniciOd() {
        return vStaniciOd;
    }

    public void setvStaniciOd(Date vStaniciOd) {
        this.vStaniciOd = vStaniciOd;
    }

    public Date getvStaniciDo() {
        return vStaniciDo;
    }

    public void setvStaniciDo(Date vStaniciDo) {
        this.vStaniciDo = vStaniciDo;
    }
}
