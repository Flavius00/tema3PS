package org.example.controller.utils;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.example.model.Camera;
import org.example.model.Rezervare;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DocExporter {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public static void exportRezervari(List<Rezervare> rezervari, String filePath) throws IOException {
        try (XWPFDocument document = new XWPFDocument()) {
            // Title
            XWPFParagraph title = document.createParagraph();
            XWPFRun titleRun = title.createRun();
            titleRun.setText("Lista rezervărilor");
            titleRun.setBold(true);
            titleRun.setFontSize(16);

            // Create table
            XWPFTable table = document.createTable(rezervari.size() + 1, 8);

            // Set headers
            XWPFTableRow headerRow = table.getRow(0);
            headerRow.getCell(0).setText("ID");
            headerRow.getCell(1).setText("Data început");
            headerRow.getCell(2).setText("Data sfârșit");
            headerRow.getCell(3).setText("ID Camera");
            headerRow.getCell(4).setText("Nume client");
            headerRow.getCell(5).setText("Prenume client");
            headerRow.getCell(6).setText("Telefon");
            headerRow.getCell(7).setText("Email");

            // Fill data
            for (int i = 0; i < rezervari.size(); i++) {
                Rezervare rezervare = rezervari.get(i);
                XWPFTableRow row = table.getRow(i + 1);

                row.getCell(0).setText(String.valueOf(rezervare.getId()));
                row.getCell(1).setText(rezervare.getStartDate().format(DATE_FORMATTER));
                row.getCell(2).setText(rezervare.getEndDate().format(DATE_FORMATTER));
                row.getCell(3).setText(String.valueOf(rezervare.getIdCamera()));
                row.getCell(4).setText(rezervare.getNumeClient());
                row.getCell(5).setText(rezervare.getPrenumeClient());
                row.getCell(6).setText(rezervare.getTelefonClient());
                row.getCell(7).setText(rezervare.getEmailClient());
            }

            // Save the document
            try (FileOutputStream out = new FileOutputStream(filePath)) {
                document.write(out);
            }
        }
    }

    public static void exportCamere(List<Camera> camere, String filePath) throws IOException {
        try (XWPFDocument document = new XWPFDocument()) {
            // Title
            XWPFParagraph title = document.createParagraph();
            XWPFRun titleRun = title.createRun();
            titleRun.setText("Lista camerelor");
            titleRun.setBold(true);
            titleRun.setFontSize(16);

            // Create table
            XWPFTable table = document.createTable(camere.size() + 1, 5);

            // Set headers
            XWPFTableRow headerRow = table.getRow(0);
            headerRow.getCell(0).setText("ID");
            headerRow.getCell(1).setText("ID Hotel");
            headerRow.getCell(2).setText("Număr cameră");
            headerRow.getCell(3).setText("Preț per noapte");
            headerRow.getCell(4).setText("ID Poze");

            // Fill data
            for (int i = 0; i < camere.size(); i++) {
                Camera camera = camere.get(i);
                XWPFTableRow row = table.getRow(i + 1);

                row.getCell(0).setText(String.valueOf(camera.getId()));
                row.getCell(1).setText(String.valueOf(camera.getIdHotel()));
                row.getCell(2).setText(camera.getNrCamera());
                row.getCell(3).setText(String.valueOf(camera.getPretPerNoapte()));
                row.getCell(4).setText(String.valueOf(camera.getIdPoze()));
            }

            // Save the document
            try (FileOutputStream out = new FileOutputStream(filePath)) {
                document.write(out);
            }
        }
    }
}