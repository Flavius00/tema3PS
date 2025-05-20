package org.example.controller;

import org.example.model.Camera;
import org.example.model.Hotel;
import org.example.model.Rezervare;
import org.example.view.StatisticsView;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsController {
    private HotelController hotelController;
    private CameraController cameraController;
    private RezervareController rezervareController;
    private StatisticsView view;

    public StatisticsController(HotelController hotelController, CameraController cameraController,
                                RezervareController rezervareController) {
        this.hotelController = hotelController;
        this.cameraController = cameraController;
        this.rezervareController = rezervareController;
    }

    public void setView(StatisticsView view) {
        this.view = view;
    }

    public List<Hotel> getAllHotels() throws SQLException {
        return hotelController.getAllHotels();
    }

    public DefaultCategoryDataset createRoomPriceDataset(int hotelId) throws SQLException {
        List<Camera> camere = cameraController.getCamereByHotel(hotelId);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Camera camera : camere) {
            dataset.addValue(camera.getPretPerNoapte(), "Preț", camera.getNrCamera());
        }

        return dataset;
    }

    public DefaultCategoryDataset createReservationsByMonthDataset(int hotelId) throws SQLException {
        List<Rezervare> rezervari = rezervareController.getAllRezervari();
        List<Camera> camere = cameraController.getCamereByHotel(hotelId);

        List<Integer> camereIds = camere.stream()
                .map(Camera::getId)
                .collect(Collectors.toList());

        // Filter rezervari for this hotel
        rezervari = rezervari.stream()
                .filter(r -> camereIds.contains(r.getIdCamera()))
                .collect(Collectors.toList());

        Map<String, Integer> reservationsByMonth = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");

        for (Rezervare rezervare : rezervari) {
            String month = rezervare.getStartDate().format(formatter);
            reservationsByMonth.put(month, reservationsByMonth.getOrDefault(month, 0) + 1);
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<String, Integer> entry : reservationsByMonth.entrySet()) {
            dataset.addValue(entry.getValue(), "Rezervări", entry.getKey());
        }

        return dataset;
    }

    public DefaultPieDataset createOccupancyRateDataset(int hotelId) throws SQLException {
        List<Camera> camere = cameraController.getCamereByHotel(hotelId);
        int totalRooms = camere.size();

        // Check occupancy for today
        LocalDateTime now = LocalDateTime.now();
        List<Camera> occupiedRooms = cameraController.getCamereRezervateByDate(hotelId, now);
        int occupiedCount = occupiedRooms.size();
        int availableCount = totalRooms - occupiedCount;

        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Ocupate", occupiedCount);
        dataset.setValue("Disponibile", availableCount);

        return dataset;
    }
}