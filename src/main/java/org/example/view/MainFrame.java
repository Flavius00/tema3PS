package org.example.view;

import org.example.controller.CameraController;
import org.example.controller.HotelController;
import org.example.controller.MainFrameController;
import org.example.controller.RezervareController;
import org.example.controller.StatisticsController;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

public class MainFrame extends JFrame {
    private HotelController hotelController;
    private CameraController cameraController;
    private RezervareController rezervareController;
    private StatisticsController statisticsController;
    private MainFrameController mainController;

    private JTabbedPane tabbedPane;
    private HotelView hotelView;
    private CameraView cameraView;
    private RezervareView rezervareView;
    private StatisticsView statisticsView;

    private ResourceBundle bundle;
    private JMenu menuLanguage;
    private JMenuItem menuItemRomanian;
    private JMenuItem menuItemEnglish;
    private JMenuItem menuItemFrench;

    public MainFrame(HotelController hotelController, CameraController cameraController,
                     RezervareController rezervareController, StatisticsController statisticsController,
                     MainFrameController mainController, ResourceBundle bundle) {
        this.hotelController = hotelController;
        this.cameraController = cameraController;
        this.rezervareController = rezervareController;
        this.statisticsController = statisticsController;
        this.mainController = mainController;
        this.bundle = bundle;

        initComponents();
        setTitle(bundle.getString("appTitle"));
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        createMenuBar();

        tabbedPane = new JTabbedPane();

        hotelView = new HotelView(hotelController, bundle);
        cameraView = new CameraView(cameraController, bundle);
        rezervareView = new RezervareView(rezervareController, bundle);
        statisticsView = new StatisticsView(statisticsController, bundle);

        tabbedPane.addTab(bundle.getString("hotels"), hotelView);
        tabbedPane.addTab(bundle.getString("rooms"), cameraView);
        tabbedPane.addTab(bundle.getString("reservations"), rezervareView);
        tabbedPane.addTab(bundle.getString("statistics"), statisticsView);

        getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuFile = new JMenu(bundle.getString("file"));
        JMenuItem menuItemExit = new JMenuItem(bundle.getString("exit"));
        menuItemExit.addActionListener(e -> System.exit(0));
        menuFile.add(menuItemExit);

        menuLanguage = new JMenu(bundle.getString("language"));

        menuItemRomanian = new JMenuItem("Română");
        menuItemRomanian.addActionListener(e -> mainController.changeLanguage("ro"));

        menuItemEnglish = new JMenuItem("English");
        menuItemEnglish.addActionListener(e -> mainController.changeLanguage("en"));

        menuItemFrench = new JMenuItem("Français");
        menuItemFrench.addActionListener(e -> mainController.changeLanguage("fr"));

        menuLanguage.add(menuItemRomanian);
        menuLanguage.add(menuItemEnglish);
        menuLanguage.add(menuItemFrench);

        menuBar.add(menuFile);
        menuBar.add(menuLanguage);

        setJMenuBar(menuBar);
    }

    public void changeLanguage(ResourceBundle bundle) {
        this.bundle = bundle;
        updateComponents();
    }

    private void updateComponents() {
        // Update frame title
        setTitle(bundle.getString("appTitle"));

        // Update menu
        menuLanguage.setText(bundle.getString("language"));

        // Update tabs
        tabbedPane.setTitleAt(0, bundle.getString("hotels"));
        tabbedPane.setTitleAt(1, bundle.getString("rooms"));
        tabbedPane.setTitleAt(2, bundle.getString("reservations"));
        tabbedPane.setTitleAt(3, bundle.getString("statistics"));

        // Update views
        hotelView.changeLanguage(bundle);
        cameraView.changeLanguage(bundle);
        rezervareView.changeLanguage(bundle);
        statisticsView.changeLanguage(bundle);
    }

    public void showCamereTab(int hotelId) {
        cameraView.setCurrentHotelId(hotelId);
        tabbedPane.setSelectedIndex(1); // Index pentru tabul Camere
    }

    public void showRezervariTab(int cameraId) {
        rezervareView.setCurrentCameraId(cameraId);
        tabbedPane.setSelectedIndex(2); // Index pentru tabul Rezervări
    }

    public HotelView getHotelView() {
        return hotelView;
    }

    public CameraView getCameraView() {
        return cameraView;
    }

    public RezervareView getRezervareView() {
        return rezervareView;
    }

    public StatisticsView getStatisticsView() {
        return statisticsView;
    }
}