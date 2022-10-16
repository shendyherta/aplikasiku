package com.sh.aplikasiku.model;

public class UserArtikel {
    //inisiasi variabel
    private String id, judul, penjelasan, avatar, dateCreated, dateUpdated;

    //construktor untuk menerima data
    public UserArtikel(String id, String judul, String penjelasan, String avatar,
                       String dateCreated, String dateUpdated) {
        this.id = id;
        this.judul = judul;
        this.penjelasan = penjelasan;
        this.avatar = avatar;
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

    //fungsi untuk mendapatkan judul
    public String getJudul() {
        return judul;
    }

    //fungsi untuk mengisi judul
    public void setJudul(String judul) {
        this.judul = judul;
    }

    //fungsi untuk mendapatkan penjelasan
    public String getPenjelasan() {
        return penjelasan;
    }

    //fungsi untuk mengisi penjelasan
    public void setPenjelasan(String penjelasan) {
        this.penjelasan = penjelasan;
    }

    //fungsi untuk mendapatkan avatar
    public String getAvatar() {
        return avatar;
    }

    //fungsi untuk mengisi avatar
    public void setAvatar(String avatar) {
        this.avatar = avatar;
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
