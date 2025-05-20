package org.example.controller;

import org.example.controller.utils.CsvExporter;
import org.example.controller.utils.DocExporter;
import org.example.model.Rezervare;
import org.example.model.repository.CameraRepository;
import org.example.model.repository.RezervareRepository;
import org.example.model.Observable;
import org.example.view.RezervareView;

import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class RezervareController extends Observable {
    private RezervareRepository rezervareRepository;
    private CameraRepository cameraRepository; // Adăugat pentru a obține informații despre camere
    private RezervareView view;

    public RezervareController(RezervareRepository rezervareRepository, CameraRepository cameraRepository) {
        this.rezervareRepository = rezervareRepository;
        this.cameraRepository = cameraRepository;
    }

    public void setView(RezervareView view) {
        this.view = view;
    }

    public List<Rezervare> getAllRezervari() throws SQLException {
        return rezervareRepository.getAllRezervari();
    }

    public List<Rezervare> getRezervariByCamera(int idCamera) throws SQLException {
        return rezervareRepository.getRezervariByCamera(idCamera);
    }

    public boolean saveRezervare(Rezervare rezervare) {
        boolean success = rezervareRepository.save(rezervare);
        if (success) {
            notifyObservers();
        }
        return success;
    }

    public boolean updateRezervare(Rezervare rezervare) {
        boolean success = rezervareRepository.update(rezervare);
        if (success) {
            notifyObservers();
        }
        return success;
    }

    public boolean deleteRezervare(int id) {
        boolean success = rezervareRepository.delete(id);
        if (success) {
            notifyObservers();
        }
        return success;
    }

    public List<Integer> getCamereRezervateByDate(LocalDateTime date) throws SQLException {
        return rezervareRepository.getAllRezervari().stream()
                .filter(rezervare -> isDateBetween(date, rezervare.getStartDate(), rezervare.getEndDate()))
                .map(Rezervare::getIdCamera)
                .collect(Collectors.toList());
    }

    private boolean isDateBetween(LocalDateTime date, LocalDateTime start, LocalDateTime end) {
        return (date.isEqual(start) || date.isAfter(start)) &&
                (date.isEqual(end) || date.isBefore(end));
    }

    public void exportRezervariToCsv(int idHotel, LocalDateTime date, String filePath) throws SQLException, IOException {
        List<Rezervare> rezervari = getRezervariByHotelAndDate(idHotel, date);
        CsvExporter.exportRezervari(rezervari, filePath);
    }

    public void exportRezervariToDoc(int idHotel, LocalDateTime date, String filePath) throws SQLException, IOException {
        List<Rezervare> rezervari = getRezervariByHotelAndDate(idHotel, date);
        DocExporter.exportRezervari(rezervari, filePath);
    }

    public List<Rezervare> getRezervariByHotelAndDate(int idHotel, LocalDateTime date) throws SQLException {
        return rezervareRepository.getRezervariByHotelAndDate(idHotel, date);
    }

    public void showAddRezervareDialog(int idCamera) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
        view.showAddRezervareDialog(this, parentFrame, idCamera);
    }

    public void showEditRezervareDialog(Rezervare rezervare) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
        view.showEditRezervareDialog(this, rezervare, parentFrame);
    }

    // Adăugat pentru a oferi acces la CameraRepository
    public CameraRepository getCameraRepository() {
        return cameraRepository;
    }
}