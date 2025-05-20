package org.example.controller;

import org.example.model.repository.CameraRepository;
import org.example.model.repository.HotelRepository;
import org.example.model.repository.LantRepository;
import org.example.model.repository.LocatieRepository;
import org.example.model.repository.RezervareRepository;
import org.example.view.MainFrame;

import java.util.Locale;
import java.util.ResourceBundle;

public class MainFrameController {
    private MainFrame mainFrame;
    private HotelController hotelController;
    private CameraController cameraController;
    private RezervareController rezervareController;
    private StatisticsController statisticsController;

    private ResourceBundle bundle;

    public MainFrameController() {
        // Initialize repositories
        HotelRepository hotelRepository = new HotelRepository();
        CameraRepository cameraRepository = new CameraRepository();
        RezervareRepository rezervareRepository = new RezervareRepository();
        LantRepository lantRepository = new LantRepository();
        LocatieRepository locatieRepository = new LocatieRepository();

        // Initialize controllers
        rezervareController = new RezervareController(rezervareRepository, cameraRepository); // Modificat
        cameraController = new CameraController(cameraRepository, hotelRepository, rezervareController);
        hotelController = new HotelController(hotelRepository, lantRepository, locatieRepository);
        statisticsController = new StatisticsController(hotelController, cameraController, rezervareController);

        // Set main controller references
        hotelController.setMainController(this);
        cameraController.setMainController(this);

        // Initialize i18n
        Locale locale = new Locale("ro", "RO"); // Default locale
        bundle = ResourceBundle.getBundle("i18n.messages", locale);

        // Create main frame
        mainFrame = new MainFrame(
                hotelController,
                cameraController,
                rezervareController,
                statisticsController,
                this,
                bundle
        );

        // Set views to controllers
        hotelController.setView(mainFrame.getHotelView());
        cameraController.setView(mainFrame.getCameraView());
        rezervareController.setView(mainFrame.getRezervareView());
        statisticsController.setView(mainFrame.getStatisticsView());
    }

    public void showApplication() {
        mainFrame.setVisible(true);
    }

    public void showCamereTab(int hotelId) {
        mainFrame.showCamereTab(hotelId);
    }

    public void showRezervariTab(int cameraId) {
        mainFrame.showRezervariTab(cameraId);
    }

    public void changeLanguage(String language) {
        Locale locale = null;

        switch (language) {
            case "ro":
                locale = new Locale("ro", "RO");
                break;
            case "en":
                locale = new Locale("en", "US");
                break;
            case "fr":
                locale = new Locale("fr", "FR");
                break;
            default:
                locale = new Locale("ro", "RO");
                break;
        }

        bundle = ResourceBundle.getBundle("i18n.messages", locale);
        mainFrame.changeLanguage(bundle);
    }
}