package org.example.view;

import org.example.model.Hotel;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

public class HotelDialog extends JDialog {
    private JTextField numeField;
    private JComboBox<Integer> locatieComboBox;
    private JTextField telefonField;
    private JTextField emailField;
    private JTextArea facilitatiArea;
    private JComboBox<Integer> lantComboBox;

    private JButton saveButton;
    private JButton cancelButton;

    private boolean approved = false;
    private Hotel hotel;
    private ResourceBundle bundle;

    public HotelDialog(JFrame parent, Hotel hotel, ResourceBundle bundle) {
        super(parent, true);
        this.hotel = hotel;
        this.bundle = bundle;

        if (hotel == null) {
            setTitle(bundle.getString("add") + " " + bundle.getString("hotel").toLowerCase());
            this.hotel = new Hotel();
        } else {
            setTitle(bundle.getString("edit") + " " + bundle.getString("hotel").toLowerCase());
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

        // Nume
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel(bundle.getString("name") + ":"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        numeField = new JTextField(20);
        formPanel.add(numeField, gbc);

        // Locatie
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Locație:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        locatieComboBox = new JComboBox<>();
        // Adaugarea locatiilor ar trebui facuta de controller
        formPanel.add(locatieComboBox, gbc);

        // Telefon
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel(bundle.getString("phone") + ":"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        telefonField = new JTextField(20);
        formPanel.add(telefonField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel(bundle.getString("email") + ":"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Facilitati
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel(bundle.getString("facilities") + ":"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        facilitatiArea = new JTextArea(5, 20);
        facilitatiArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(facilitatiArea);
        formPanel.add(scrollPane, gbc);

        // Lant
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Lanț:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        lantComboBox = new JComboBox<>();
        // Adaugarea lanturilor ar trebui facuta de controller
        formPanel.add(lantComboBox, gbc);

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
        if (hotel.getId() != 0) {
            numeField.setText(hotel.getNume());
            locatieComboBox.setSelectedItem(hotel.getIdLocatie());
            telefonField.setText(hotel.getNrTelefon());
            emailField.setText(hotel.getEmail());
            facilitatiArea.setText(hotel.getFacilitati());
            lantComboBox.setSelectedItem(hotel.getIdLant());
        }
    }

    public boolean isApproved() {
        return approved;
    }

    public Hotel getHotel() {
        if (approved) {
            hotel.setNume(numeField.getText());
            hotel.setIdLocatie((Integer) locatieComboBox.getSelectedItem());
            hotel.setNrTelefon(telefonField.getText());
            hotel.setEmail(emailField.getText());
            hotel.setFacilitati(facilitatiArea.getText());
            hotel.setIdLant((Integer) lantComboBox.getSelectedItem());
        }
        return hotel;
    }

    public void setLocatii(Integer[] locatii) {
        locatieComboBox.removeAllItems();
        for (Integer idLocatie : locatii) {
            locatieComboBox.addItem(idLocatie);
        }
    }

    public void setLanturi(Integer[] lanturi) {
        lantComboBox.removeAllItems();
        for (Integer idLant : lanturi) {
            lantComboBox.addItem(idLant);
        }
    }
}