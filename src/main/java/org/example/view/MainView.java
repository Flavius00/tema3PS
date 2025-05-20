package org.example.view;

import org.example.controller.CameraController;
import org.example.controller.HotelController;
import org.example.controller.RezervareController;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class MainView extends JFrame {
    private HotelController hotelController;
    private CameraController cameraController;
    private RezervareController rezervareController;

    private JTabbedPane tabbedPane;
    private HotelView hotelView;
    private CameraView cameraView;
    private StatisticsView statisticsView;

    private ResourceBundle bundle;
    private JMenu menuLanguage;
    private JMenuItem menuItemRomanian;
    private JMenuItem menuItemEnglish;
    private JMenuItem menuItemFrench;

    private static final Map<String, Locale> SUPPORTED_LOCALES = new HashMap<>();

    static {
        SUPPORTED_LOCALES.put("ro", new Locale("ro", "RO"));
        SUPPORTED_LOCALES.put("en", new Locale("en", "US"));
        SUPPORTED_LOCALES.put("fr", new Locale("fr", "FR"));
    }

    public MainView(HotelController hotelController, CameraController cameraController,
                    RezervareController rezervareController, ResourceBundle bundle) {
        this.hotelController = hotelController;
        this.cameraController = cameraController;
        this.rezervareController = rezervareController;
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
        statisticsView = new StatisticsView(hotelController, cameraController, rezervareController, bundle);

        tabbedPane.addTab(bundle.getString("hotels"), hotelView);
        tabbedPane.addTab(bundle.getString("rooms"), cameraView);
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
        menuItemRomanian.addActionListener(e -> changeLanguage("ro"));

        menuItemEnglish = new JMenuItem("English");
        menuItemEnglish.addActionListener(e -> changeLanguage("en"));

        menuItemFrench = new JMenuItem("Français");
        menuItemFrench.addActionListener(e -> changeLanguage("fr"));

        menuLanguage.add(menuItemRomanian);
        menuLanguage.add(menuItemEnglish);
        menuLanguage.add(menuItemFrench);

        menuBar.add(menuFile);
        menuBar.add(menuLanguage);

        setJMenuBar(menuBar);
    }

    private void changeLanguage(String language) {
        Locale locale = SUPPORTED_LOCALES.get(language);
        if (locale != null) {
            bundle = ResourceBundle.getBundle("i18n.messages", locale);
            updateComponents();
        }
    }

    private void updateComponents() {
        // Update frame title
        setTitle(bundle.getString("appTitle"));

        // Update menu
        menuLanguage.setText(bundle.getString("language"));

        // Update tabs
        tabbedPane.setTitleAt(0, bundle.getString("hotels"));
        tabbedPane.setTitleAt(1, bundle.getString("rooms"));
        tabbedPane.setTitleAt(2, bundle.getString("statistics"));

        // Update views
        hotelView.changeLanguage(bundle);
        cameraView.changeLanguage(bundle);
        statisticsView.changeLanguage(bundle);
    }
}