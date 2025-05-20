package org.example.controller;

import org.example.model.Hotel;
import org.example.model.repository.HotelRepository;
import org.example.model.Observable;
import org.example.view.HotelView;

import java.sql.SQLException;
import java.util.List;

public class HotelController extends Observable {
    private HotelRepository hotelRepository;
    private Hotel selectedHotel;

    public HotelController(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public List<Hotel> getAllHotels() throws SQLException {
        return hotelRepository.getAllHotels();
    }

    public boolean addHotel(String nume, int idLocatie, String nrTelefon,
                            String email, String facilitati, int idLant) {
        Hotel hotel = new Hotel();
        hotel.setNume(nume);
        hotel.setIdLocatie(idLocatie);
        hotel.setNrTelefon(nrTelefon);
        hotel.setEmail(email);
        hotel.setFacilitati(facilitati);
        hotel.setIdLant(idLant);

        boolean success = hotelRepository.save(hotel);
        if (success) {
            notifyObservers();
        }
        return success;
    }

    public boolean updateHotel(int id, String nume, int idLocatie, String nrTelefon,
                               String email, String facilitati, int idLant) {
        Hotel hotel = new Hotel();
        hotel.setId(id);
        hotel.setNume(nume);
        hotel.setIdLocatie(idLocatie);
        hotel.setNrTelefon(nrTelefon);
        hotel.setEmail(email);
        hotel.setFacilitati(facilitati);
        hotel.setIdLant(idLant);

        boolean success = hotelRepository.update(hotel);
        if (success) {
            notifyObservers();
        }
        return success;
    }




    public boolean deleteHotel(int id) {
        boolean success = hotelRepository.delete(id);
        if (success) {
            notifyObservers();
        }
        return success;
    }

    public void selectHotel(Hotel hotel) {
        this.selectedHotel = hotel;
        notifyObservers();
    }

    public Hotel getSelectedHotel() {
        return selectedHotel;
    }

    public List<Hotel> getHotelsByLant(int idLant) throws SQLException {
        return hotelRepository.getHotelsByLant(idLant);
    }
}