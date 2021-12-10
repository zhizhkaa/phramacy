package com.pharmacy;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.stage.Window;

public class MainController {

    // Само окно
    @FXML private SplitPane mainPane;

    // Настройки аккаунта
    @FXML private Pane accountSettings;

    // Работа с данными
    @FXML private Pane tables;

    // Работа с отчётами
    @FXML private Pane reports;

    // Работа с аккаунтами
    @FXML private Pane accountsAdmin;

    // Статистика сервера
    @FXML private Pane statsAdmin;

    public void onAccountSetButtonPressed(ActionEvent event) {

        accountSettings.setVisible(true);
        tables.setVisible(false);
        reports.setVisible(false);
        accountsAdmin.setVisible(false);
        statsAdmin.setVisible(false);
    }

    public void onTablesButtonPressed(ActionEvent event) {
        accountSettings.setVisible(false);
        tables.setVisible(true);
        reports.setVisible(false);
        accountsAdmin.setVisible(false);
        statsAdmin.setVisible(false);
    }

    public void onReportsButtonPressed(ActionEvent event) {
        accountSettings.setVisible(false);
        tables.setVisible(false);
        reports.setVisible(true);
        accountsAdmin.setVisible(false);
        statsAdmin.setVisible(false);
    }

    public void onAccAButtonPressed(ActionEvent event) {
        accountSettings.setVisible(false);
        tables.setVisible(false);
        reports.setVisible(false);
        accountsAdmin.setVisible(true);
        statsAdmin.setVisible(false);
    }

    public void onStatsAButtonPressed(ActionEvent event) {
        accountSettings.setVisible(false);
        tables.setVisible(false);
        reports.setVisible(false);
        accountsAdmin.setVisible(false);
        statsAdmin.setVisible(true);
    }
}
