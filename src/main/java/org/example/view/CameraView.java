package org.example.view;

import org.example.controller.CameraController;
import org.example.model.Camera;
import org.example.model.Observer;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
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

        JPanel buttonPanel = new JPanel();
        addButton = new JButton(bundle.getString("add"));
        editButton = new JButton(bundle.getString("edit"));
        deleteButton = new JButton(bundle.getString("delete"));
        viewRezervariButton = new JButton(bundle.getString("viewReservations"));
        exportCsvButton = new JButton(bundle.getString("exportCsv"));
        exportDocButton = new JButton(bundle.getString("exportDoc"));

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewRezervariButton);
        buttonPanel.add(exportCsvButton);
        buttonPanel.add(exportDocButton);

        controlPanel.add(buttonPanel, BorderLayout.SOUTH);

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

    public void changeLanguage(ResourceBundle bundle) {
        this.bundle = bundle;
        addButton.setText(bundle.getString("add"));
        editButton.setText(bundle.getString("edit"));
        deleteButton.setText(bundle.getString("delete"));
        viewRezervariButton.setText(bundle.getString("viewReservations"));
        exportCsvButton.setText(bundle.getString("exportCsv"));
        exportDocButton.setText(bundle.getString("exportDoc"));
        filterButton.setText(bundle.getString("filter"));
    }
}