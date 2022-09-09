package com.sh.aplikasiku.model;

public class UserRekam {
    private String id, idUser, pasien, beratBadan, lingkarBadan, kondisiHB, tekananDarah,
            lajuPernafasan, suhu, denyutJantung, rujukan, dateCreated, dateUpdated;

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

    public String getBeratBadan() {
        return beratBadan;
    }

    public void setBeratBadan(String beratBadan) {
        this.beratBadan = beratBadan;
    }

    public String getLingkarBadan() {
        return lingkarBadan;
    }

    public void setLingkarBadan(String lingkarBadan) {
        this.lingkarBadan = lingkarBadan;
    }

    public String getKondisiHB() {
        return kondisiHB;
    }

    public void setKondisiHB(String kondisiHB) {
        this.kondisiHB = kondisiHB;
    }

    public String getTekananDarah() {
        return tekananDarah;
    }

    public void setTekananDarah(String tekananDarah) {
        this.tekananDarah = tekananDarah;
    }

    public String getLajuPernafasan() {
        return lajuPernafasan;
    }

    public void setLajuPernafasan(String lajuPernafasan) {
        this.lajuPernafasan = lajuPernafasan;
    }

    public String getSuhu() {
        return suhu;
    }

    public void setSuhu(String suhu) {
        this.suhu = suhu;
    }

    public String getDenyutJantung() {
        return denyutJantung;
    }

    public void setDenyutJantung(String denyutJantung) {
        this.denyutJantung = denyutJantung;
    }

    public String getRujukan() {
        return rujukan;
    }

    public void setRujukan(String rujukan) {
        this.rujukan = rujukan;
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
