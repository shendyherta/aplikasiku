package com.sh.aplikasiku.model;

public class UserPantau {
    private String id, idUser, pasien, denyut, kondisi, dateCreated, dateUpdated;

    public UserPantau(String id, String idUser, String pasien, String denyut, String kondisi,
                      String dateCreated, String dateUpdated){
        this.id = id;
        this.idUser = idUser;
        this.pasien = pasien;
        this.denyut = denyut;
        this.kondisi = kondisi;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getPasien() {
        return pasien;
    }

    public void setPasien(String pasien) {
        this.pasien = pasien;
    }

    public String getDenyut() {
        return denyut;
    }

    public void setDenyut(String denyut) {
        this.denyut = denyut;
    }

    public String getKondisi() {
        return kondisi;
    }

    public void setKondisi(String kondisi) {
        this.kondisi = kondisi;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }
}
