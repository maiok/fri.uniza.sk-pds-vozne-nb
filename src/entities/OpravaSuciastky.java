package entities;

import java.sql.Date;

public class OpravaSuciastky {

    private Integer idOpravy;
    private Integer idZamestnanca;
    private Integer idSuciastky;
    private Date opravaOd;
    private Date opravaDo;

    public OpravaSuciastky() {
    }

    public Integer getIdOpravy() {
        return idOpravy;
    }

    public void setIdOpravy(Integer idOpravy) {
        this.idOpravy = idOpravy;
    }

    public Integer getIdZamestnanca() {
        return idZamestnanca;
    }

    public void setIdZamestnanca(Integer idZamestnanca) {
        this.idZamestnanca = idZamestnanca;
    }

    public Integer getIdSuciastky() {
        return idSuciastky;
    }

    public void setIdSuciastky(Integer idSuciastky) {
        this.idSuciastky = idSuciastky;
    }

    public Date getOpravaOd() {
        return opravaOd;
    }

    public void setOpravaOd(Date opravaOd) {
        this.opravaOd = opravaOd;
    }

    public Date getOpravaDo() {
        return opravaDo;
    }

    public void setOpravaDo(Date opravaDo) {
        this.opravaDo = opravaDo;
    }
}
