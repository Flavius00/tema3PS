package org.example.view;

import org.example.controller.RezervareController;
import org.example.model.Rezervare;
import org.example.model.Observer;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class RezervareView extends JPanel implements Observer {
    private RezervareController controller;
    private JList<Rezervare> rezervareList;
    private DefaultListModel<Rezervare> listModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private ResourceBundle bundle;
    private int currentCameraId;

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

        JPanel buttonPanel = new JPanel();
        addButton = new JButton(bundle.getString("add"));
        editButton = new JButton(bundle.getString("edit"));
        deleteButton = new JButton(bundle.getString("delete"));

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

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
    }

    public void setCurrentCameraId(int cameraId) {
        this.currentCameraId = cameraId;
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

    public void changeLanguage(ResourceBundle bundle) {
        this.bundle = bundle;
        addButton.setText(bundle.getString("add"));
        editButton.setText(bundle.getString("edit"));
        deleteButton.setText(bundle.getString("delete"));
    }
}