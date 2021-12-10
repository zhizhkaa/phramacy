package com.pharmacy;

import com.pharmacy.classes.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Window;
import org.json.JSONException;

public class MainController {
    // Настройки аккаунта
    @FXML private Pane accountSettings;
    @FXML private TextField userName;
    @FXML private TextField role;

    // Работа с данными
    @FXML private Pane tables;

    // Работа с отчётами
    @FXML private Pane reports;

    // Работа с аккаунтами
    @FXML private Pane accountsAdmin;

    // Статистика сервера
    @FXML private Pane statsAdmin;

    private User user;

    public void setUser(User user) throws JSONException {
        this.user = user;
        userName.setText(user.getLogin());
        int access = user.getAccess();

        if (access == 0) { role.setText("Пользватель"); }
        else if (access == 1) { role.setText("Администратор"); }

        user.checkUser();
    }

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
