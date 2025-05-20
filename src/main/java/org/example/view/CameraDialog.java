package org.example.view;

import org.example.model.Camera;
import org.example.model.Hotel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ResourceBundle;

public class CameraDialog extends JDialog {
    private JComboBox<Hotel> hotelComboBox;
    private JTextField nrCameraField;
    private JTextField pretField;
    private JComboBox<Integer> pozeComboBox;

    private JButton saveButton;
    private JButton cancelButton;

    private boolean approved = false;
    private Camera camera;
    private ResourceBundle bundle;

    public CameraDialog(JFrame parent, Camera camera, ResourceBundle bundle, List<Hotel> hoteluri, Integer[] pozeIds) {
        super(parent, true);
        this.camera = camera;
        this.bundle = bundle;

        if (camera == null) {
            setTitle(bundle.getString("add") + " " + bundle.getString("room").toLowerCase());
            this.camera = new Camera();
        } else {
            setTitle(bundle.getString("edit") + " " + bundle.getString("room").toLowerCase());
        }

        initComponents(hoteluri, pozeIds);
        loadData();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents(List<Hotel> hoteluri, Integer[] pozeIds) {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Hotel
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel(bundle.getString("hotel") + ":"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        hotelComboBox = new JComboBox<>();
        for (Hotel hotel : hoteluri) {
            hotelComboBox.addItem(hotel);
        }
        formPanel.add(hotelComboBox, gbc);

        // Numar camera
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel(bundle.getString("room") + ":"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        nrCameraField = new JTextField(20);
        formPanel.add(nrCameraField, gbc);

        // Pret per noapte
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel(bundle.getString("pricePerNight") + ":"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        pretField = new JTextField(20);
        formPanel.add(pretField, gbc);

        // Poze
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Poze:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        pozeComboBox = new JComboBox<>();
        for (Integer id : pozeIds) {
            pozeComboBox.addItem(id);
        }
        formPanel.add(pozeComboBox, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        saveButton = new JButton(bundle.getString("save"));
        cancelButton = new JButton(bundle.getString("cancel"));

        saveButton.addActionListener(e -> {
            approved = true;
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        if (camera.getId() != 0) {
            // Attempt to select the correct Hotel based on ID
            for (int i = 0; i < hotelComboBox.getItemCount(); i++) {
                Hotel hotel = hotelComboBox.getItemAt(i);
                if (hotel.getId() == camera.getIdHotel()) {
                    hotelComboBox.setSelectedIndex(i);
                    break;
                }
            }

            nrCameraField.setText(camera.getNrCamera());
            pretField.setText(String.valueOf(camera.getPretPerNoapte()));

            // Attempt to select the correct Poze ID
            for (int i = 0; i < pozeComboBox.getItemCount(); i++) {
                Integer id = pozeComboBox.getItemAt(i);
                if (id == camera.getIdPoze()) {
                    pozeComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    public boolean isApproved() {
        return approved;
    }

    public Camera getCamera() {
        if (approved) {
            Hotel selectedHotel = (Hotel) hotelComboBox.getSelectedItem();
            if (selectedHotel != null) {
                camera.setIdHotel(selectedHotel.getId());
            }

            camera.setNrCamera(nrCameraField.getText());

            try {
                camera.setPretPerNoapte(Float.parseFloat(pretField.getText()));
            } catch (NumberFormatException e) {
                camera.setPretPerNoapte(0);
            }

            Integer selectedPozeId = (Integer) pozeComboBox.getSelectedItem();
            if (selectedPozeId != null) {
                camera.setIdPoze(selectedPozeId);
            }
        }
        return camera;
    }

    public void preSelectHotel(int hotelId) {
        for (int i = 0; i < hotelComboBox.getItemCount(); i++) {
            Hotel hotel = hotelComboBox.getItemAt(i);
            if (hotel.getId() == hotelId) {
                hotelComboBox.setSelectedIndex(i);
                break;
            }
        }
    }
}