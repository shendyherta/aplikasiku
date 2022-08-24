package com.sh.aplikasiku.model;

public class UserRekam {
    private String id, beratBadan, lingkarBadan, kondisiHB, tekananDarah,
            lajuPernafasan, suhu, denyutJantung;

    public UserRekam(String beratBadan, String lingkarBadan, String kondisiHB, String tekananDarah,
                     String lajuPernafasan, String suhu, String denyutJantung) {
        this.beratBadan = beratBadan;
        this.lingkarBadan = lingkarBadan;
        this.kondisiHB = kondisiHB;
        this.tekananDarah = tekananDarah;
        this.lajuPernafasan = lajuPernafasan;
        this.suhu = suhu;
        this.denyutJantung = denyutJantung;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
