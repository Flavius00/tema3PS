package org.example.controller.utils;

import org.example.model.Camera;
import org.example.model.Hotel;
import org.example.model.Lant;
import org.example.model.Locatie;
import org.example.model.Rezervare;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ViewHelper {

    public static <T> void populateListModel(DefaultListModel<T> model, List<T> items) {
        model.clear();
        items.forEach(model::addElement);
    }

    public static <T> void populateComboBox(JComboBox<T> comboBox, List<T> items) {
        comboBox.removeAllItems();
        items.forEach(comboBox::addItem);
    }

    public static void showErrorMessage(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfoMessage(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showConfirmDialog(Component parent, String message, String title, Runnable onConfirm) {
        int result = JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            onConfirm.run();
        }
    }

    public static LocalDateTime parseDate(String dateStr, DateTimeFormatter formatter) throws DateTimeParseException {
        LocalDate date = LocalDate.parse(dateStr, formatter);
        return date.atStartOfDay();
    }

    public static File showSaveFileDialog(Component parent, String title, String defaultFileName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(title);
        fileChooser.setSelectedFile(new File(defaultFileName));

        int userSelection = fileChooser.showSaveDialog(parent);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }

        return null;
    }

    public static <T> void tryWithErrorHandling(Component parent, String title, ThrowingConsumer<T> action, T parameter) {
        try {
            action.accept(parameter);
        } catch (Exception e) {
            showErrorMessage(parent, e.getMessage(), title);
        }
    }

    public static <T, R> R tryWithErrorHandling(Component parent, String title, ThrowingFunction<T, R> action, T parameter, R defaultValue) {
        try {
            return action.apply(parameter);
        } catch (Exception e) {
            showErrorMessage(parent, e.getMessage(), title);
            return defaultValue;
        }
    }

    @FunctionalInterface
    public interface ThrowingConsumer<T> {
        void accept(T t) throws Exception;
    }

    @FunctionalInterface
    public interface ThrowingFunction<T, R> {
        R apply(T t) throws Exception;
    }
}