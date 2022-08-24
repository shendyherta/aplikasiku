package com.sh.aplikasiku.model;

public class UserArtikel {
    private String id, judul, penjelasan, avatar;

    public UserArtikel(){

    }

    public UserArtikel(String judul, String penjelasan, String avatar){
        this.judul = judul;
        this.penjelasan = penjelasan;
        this.avatar = avatar;
    }

    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }

    public String getJudul(){
        return judul;
    }
    public void setJudul(String judul){
        this.judul = judul;
    }
    public String getPenjelasan(){
        return penjelasan;
    }
    public void setPenjelasan(String penjelasan){
        this.penjelasan = penjelasan;
    }

    public String getAvatar(){
        return avatar;
    }
    public void setAvatar(String avatar){
        this.avatar = avatar;
    }
}
