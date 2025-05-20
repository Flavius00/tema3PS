package org.example.model;

public class Hotel {
    private int id;
    private String nume;
    private int idLocatie;
    private String nrTelefon;
    private String email;
    private String facilitati;
    private int idLant;


    public Hotel(int id, String nume, int idLocatie, String nrTelefon, String email, String facilitati, int idLant) {
        this.id = id;
        this.nume = nume;
        this.idLocatie = idLocatie;
        this.nrTelefon = nrTelefon;
        this.email = email;
        this.facilitati = facilitati;
        this.idLant = idLant;
    }

    public Hotel() {}

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

    public int getIdLocatie() {
        return idLocatie;
    }

    public void setIdLocatie(int idLocatie) {
        this.idLocatie = idLocatie;
    }

    public String getNrTelefon() {
        return nrTelefon;
    }

    public void setNrTelefon(String nrTelefon) {
        this.nrTelefon = nrTelefon;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFacilitati() {
        return facilitati;
    }

    public void setFacilitati(String facilitati) {
        this.facilitati = facilitati;
    }

    public int getIdLant() {
        return idLant;
    }

    public void setIdLant(int idLant) {
        this.idLant = idLant;
    }
}
