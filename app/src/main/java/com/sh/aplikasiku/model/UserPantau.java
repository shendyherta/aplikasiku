package com.sh.aplikasiku.model;

public class UserPantau {
    private String id, denyut, kondisi, idUser;

    public UserPantau(String denyut, String kondisi){
        this.kondisi = kondisi;
        this.denyut = denyut;
    }

    public UserPantau(String id, String denyut, String kondisi){
        this.id = id;
        this.kondisi = kondisi;
        this.denyut = denyut;
    }

    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }



    public String getDenyut(){
        return denyut;
    }
    public void setDenyut(String denyut){
        this.denyut= denyut;
    }

    public String getKondisi(){
        return kondisi;
    }
    public void setKondisi(String kondisi){
        this.kondisi = kondisi;
    }
}
