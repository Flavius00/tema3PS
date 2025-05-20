package org.example.view;

import org.example.controller.StatisticsController;
import org.example.model.Hotel;
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
import java.util.List;
import java.util.ResourceBundle;

public class StatisticsView extends JPanel implements Observer {
    private StatisticsController controller;
    private ResourceBundle bundle;

    private JComboBox<Hotel> hotelComboBox;
    private JPanel chartsPanel;
    private JButton refreshButton;
    private JComboBox<String> statTypeCombo;

    public StatisticsView(StatisticsController controller, ResourceBundle bundle) {
        this.controller = controller;
        this.bundle = bundle;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel(bundle.getString("hotel")));

        hotelComboBox = new JComboBox<>();
        loadHotels();
        controlPanel.add(hotelComboBox);

        statTypeCombo = new JComboBox<>();
        statTypeCombo.addItem(bundle.getString("statRoomPrice"));
        statTypeCombo.addItem(bundle.getString("statReservationsByMonth"));
        statTypeCombo.addItem(bundle.getString("statOccupancyRate"));
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
        try {
            List<Hotel> hotels = controller.getAllHotels();
            hotelComboBox.removeAllItems();

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
        DefaultCategoryDataset dataset = controller.createRoomPriceDataset(hotelId);

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
        DefaultCategoryDataset dataset = controller.createReservationsByMonthDataset(hotelId);

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
        DefaultPieDataset dataset = controller.createOccupancyRateDataset(hotelId);

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

        String selectedItem = (String) statTypeCombo.getSelectedItem();
        statTypeCombo.removeAllItems();
        statTypeCombo.addItem(bundle.getString("statRoomPrice"));
        statTypeCombo.addItem(bundle.getString("statReservationsByMonth"));
        statTypeCombo.addItem(bundle.getString("statOccupancyRate"));

        // Try to select the previously selected item
        for (int i = 0; i < statTypeCombo.getItemCount(); i++) {
            if (selectedItem.equals(statTypeCombo.getItemAt(i))) {
                statTypeCombo.setSelectedIndex(i);
                break;
            }
        }

        refreshCharts();
    }
}