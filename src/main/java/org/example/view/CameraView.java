package org.example.view;

import org.example.controller.CameraController;
import org.example.model.Camera;
import org.example.model.Hotel;
import org.example.model.Observer;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class CameraView extends JPanel implements Observer {
    private CameraController controller;
    private JList<Camera> cameraList;
    private DefaultListModel<Camera> listModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton filterButton;
    private JButton viewRezervariButton;
    private JButton exportCsvButton;
    private JButton exportDocButton;
    private JButton exportAllCsvButton;
    private JButton exportAllDocButton;
    private JTextField pretMinField;
    private JTextField pretMaxField;
    private JTextField dateField;
    private ResourceBundle bundle;
    private int currentHotelId;

    public CameraView(CameraController controller, ResourceBundle bundle) {
        this.controller = controller;
        this.bundle = bundle;
        controller.addObserver(this);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        cameraList = new JList<>(listModel);
        cameraList.setCellRenderer(new CameraListCellRenderer());
        cameraList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(cameraList);
        add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new BorderLayout());

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel(bundle.getString("minPrice")));
        pretMinField = new JTextField(5);
        filterPanel.add(pretMinField);

        filterPanel.add(new JLabel(bundle.getString("maxPrice")));
        pretMaxField = new JTextField(5);
        filterPanel.add(pretMaxField);

        filterPanel.add(new JLabel(bundle.getString("date")));
        dateField = new JTextField(10);
        filterPanel.add(dateField);

        filterButton = new JButton(bundle.getString("filter"));
        filterPanel.add(filterButton);

        controlPanel.add(filterPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        addButton = new JButton(bundle.getString("add"));
        editButton = new JButton(bundle.getString("edit"));
        deleteButton = new JButton(bundle.getString("delete"));
        viewRezervariButton = new JButton(bundle.getString("viewReservations"));

        exportCsvButton = new JButton(bundle.getString("exportReservedCsv"));
        exportDocButton = new JButton(bundle.getString("exportReservedDoc"));
        exportAllCsvButton = new JButton(bundle.getString("exportAllCsv"));
        exportAllDocButton = new JButton(bundle.getString("exportAllDoc"));

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewRezervariButton);
        buttonPanel.add(exportCsvButton);
        buttonPanel.add(exportDocButton);
        buttonPanel.add(exportAllCsvButton);
        buttonPanel.add(exportAllDocButton);

        JPanel buttonWrapperPanel = new JPanel(new BorderLayout());
        buttonWrapperPanel.add(buttonPanel, BorderLayout.CENTER);
        buttonWrapperPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        controlPanel.add(buttonWrapperPanel, BorderLayout.SOUTH);

        add(controlPanel, BorderLayout.SOUTH);

        filterButton.addActionListener(e -> controller.showFilterResults());

        addButton.addActionListener(e -> controller.showAddCameraDialog());

        editButton.addActionListener(e -> {
            Camera selectedCamera = cameraList.getSelectedValue();
            if (selectedCamera != null) {
                controller.showEditCameraDialog(selectedCamera);
            }
        });

        deleteButton.addActionListener(e -> {
            Camera selectedCamera = cameraList.getSelectedValue();
            if (selectedCamera != null) {
                controller.deleteCamera(selectedCamera.getId());
            }
        });

        viewRezervariButton.addActionListener(e -> {
            Camera selectedCamera = cameraList.getSelectedValue();
            if (selectedCamera != null) {
                controller.showRezervariForCamera(selectedCamera);
            }
        });

        exportCsvButton.addActionListener(e -> controller.exportCamereRezervateToCSV());
        exportDocButton.addActionListener(e -> controller.exportCamereRezervateToDoc());
        exportAllCsvButton.addActionListener(e -> controller.exportCamereToCSV());
        exportAllDocButton.addActionListener(e -> controller.exportCamereToDoc());
    }

    public void setCurrentHotelId(int hotelId) {
        this.currentHotelId = hotelId;
        loadCamere();
    }

    private void loadCamere() {
        try {
            List<Camera> camere = controller.getCamereByHotel(currentHotelId);
            listModel.clear();

            for (Camera camera : camere) {
                listModel.addElement(camera);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    bundle.getString("errorLoadingRooms") + e.getMessage(),
                    bundle.getString("error"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public int getCurrentHotelId() {
        return currentHotelId;
    }

    public String getPretMin() {
        return pretMinField.getText();
    }

    public String getPretMax() {
        return pretMaxField.getText();
    }

    public String getDate() {
        return dateField.getText();
    }

    public void displayCamere(List<Camera> camere) {
        listModel.clear();
        for (Camera camera : camere) {
            listModel.addElement(camera);
        }
    }

    @Override
    public void update() {
        loadCamere();
    }

    public void changeLanguage(ResourceBundle bundle) {
        this.bundle = bundle;
        addButton.setText(bundle.getString("add"));
        editButton.setText(bundle.getString("edit"));
        deleteButton.setText(bundle.getString("delete"));
        viewRezervariButton.setText(bundle.getString("viewReservations"));
        exportCsvButton.setText(bundle.getString("exportReservedCsv"));
        exportDocButton.setText(bundle.getString("exportReservedDoc"));
        exportAllCsvButton.setText(bundle.getString("exportAllCsv"));
        exportAllDocButton.setText(bundle.getString("exportAllDoc"));
        filterButton.setText(bundle.getString("filter"));
    }

    public void showAddCameraDialog(CameraController controller, JFrame parentFrame) throws SQLException {
        List<Hotel> hoteluri = controller.getAllHotels();

        // Pentru simplitate, utilizăm valori statice pentru poze (în realitate ar trebui să existe un repository)
        Integer[] pozeIds = {1, 2, 3};

        CameraDialog dialog = new CameraDialog(parentFrame, null, bundle, hoteluri, pozeIds);

        if (currentHotelId > 0) {
            dialog.preSelectHotel(currentHotelId);
        }

        dialog.setVisible(true);

        if (dialog.isApproved()) {
            Camera camera = dialog.getCamera();
            controller.saveCamera(camera);
        }
    }

    public void showEditCameraDialog(CameraController controller, Camera camera, JFrame parentFrame) throws SQLException {
        List<Hotel> hoteluri = controller.getAllHotels();

        // Pentru simplitate, utilizăm valori statice pentru poze (în realitate ar trebui să existe un repository)
        Integer[] pozeIds = {1, 2, 3};

        CameraDialog dialog = new CameraDialog(parentFrame, camera, bundle, hoteluri, pozeIds);
        dialog.setVisible(true);

        if (dialog.isApproved()) {
            Camera updatedCamera = dialog.getCamera();
            controller.updateCamera(updatedCamera);
        }
    }

    private class CameraListCellRenderer extends DefaultListCellRenderer {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Camera) {
                Camera camera = (Camera) value;
                setText(String.format("%s - %.2f %s",
                        camera.getNrCamera(),
                        camera.getPretPerNoapte(),
                        bundle.getString("currency")));
            }
            return this;
        }
    }
}