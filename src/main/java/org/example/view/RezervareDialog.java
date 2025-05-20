package org.example.view;

import org.example.model.Rezervare;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

public class RezervareDialog extends JDialog {
    private JTextField startDateField;
    private JTextField endDateField;
    private JComboBox<Integer> cameraComboBox;
    private JTextField numeClientField;
    private JTextField prenumeClientField;
    private JTextField telefonClientField;
    private JTextField emailClientField;

    private JButton saveButton;
    private JButton cancelButton;

    private boolean approved = false;
    private Rezervare rezervare;
    private ResourceBundle bundle;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public RezervareDialog(JFrame parent, Rezervare rezervare, ResourceBundle bundle) {
        super(parent, true);
        this.rezervare = rezervare;
        this.bundle = bundle;

        if (rezervare == null) {
            setTitle(bundle.getString("add") + " " + bundle.getString("reservation").toLowerCase());
            this.rezervare = new Rezervare(0, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 0, "", "", "", "");
        } else {
            setTitle(bundle.getString("edit") + " " + bundle.getString("reservation").toLowerCase());
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

        // Data început
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Data început:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        startDateField = new JTextField(20);
        formPanel.add(startDateField, gbc);

        // Data sfârșit
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Data sfârșit:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        endDateField = new JTextField(20);
        formPanel.add(endDateField, gbc);

        // Camera
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel(bundle.getString("room") + ":"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        cameraComboBox = new JComboBox<>();
        // Adaugarea camerelor ar trebui facuta de controller
        formPanel.add(cameraComboBox, gbc);

        // Nume client
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Nume client:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        numeClientField = new JTextField(20);
        formPanel.add(numeClientField, gbc);

        // Prenume client
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Prenume client:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        prenumeClientField = new JTextField(20);
        formPanel.add(prenumeClientField, gbc);

        // Telefon client
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Telefon client:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        telefonClientField = new JTextField(20);
        formPanel.add(telefonClientField, gbc);

        // Email client
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Email client:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        emailClientField = new JTextField(20);
        formPanel.add(emailClientField, gbc);

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
        if (rezervare.getId() != 0) {
            startDateField.setText(rezervare.getStartDate().format(DATE_FORMATTER));
            endDateField.setText(rezervare.getEndDate().format(DATE_FORMATTER));
            cameraComboBox.setSelectedItem(rezervare.getIdCamera());
            numeClientField.setText(rezervare.getNumeClient());
            prenumeClientField.setText(rezervare.getPrenumeClient());
            telefonClientField.setText(rezervare.getTelefonClient());
            emailClientField.setText(rezervare.getEmailClient());
        } else {
            startDateField.setText(LocalDateTime.now().format(DATE_FORMATTER));
            endDateField.setText(LocalDateTime.now().plusDays(1).format(DATE_FORMATTER));
        }
    }

    public boolean isApproved() {
        return approved;
    }

    public Rezervare getRezervare() {
        if (approved) {
            try {
                LocalDateTime startDate = LocalDateTime.parse(startDateField.getText(), DATE_FORMATTER);
                LocalDateTime endDate = LocalDateTime.parse(endDateField.getText(), DATE_FORMATTER);

                rezervare.setStartDate(startDate);
                rezervare.setEndDate(endDate);
                rezervare.setIdCamera((Integer) cameraComboBox.getSelectedItem());
                rezervare.setNumeClient(numeClientField.getText());
                rezervare.setPrenumeClient(prenumeClientField.getText());
                rezervare.setTelefonClient(telefonClientField.getText());
                rezervare.setEmailClient(emailClientField.getText());
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Format dată invalid. Utilizați formatul: dd-MM-yyyy HH:mm",
                        "Eroare", JOptionPane.ERROR_MESSAGE);
                approved = false;
            }
        }
        return rezervare;
    }

    public void setCamere(Integer[] camere) {
        cameraComboBox.removeAllItems();
        for (Integer idCamera : camere) {
            cameraComboBox.addItem(idCamera);
        }
    }

    public void setSelectedCamera(int idCamera) {
        cameraComboBox.setSelectedItem(idCamera);
    }
}