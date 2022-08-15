package com.sh.aplikasiku.model;

public class User {
    private String id, judul, penjelasan;

    public User(){

    }
    public User(String judul, String penjelasan){
        this.judul = judul;
        this.penjelasan = penjelasan;
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
}
