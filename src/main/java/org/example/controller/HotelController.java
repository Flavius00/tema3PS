package org.example.controller;

import org.example.model.Hotel;
import org.example.model.Lant;
import org.example.model.Locatie;
import org.example.model.repository.HotelRepository;
import org.example.model.repository.LantRepository;
import org.example.model.repository.LocatieRepository;
import org.example.model.Observable;
import org.example.view.HotelView;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class HotelController extends Observable {
    private HotelRepository hotelRepository;
    private LantRepository lantRepository;
    private LocatieRepository locatieRepository;
    private Hotel selectedHotel;
    private HotelView view;
    private MainFrameController mainController;

    public HotelController(HotelRepository hotelRepository, LantRepository lantRepository,
                           LocatieRepository locatieRepository) {
        this.hotelRepository = hotelRepository;
        this.lantRepository = lantRepository;
        this.locatieRepository = locatieRepository;
    }

    public void setMainController(MainFrameController mainController) {
        this.mainController = mainController;
    }

    public void setView(HotelView view) {
        this.view = view;
    }

    public List<Hotel> getAllHotels() throws SQLException {
        return hotelRepository.getAllHotels();
    }

    public List<Lant> getAllLanturi() throws SQLException {
        return lantRepository.getAllLanturi();
    }

    public List<Locatie> getAllLocatii() throws SQLException {
        return locatieRepository.getAllLocatii();
    }

    public boolean saveHotel(Hotel hotel) {
        boolean success = hotelRepository.save(hotel);
        if (success) {
            notifyObservers();
        }
        return success;
    }

    public boolean updateHotel(Hotel hotel) {
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

    public void showAddHotelDialog() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
        try {
            view.showAddHotelDialog(this, parentFrame);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parentFrame, "Eroare la deschiderea dialogului: " + e.getMessage(),
                    "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showEditHotelDialog(Hotel hotel) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
        try {
            view.showEditHotelDialog(this, hotel, parentFrame);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parentFrame, "Eroare la deschiderea dialogului: " + e.getMessage(),
                    "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showCamereForHotel() {
        if (selectedHotel != null && mainController != null) {
            mainController.showCamereTab(selectedHotel.getId());
        }
    }
}