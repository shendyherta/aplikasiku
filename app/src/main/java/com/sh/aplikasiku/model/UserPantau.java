package com.sh.aplikasiku.model;

public class UserPantau {
    //inisiasi variabel
    private String id, idUser, pasien, denyut, kondisi, rujukan, dateCreated, dateUpdated;

    //construktor untuk menerima data
    public UserPantau(String id, String idUser, String pasien, String denyut, String kondisi,
                      String rujukan, String dateCreated, String dateUpdated){
        this.id = id;
        this.idUser = idUser;
        this.pasien = pasien;
        this.denyut = denyut;
        this.kondisi = kondisi;
        this.rujukan = rujukan;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
    }

    //fungsi untuk mendapatkan id
    public String getId() {
        return id;
    }

    //fungsi untuk mengisi id
    public void setId(String id) {
        this.id = id;
    }

    //fungsi untuk mendapatkan idUser
    public String getIdUser() {
        return idUser;
    }

    //fungsi untuk mengisi idUser
    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    //fungsi untuk mendapatkan pasien
    public String getPasien() {
        return pasien;
    }

    //fungsi untuk mengisi pasien
    public void setPasien(String pasien) {
        this.pasien = pasien;
    }

    //fungsi untuk mendapatkan denyut
    public String getDenyut() {
        return denyut;
    }

    //fungsi untuk mengisi denyut
    public void setDenyut(String denyut) {
        this.denyut = denyut;
    }

    //fungsi untuk mendapatkan kondisi
    public String getKondisi() {
        return kondisi;
    }

    //fungsi untuk mengisi kondisi
    public void setKondisi(String kondisi) {
        this.kondisi = kondisi;
    }

    //fungsi untuk mendapatkan rujukan
    public String getRujukan() {
        return rujukan;
    }

    //fungsi untuk mengisi rujukan
    public void setRujukan(String rujukan) {
        this.rujukan = rujukan;
    }

    //fungsi untuk mendapatkan dateCreated
    public String getDateCreated() {
        return dateCreated;
    }

    //fungsi untuk mengisi dateCreated
    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    //fungsi untuk mendapatkan dateUpdated
    public String getDateUpdated() {
        return dateUpdated;
    }

    //fungsi untuk mengisi dateUpdated
    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }
}
