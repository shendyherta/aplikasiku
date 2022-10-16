package com.sh.aplikasiku.model;

public class User {
    //inisiasi variabel
    private int role;
    private String id, username;

    //construktor untuk menerima data
    public User(String id, String username, int role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    //fungsi untuk mendapatkan id
    public String getId() {
        return id;
    }

    //fungsi untuk mengisi id
    public void setId(String id) {
        this.id = id;
    }

    //fungsi untuk mendapatkan role
    public int getRole() {
        return role;
    }

    //fungsi untuk mengisi role
    public void setRole(int role) {
        this.role = role;
    }

    //fungsi untuk mendapatkan username
    public String getUsername() {
        return username;
    }

    //fungsi untuk mengisi username
    public void setUsername(String username) {
        this.username = username;
    }

    //fungsi untuk mengubah username ke string agar bisa digunakan pada spinner
    @Override
    public String toString() {
        return username;
    }
}