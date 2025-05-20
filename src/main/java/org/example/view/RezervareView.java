package org.example.view;

import org.example.controller.RezervareController;
import org.example.model.Camera;
import org.example.model.Rezervare;
import org.example.model.Observer;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ResourceBundle;

public class RezervareView extends JPanel implements Observer {
    private RezervareController controller;
    private JList<Rezervare> rezervareList;
    private DefaultListModel<Rezervare> listModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton exportCsvButton;
    private JButton exportDocButton;
    private JTextField dateField;
    private JLabel dateLabel;
    private ResourceBundle bundle;
    private int currentCameraId;
    private int currentHotelId;

    public RezervareView(RezervareController controller, ResourceBundle bundle) {
        this.controller = controller;
        this.bundle = bundle;
        controller.addObserver(this);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        rezervareList = new JList<>(listModel);
        rezervareList.setCellRenderer(new RezervareListCellRenderer());
        rezervareList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(rezervareList);
        add(scrollPane, BorderLayout.CENTER);

        // Panel pentru dată și filtrare
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dateLabel = new JLabel(bundle.getString("date") + " (dd-MM-yyyy):");
        dateField = new JTextField(10);
        datePanel.add(dateLabel);
        datePanel.add(dateField);

        // Panel pentru toate butoanele
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.add(datePanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButton = new JButton(bundle.getString("add"));
        editButton = new JButton(bundle.getString("edit"));
        deleteButton = new JButton(bundle.getString("delete"));
        exportCsvButton = new JButton(bundle.getString("exportCsv"));
        exportDocButton = new JButton(bundle.getString("exportDoc"));

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exportCsvButton);
        buttonPanel.add(exportDocButton);

        controlPanel.add(buttonPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> controller.showAddRezervareDialog(currentCameraId));

        editButton.addActionListener(e -> {
            Rezervare selectedRezervare = rezervareList.getSelectedValue();
            if (selectedRezervare != null) {
                controller.showEditRezervareDialog(selectedRezervare);
            }
        });

        deleteButton.addActionListener(e -> {
            Rezervare selectedRezervare = rezervareList.getSelectedValue();
            if (selectedRezervare != null) {
                controller.deleteRezervare(selectedRezervare.getId());
            }
        });

        exportCsvButton.addActionListener(e -> exportRezervariToCsv());
        exportDocButton.addActionListener(e -> exportRezervariToDoc());
    }

    private void exportRezervariToCsv() {
        try {
            String dateStr = dateField.getText();

            if (dateStr.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        bundle.getString("pleaseSelectDate"),
                        bundle.getString("error"),
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(bundle.getString("saveCSV"));
            fileChooser.setSelectedFile(new File("rezervari.csv"));

            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDateTime date = LocalDate.parse(dateStr, formatter).atStartOfDay();

                controller.exportRezervariToCsv(currentHotelId, date, fileToSave.getAbsolutePath());

                JOptionPane.showMessageDialog(this,
                        bundle.getString("exportSuccessful"),
                        bundle.getString("success"),
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    bundle.getString("invalidDateFormat"),
                    bundle.getString("error"),
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    bundle.getString("errorExporting") + e.getMessage(),
                    bundle.getString("error"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportRezervariToDoc() {
        try {
            String dateStr = dateField.getText();

            if (dateStr.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        bundle.getString("pleaseSelectDate"),
                        bundle.getString("error"),
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(bundle.getString("saveDOC"));
            fileChooser.setSelectedFile(new File("rezervari.docx"));

            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDateTime date = LocalDate.parse(dateStr, formatter).atStartOfDay();

                controller.exportRezervariToDoc(currentHotelId, date, fileToSave.getAbsolutePath());

                JOptionPane.showMessageDialog(this,
                        bundle.getString("exportSuccessful"),
                        bundle.getString("success"),
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    bundle.getString("invalidDateFormat"),
                    bundle.getString("error"),
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    bundle.getString("errorExporting") + e.getMessage(),
                    bundle.getString("error"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setCurrentCameraId(int cameraId) {
        this.currentCameraId = cameraId;

        // Obține hotelId pentru camera curentă
        try {
            List<Camera> camere = controller.getCameraRepository().getAllCamera();
            for (Camera camera : camere) {
                if (camera.getId() == cameraId) {
                    this.currentHotelId = camera.getIdHotel();
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        loadRezervari();
    }

    private void loadRezervari() {
        try {
            List<Rezervare> rezervari = controller.getRezervariByCamera(currentCameraId);
            listModel.clear();

            for (Rezervare rezervare : rezervari) {
                listModel.addElement(rezervare);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    bundle.getString("errorLoadingReservations") + e.getMessage(),
                    bundle.getString("error"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void update() {
        loadRezervari();
    }

    public void changeLanguage(ResourceBundle bundle) {
        this.bundle = bundle;
        addButton.setText(bundle.getString("add"));
        editButton.setText(bundle.getString("edit"));
        deleteButton.setText(bundle.getString("delete"));
        exportCsvButton.setText(bundle.getString("exportCsv"));
        exportDocButton.setText(bundle.getString("exportDoc"));
        dateLabel.setText(bundle.getString("date") + " (dd-MM-yyyy):");
    }

    public void showAddRezervareDialog(RezervareController controller, JFrame parentFrame, int idCamera) {
        RezervareDialog dialog = new RezervareDialog(parentFrame, null, bundle, idCamera);
        dialog.setVisible(true);

        if (dialog.isApproved()) {
            Rezervare rezervare = dialog.getRezervare();
            controller.saveRezervare(rezervare);
        }
    }

    public void showEditRezervareDialog(RezervareController controller, Rezervare rezervare, JFrame parentFrame) {
        RezervareDialog dialog = new RezervareDialog(parentFrame, rezervare, bundle, rezervare.getIdCamera());
        dialog.setVisible(true);

        if (dialog.isApproved()) {
            Rezervare updatedRezervare = dialog.getRezervare();
            controller.updateRezervare(updatedRezervare);
        }
    }

    private class RezervareListCellRenderer extends DefaultListCellRenderer {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Rezervare) {
                Rezervare rezervare = (Rezervare) value;
                setText(String.format("%s - %s: %s %s",
                        rezervare.getStartDate().format(formatter),
                        rezervare.getEndDate().format(formatter),
                        rezervare.getNumeClient(),
                        rezervare.getPrenumeClient()));
            }
            return this;
        }
    }
}