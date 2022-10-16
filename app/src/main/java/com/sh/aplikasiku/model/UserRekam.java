package com.sh.aplikasiku.model;

public class UserRekam {
    //inisiasi variabel
    private String id, idUser, pasien, beratBadan, lingkarBadan, kondisiHB, tekananDarah,
            lajuPernafasan, suhu, denyutJantung, rujukan, dateCreated, dateUpdated;

    //construktor untuk menerima data
    public UserRekam(String id, String idUser, String pasien, String beratBadan, String lingkarBadan,
                     String kondisiHB, String tekananDarah, String lajuPernafasan, String suhu,
                     String denyutJantung, String rujukan, String dateCreated, String dateUpdated) {
        this.id = id;
        this.idUser = idUser;
        this.pasien = pasien;
        this.beratBadan = beratBadan;
        this.lingkarBadan = lingkarBadan;
        this.kondisiHB = kondisiHB;
        this.tekananDarah = tekananDarah;
        this.lajuPernafasan = lajuPernafasan;
        this.suhu = suhu;
        this.denyutJantung = denyutJantung;
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

    //fungsi untuk mendapatkan beratBadan
    public String getBeratBadan() {
        return beratBadan;
    }

    //fungsi untuk mengisi beratBadan
    public void setBeratBadan(String beratBadan) {
        this.beratBadan = beratBadan;
    }

    //fungsi untuk mendapatkan lingkarBadan
    public String getLingkarBadan() {
        return lingkarBadan;
    }

    //fungsi untuk mengisi lingkarBadan
    public void setLingkarBadan(String lingkarBadan) {
        this.lingkarBadan = lingkarBadan;
    }

    //fungsi untuk mendapatkan kondisiHB
    public String getKondisiHB() {
        return kondisiHB;
    }

    //fungsi untuk mengisi kondisiHB
    public void setKondisiHB(String kondisiHB) {
        this.kondisiHB = kondisiHB;
    }

    //fungsi untuk mendapatkan tekananDarah
    public String getTekananDarah() {
        return tekananDarah;
    }

    //fungsi untuk mengisi tekananDarah
    public void setTekananDarah(String tekananDarah) {
        this.tekananDarah = tekananDarah;
    }

    //fungsi untuk mendapatkan lajuPernafasan
    public String getLajuPernafasan() {
        return lajuPernafasan;
    }

    //fungsi untuk mengisi lajuPernafasan
    public void setLajuPernafasan(String lajuPernafasan) {
        this.lajuPernafasan = lajuPernafasan;
    }

    //fungsi untuk mendapatkan suhu
    public String getSuhu() {
        return suhu;
    }

    //fungsi untuk mengisi suhu
    public void setSuhu(String suhu) {
        this.suhu = suhu;
    }

    //fungsi untuk mendapatkan denyutJantung
    public String getDenyutJantung() {
        return denyutJantung;
    }

    //fungsi untuk mengisi denyutJantung
    public void setDenyutJantung(String denyutJantung) {
        this.denyutJantung = denyutJantung;
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
