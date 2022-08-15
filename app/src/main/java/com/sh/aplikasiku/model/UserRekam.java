package com.sh.aplikasiku.model;

public class UserRekam {
    private String id, berat, lingkar, suhu, laju, kondisi, tekanan, denyut;

    public UserRekam(String berat, String denyut, String laju, String suhu, String tekanan, String kondisi, String lingkar){
        this.berat = berat;
        this.lingkar = lingkar;
        this.suhu = suhu;
        this.laju = laju;
        this.kondisi = kondisi;
        this.tekanan = tekanan;
        this.denyut = denyut;
    }

    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }

    public String getLingkar(){
        return lingkar;
    }
    public void setLingkar(String lingkar){
        this.lingkar = lingkar;
    }

    public String getSuhu(){
        return suhu;
    }
    public void setSuhu(String suhu){
        this.suhu = suhu;
    }

    public String getLaju(){
        return laju;
    }
    public void setLaju(String laju){
        this.laju = laju;
    }

    public String getKondisi(){
        return kondisi;
    }
    public void setBKondisi(String kondisi){
        this.kondisi = kondisi;
    }

    public String getTekanan(){
        return tekanan;
    }
    public void setTekanan(String tekanan){
        this.tekanan = tekanan;
    }

    public String getDenyut(){
        return denyut;
    }
    public void setDenyut(String denyut){
        this.denyut= denyut;
    }

    public String getBerat(){
        return berat;
    }
    public void setBerat(String berat){
        this.berat = berat;
    }
}
