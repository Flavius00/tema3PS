package org.example.controller;

import org.example.controller.utils.CsvExporter;
import org.example.controller.utils.DocExporter;
import org.example.model.Camera;
import org.example.model.Hotel;
import org.example.model.repository.CameraRepository;
import org.example.model.repository.HotelRepository;
import org.example.model.Observable;
import org.example.view.CameraView;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

public class CameraController extends Observable {
    private CameraRepository cameraRepository;
    private HotelRepository hotelRepository;
    private RezervareController rezervareController;
    private CameraView view;
    private MainFrameController mainController;

    public CameraController(CameraRepository cameraRepository, HotelRepository hotelRepository,
                            RezervareController rezervareController) {
        this.cameraRepository = cameraRepository;
        this.hotelRepository = hotelRepository;
        this.rezervareController = rezervareController;
    }

    public void setMainController(MainFrameController mainController) {
        this.mainController = mainController;
    }

    public void setView(CameraView view) {
        this.view = view;
    }

    public List<Camera> getAllCamere() throws SQLException {
        return cameraRepository.getAllCamera();
    }

    public List<Hotel> getAllHotels() throws SQLException {
        return hotelRepository.getAllHotels();
    }

    public List<Camera> getCamereByHotel(int idHotel) throws SQLException {
        return cameraRepository.getCamereByHotel(idHotel);
    }

    public boolean saveCamera(Camera camera) {
        boolean success = cameraRepository.save(camera);
        if (success) {
            notifyObservers();
        }
        return success;
    }

    public boolean updateCamera(Camera camera) {
        boolean success = cameraRepository.update(camera);
        if (success) {
            notifyObservers();
        }
        return success;
    }

    public boolean deleteCamera(int id) {
        boolean success = cameraRepository.delete(id);
        if (success) {
            notifyObservers();
        }
        return success;
    }

    public List<Camera> filterCamereByPret(int idHotel, float pretMin, float pretMax) throws SQLException {
        return getCamereByHotel(idHotel).stream()
                .filter(camera -> camera.getPretPerNoapte() >= pretMin && camera.getPretPerNoapte() <= pretMax)
                .collect(Collectors.toList());
    }

    public List<Camera> getCamereLibereByDate(int idHotel, LocalDateTime date) throws SQLException {
        List<Camera> toateCamerele = getCamereByHotel(idHotel);
        List<Integer> camereRezervate = rezervareController.getCamereRezervateByDate(date);

        return toateCamerele.stream()
                .filter(camera -> !camereRezervate.contains(camera.getId()))
                .collect(Collectors.toList());
    }

    public List<Camera> getCamereRezervateByDate(int idHotel, LocalDateTime date) throws SQLException {
        List<Camera> toateCamerele = getCamereByHotel(idHotel);
        List<Integer> camereRezervate = rezervareController.getCamereRezervateByDate(date);

        return toateCamerele.stream()
                .filter(camera -> camereRezervate.contains(camera.getId()))
                .collect(Collectors.toList());
    }

    public void showAddCameraDialog() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
        try {
            view.showAddCameraDialog(this, parentFrame);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parentFrame, "Eroare la deschiderea dialogului: " + e.getMessage(),
                    "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showEditCameraDialog(Camera camera) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
        try {
            view.showEditCameraDialog(this, camera, parentFrame);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parentFrame, "Eroare la deschiderea dialogului: " + e.getMessage(),
                    "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showFilterResults() {
        try {
            int currentHotelId = view.getCurrentHotelId();
            String pretMinStr = view.getPretMin();
            String pretMaxStr = view.getPretMax();
            String dateStr = view.getDate();

            List<Camera> filteredCamere;

            if (!pretMinStr.isEmpty() && !pretMaxStr.isEmpty()) {
                float pretMin = Float.parseFloat(pretMinStr);
                float pretMax = Float.parseFloat(pretMaxStr);
                filteredCamere = filterCamereByPret(currentHotelId, pretMin, pretMax);
                view.displayCamere(filteredCamere);
            } else if (!dateStr.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDateTime date = LocalDate.parse(dateStr, formatter).atStartOfDay();

                List<Camera> camereLibere = getCamereLibereByDate(currentHotelId, date);
                view.displayCamere(camereLibere);
            } else {
                // Afișează toate camerele dacă nu sunt specificate filtre
                List<Camera> allCamere = getCamereByHotel(currentHotelId);
                view.displayCamere(allCamere);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Format număr invalid", "Eroare", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(view, "Format dată invalid. Utilizați formatul: dd-MM-yyyy",
                    "Eroare", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Eroare la filtrarea camerelor: " + e.getMessage(),
                    "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showRezervariForCamera(Camera camera) {
        if (camera != null && mainController != null) {
            mainController.showRezervariTab(camera.getId());
        }
    }

    public void exportCamereToCSV() {
        try {
            int hotelId = view.getCurrentHotelId();

            // Obține toate camerele pentru hotelul curent sau camerele filtrate
            List<Camera> camere = getCamereByHotel(hotelId);

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Salvare CSV");
            fileChooser.setSelectedFile(new File("camere.csv"));

            int userSelection = fileChooser.showSaveDialog(view);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                CsvExporter.exportCamere(camere, fileToSave.getAbsolutePath());

                JOptionPane.showMessageDialog(view, "Export realizat cu succes",
                        "Succes", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException | IOException e) {
            JOptionPane.showMessageDialog(view, "Eroare la exportare: " + e.getMessage(),
                    "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void exportCamereToDoc() {
        try {
            int hotelId = view.getCurrentHotelId();

            // Obține toate camerele pentru hotelul curent sau camerele filtrate
            List<Camera> camere = getCamereByHotel(hotelId);

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Salvare DOC");
            fileChooser.setSelectedFile(new File("camere.docx"));

            int userSelection = fileChooser.showSaveDialog(view);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                DocExporter.exportCamere(camere, fileToSave.getAbsolutePath());

                JOptionPane.showMessageDialog(view, "Export realizat cu succes",
                        "Succes", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException | IOException e) {
            JOptionPane.showMessageDialog(view, "Eroare la exportare: " + e.getMessage(),
                    "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void exportCamereRezervateToCSV() {
        try {
            int hotelId = view.getCurrentHotelId();
            String dateStr = view.getDate();

            if (dateStr.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Selectați o dată", "Eroare", JOptionPane.ERROR_MESSAGE);
                return;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDateTime date = LocalDate.parse(dateStr, formatter).atStartOfDay();

            List<Camera> camereRezervate = getCamereRezervateByDate(hotelId, date);

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Salvare CSV");
            fileChooser.setSelectedFile(new File("camere_rezervate.csv"));

            int userSelection = fileChooser.showSaveDialog(view);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                CsvExporter.exportCamere(camereRezervate, fileToSave.getAbsolutePath());

                JOptionPane.showMessageDialog(view, "Export realizat cu succes",
                        "Succes", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException | IOException | DateTimeParseException e) {
            JOptionPane.showMessageDialog(view, "Eroare la exportare: " + e.getMessage(),
                    "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void exportCamereRezervateToDoc() {
        try {
            int hotelId = view.getCurrentHotelId();
            String dateStr = view.getDate();

            if (dateStr.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Selectați o dată", "Eroare", JOptionPane.ERROR_MESSAGE);
                return;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDateTime date = LocalDate.parse(dateStr, formatter).atStartOfDay();

            List<Camera> camereRezervate = getCamereRezervateByDate(hotelId, date);

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Salvare DOC");
            fileChooser.setSelectedFile(new File("camere_rezervate.docx"));

            int userSelection = fileChooser.showSaveDialog(view);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                DocExporter.exportCamere(camereRezervate, fileToSave.getAbsolutePath());

                JOptionPane.showMessageDialog(view, "Export realizat cu succes",
                        "Succes", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException | IOException | DateTimeParseException e) {
            JOptionPane.showMessageDialog(view, "Eroare la exportare: " + e.getMessage(),
                    "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }
}