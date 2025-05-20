package org.example.view;

import org.example.controller.HotelController;
import org.example.model.Hotel;
import org.example.model.Lant;
import org.example.model.Locatie;
import org.example.model.Observer;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class HotelView extends JPanel implements Observer {
    private HotelController controller;
    private JList<Hotel> hotelList;
    private DefaultListModel<Hotel> listModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton viewCamereButton;
    private ResourceBundle bundle;

    public HotelView(HotelController controller, ResourceBundle bundle) {
        this.controller = controller;
        this.bundle = bundle;
        controller.addObserver(this);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        hotelList = new JList<>(listModel);
        hotelList.setCellRenderer(new HotelListCellRenderer());
        hotelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(hotelList);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        addButton = new JButton(bundle.getString("add"));
        editButton = new JButton(bundle.getString("edit"));
        deleteButton = new JButton(bundle.getString("delete"));
        viewCamereButton = new JButton(bundle.getString("viewRooms"));

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewCamereButton);

        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> controller.showAddHotelDialog());

        editButton.addActionListener(e -> {
            Hotel selectedHotel = hotelList.getSelectedValue();
            if (selectedHotel != null) {
                controller.showEditHotelDialog(selectedHotel);
            }
        });

        deleteButton.addActionListener(e -> {
            Hotel selectedHotel = hotelList.getSelectedValue();
            if (selectedHotel != null) {
                controller.deleteHotel(selectedHotel.getId());
            }
        });

        viewCamereButton.addActionListener(e -> {
            Hotel selectedHotel = hotelList.getSelectedValue();
            if (selectedHotel != null) {
                controller.selectHotel(selectedHotel);
                controller.showCamereForHotel();
            }
        });

        loadHotels();
    }

    private void loadHotels() {
        try {
            List<Hotel> hotels = controller.getAllHotels();
            listModel.clear();

            for (Hotel hotel : hotels) {
                listModel.addElement(hotel);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    bundle.getString("errorLoadingHotels") + e.getMessage(),
                    bundle.getString("error"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void update() {
        loadHotels();
    }

    public void changeLanguage(ResourceBundle bundle) {
        this.bundle = bundle;
        addButton.setText(bundle.getString("add"));
        editButton.setText(bundle.getString("edit"));
        deleteButton.setText(bundle.getString("delete"));
        viewCamereButton.setText(bundle.getString("viewRooms"));
    }

    public void showAddHotelDialog(HotelController controller, JFrame parentFrame) throws SQLException {
        List<Lant> lanturi = controller.getAllLanturi();
        List<Locatie> locatii = controller.getAllLocatii();

        HotelDialog dialog = new HotelDialog(parentFrame, null, bundle, lanturi, locatii);
        dialog.setVisible(true);

        if (dialog.isApproved()) {
            Hotel hotel = dialog.getHotel();
            controller.saveHotel(hotel);
        }
    }

    public void showEditHotelDialog(HotelController controller, Hotel hotel, JFrame parentFrame) throws SQLException {
        List<Lant> lanturi = controller.getAllLanturi();
        List<Locatie> locatii = controller.getAllLocatii();

        HotelDialog dialog = new HotelDialog(parentFrame, hotel, bundle, lanturi, locatii);
        dialog.setVisible(true);

        if (dialog.isApproved()) {
            Hotel updatedHotel = dialog.getHotel();
            controller.updateHotel(updatedHotel);
        }
    }

    private class HotelListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Hotel) {
                Hotel hotel = (Hotel) value;
                setText(hotel.getNume());
            }
            return this;
        }
    }
}