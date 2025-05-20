package org.example.view;

import org.example.model.Camera;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

public class CameraDialog extends JDialog {
    private JComboBox<Integer> hotelComboBox;
    private JTextField nrCameraField;
    private JTextField pretField;
    private JComboBox<Integer> pozeComboBox;

    private JButton saveButton;
    private JButton cancelButton;

    private boolean approved = false;
    private Camera camera;
    private ResourceBundle bundle;

    public CameraDialog(JFrame parent, Camera camera, ResourceBundle bundle) {
        super(parent, true);
        this.camera = camera;
        this.bundle = bundle;

        if (camera == null) {
            setTitle(bundle.getString("add") + " " + bundle.getString("room").toLowerCase());
            this.camera = new Camera();
        } else {
            setTitle(bundle.getString("edit") + " " + bundle.getString("room").toLowerCase());
        }

        initComponents();
        loadData();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
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
        // Adaugarea hotelurilor ar trebui facuta de controller
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
        // Adaugarea ID-urilor de poze ar trebui facuta de controller
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
            hotelComboBox.setSelectedItem(camera.getIdHotel());
            nrCameraField.setText(camera.getNrCamera());
            pretField.setText(String.valueOf(camera.getPretPerNoapte()));
            pozeComboBox.setSelectedItem(camera.getIdPoze());
        }
    }

    public boolean isApproved() {
        return approved;
    }

    public Camera getCamera() {
        if (approved) {
            camera.setIdHotel((Integer) hotelComboBox.getSelectedItem());
            camera.setNrCamera(nrCameraField.getText());
            try {
                camera.setPretPerNoapte(Float.parseFloat(pretField.getText()));
            } catch (NumberFormatException e) {
                camera.setPretPerNoapte(0);
            }
            camera.setIdPoze((Integer) pozeComboBox.getSelectedItem());
        }
        return camera;
    }

    public void setHoteluri(Integer[] hoteluri) {
        hotelComboBox.removeAllItems();
        for (Integer idHotel : hoteluri) {
            hotelComboBox.addItem(idHotel);
        }
    }

    public void setPoze(Integer[] poze) {
        pozeComboBox.removeAllItems();
        for (Integer idPoze : poze) {
            pozeComboBox.addItem(idPoze);
        }
    }

    public void setSelectedHotel(int idHotel) {
        hotelComboBox.setSelectedItem(idHotel);
    }
}