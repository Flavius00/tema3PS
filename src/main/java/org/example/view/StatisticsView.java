package org.example.view;

import org.example.controller.CameraController;
import org.example.controller.HotelController;
import org.example.controller.RezervareController;
import org.example.model.Camera;
import org.example.model.Hotel;
import org.example.model.Rezervare;
import org.example.model.Observer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class StatisticsView extends JPanel implements Observer {
    private HotelController hotelController;
    private CameraController cameraController;
    private RezervareController rezervareController;
    private ResourceBundle bundle;

    private JComboBox<Hotel> hotelComboBox;
    private JPanel chartsPanel;
    private JButton refreshButton;
    private JComboBox<String> statTypeCombo;

    public StatisticsView(HotelController hotelController, CameraController cameraController,
                          RezervareController rezervareController, ResourceBundle bundle) {
        this.hotelController = hotelController;
        this.cameraController = cameraController;
        this.rezervareController = rezervareController;
        this.bundle = bundle;

        hotelController.addObserver(this);
        cameraController.addObserver(this);
        rezervareController.addObserver(this);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel(bundle.getString("hotel")));

        hotelComboBox = new JComboBox<>();
        loadHotels();
        controlPanel.add(hotelComboBox);

        statTypeCombo = new JComboBox<>(new String[] {
                bundle.getString("statRoomPrice"),
                bundle.getString("statReservationsByMonth"),
                bundle.getString("statOccupancyRate")
        });
        controlPanel.add(statTypeCombo);

        refreshButton = new JButton(bundle.getString("refresh"));
        refreshButton.addActionListener(e -> refreshCharts());
        controlPanel.add(refreshButton);

        add(controlPanel, BorderLayout.NORTH);

        chartsPanel = new JPanel(new GridLayout(1, 1));
        add(chartsPanel, BorderLayout.CENTER);

        refreshCharts();
    }

    private void loadHotels() {
        hotelComboBox.removeAllItems();
        try {
            List<Hotel> hotels = hotelController.getAllHotels();
            for (Hotel hotel : hotels) {
                hotelComboBox.addItem(hotel);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    bundle.getString("errorLoadingHotels") + e.getMessage(),
                    bundle.getString("error"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshCharts() {
        chartsPanel.removeAll();

        Hotel selectedHotel = (Hotel) hotelComboBox.getSelectedItem();
        if (selectedHotel == null) {
            return;
        }

        String selectedStat = (String) statTypeCombo.getSelectedItem();

        try {
            if (selectedStat.equals(bundle.getString("statRoomPrice"))) {
                createRoomPriceChart(selectedHotel.getId());
            } else if (selectedStat.equals(bundle.getString("statReservationsByMonth"))) {
                createReservationsByMonthChart(selectedHotel.getId());
            } else if (selectedStat.equals(bundle.getString("statOccupancyRate"))) {
                createOccupancyRateChart(selectedHotel.getId());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    bundle.getString("errorCreatingChart") + e.getMessage(),
                    bundle.getString("error"),
                    JOptionPane.ERROR_MESSAGE);
        }

        chartsPanel.revalidate();
        chartsPanel.repaint();
    }

    private void createRoomPriceChart(int hotelId) throws SQLException {
        List<Camera> camere = cameraController.getCamereByHotel(hotelId);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Camera camera : camere) {
            dataset.addValue(camera.getPretPerNoapte(), bundle.getString("price"), camera.getNrCamera());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                bundle.getString("roomPriceChartTitle"),
                bundle.getString("room"),
                bundle.getString("pricePerNight"),
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartsPanel.add(chartPanel);
    }

    private void createReservationsByMonthChart(int hotelId) throws SQLException {
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
            dataset.addValue(entry.getValue(), bundle.getString("reservations"), entry.getKey());
        }

        JFreeChart chart = ChartFactory.createLineChart(
                bundle.getString("reservationsByMonthChartTitle"),
                bundle.getString("month"),
                bundle.getString("numberOfReservations"),
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartsPanel.add(chartPanel);
    }

    private void createOccupancyRateChart(int hotelId) throws SQLException {
        List<Camera> camere = cameraController.getCamereByHotel(hotelId);
        int totalRooms = camere.size();

        // Check occupancy for today
        LocalDateTime now = LocalDateTime.now();
        List<Camera> occupiedRooms = cameraController.getCamereRezervateByDate(hotelId, now);
        int occupiedCount = occupiedRooms.size();
        int availableCount = totalRooms - occupiedCount;

        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue(bundle.getString("occupied"), occupiedCount);
        dataset.setValue(bundle.getString("available"), availableCount);

        JFreeChart chart = ChartFactory.createPieChart(
                bundle.getString("occupancyRateChartTitle"),
                dataset,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartsPanel.add(chartPanel);
    }

    @Override
    public void update() {
        loadHotels();
        refreshCharts();
    }

    public void changeLanguage(ResourceBundle bundle) {
        this.bundle = bundle;
        refreshButton.setText(bundle.getString("refresh"));
        statTypeCombo.removeAllItems();
        statTypeCombo.addItem(bundle.getString("statRoomPrice"));
        statTypeCombo.addItem(bundle.getString("statReservationsByMonth"));
        statTypeCombo.addItem(bundle.getString("statOccupancyRate"));
        refreshCharts();
    }
}