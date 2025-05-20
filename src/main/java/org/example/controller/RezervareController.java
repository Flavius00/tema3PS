package org.example.controller;

import org.example.model.Rezervare;
import org.example.model.repository.RezervareRepository;
import org.example.model.Observable;
import org.example.controller.utils.CsvExporter;
import org.example.controller.utils.DocExporter;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class RezervareController extends Observable {
    private RezervareRepository rezervareRepository;

    public RezervareController(RezervareRepository rezervareRepository) {
        this.rezervareRepository = rezervareRepository;
    }

    public List<Rezervare> getAllRezervari() throws SQLException {
        return rezervareRepository.getAllRezervari();
    }

    public boolean addRezervare(LocalDateTime startDate, LocalDateTime endDate,
                                int idCamera, String numeClient, String prenumeClient,
                                String telefonClient, String emailClient) {
        Rezervare rezervare = new Rezervare(0, startDate, endDate, idCamera,
                numeClient, prenumeClient, telefonClient, emailClient);

        boolean success = rezervareRepository.save(rezervare);
        if (success) {
            notifyObservers();
        }
        return success;
    }

    public boolean updateRezervare(int id, LocalDateTime startDate, LocalDateTime endDate,
                                   int idCamera, String numeClient, String prenumeClient,
                                   String telefonClient, String emailClient) {
        Rezervare rezervare = new Rezervare(id, startDate, endDate, idCamera,
                numeClient, prenumeClient, telefonClient, emailClient);

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
}