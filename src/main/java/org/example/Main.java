package org.example;

import org.example.controller.CameraController;
import org.example.controller.HotelController;
import org.example.controller.RezervareController;
import org.example.model.repository.CameraRepository;
import org.example.model.repository.HotelRepository;
import org.example.model.repository.RezervareRepository;
import org.example.view.MainView;

import javax.swing.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize repositories
        HotelRepository hotelRepository = new HotelRepository();
        CameraRepository cameraRepository = new CameraRepository();
        RezervareRepository rezervareRepository = new RezervareRepository();

        // Initialize controllers
        RezervareController rezervareController = new RezervareController(rezervareRepository);
        CameraController cameraController = new CameraController(cameraRepository, rezervareController);
        HotelController hotelController = new HotelController(hotelRepository);

        // Initialize i18n
        Locale locale = new Locale("ro", "RO"); // Default locale
        ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", locale);

        // Initialize main view
        SwingUtilities.invokeLater(() -> {
            MainView mainView = new MainView(hotelController, cameraController, rezervareController, bundle);
            mainView.setVisible(true);
        });
    }
}