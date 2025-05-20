package org.example.controller.utils;

import org.example.model.Camera;
import org.example.model.Rezervare;

import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CsvExporter {
    private static final String SEPARATOR = ",";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public static void exportRezervari(List<Rezervare> rezervari, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Header
            writer.append("ID,Data început,Data sfârșit,ID Camera,Nume client,Prenume client,Telefon,Email\n");

            // Data
            for (Rezervare rezervare : rezervari) {
                writer.append(String.valueOf(rezervare.getId())).append(SEPARATOR);
                writer.append(rezervare.getStartDate().format(DATE_FORMATTER)).append(SEPARATOR);
                writer.append(rezervare.getEndDate().format(DATE_FORMATTER)).append(SEPARATOR);
                writer.append(String.valueOf(rezervare.getIdCamera())).append(SEPARATOR);
                writer.append(rezervare.getNumeClient()).append(SEPARATOR);
                writer.append(rezervare.getPrenumeClient()).append(SEPARATOR);
                writer.append(rezervare.getTelefonClient()).append(SEPARATOR);
                writer.append(rezervare.getEmailClient()).append("\n");
            }
        }
    }

    public static void exportCamere(List<Camera> camere, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Header
            writer.append("ID,ID Hotel,Număr cameră,Preț per noapte,ID Poze\n");

            // Data
            for (Camera camera : camere) {
                writer.append(String.valueOf(camera.getId())).append(SEPARATOR);
                writer.append(String.valueOf(camera.getIdHotel())).append(SEPARATOR);
                writer.append(camera.getNrCamera()).append(SEPARATOR);
                writer.append(String.valueOf(camera.getPretPerNoapte())).append(SEPARATOR);
                writer.append(String.valueOf(camera.getIdPoze())).append("\n");
            }
        }
    }
}