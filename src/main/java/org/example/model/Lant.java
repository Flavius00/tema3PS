package org.example.model;

public class Lant {
    private int id;
    private String nume;


    public Lant(int id, String nume) {
        this.id = id;
        this.nume = nume;
    }

    public Lant() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

}
