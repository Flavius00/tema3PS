package org.example;

import org.example.controller.MainFrameController;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrameController mainController = new MainFrameController();
            mainController.showApplication();
        });
    }
}