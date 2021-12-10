package com.pharmacy;

import com.pharmacy.classes.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.json.JSONException;

public class MainController {

    /*
    Я немного изменил структуру этого дока.
     */

    private User user;

    // Кнопки недоступные пользователю
    @FXML private Button accountsButton;
    @FXML private Button statsButton;

    public void setUser(User user) throws JSONException {
        this.user = user;
        userName.setText(user.getLogin());
        int access = user.getAccess();

        if (access == 0) {
            role.setText("Пользватель");
            // Скрываем кнопки "Аккаунты пользователей" и "Статистика"
            // Для безопасности еще отключаем аналогичные поля
            accountsButton.setVisible(false);
            accountsAdmin.setDisable(true);
            statsButton.setVisible(false);
            statsAdmin.setDisable(true);
        }
        else if (access == 1) { role.setText("Администратор"); }

        user.checkUser();
    }


    // ВКЛАДКИ:
    // Настройки аккаунта
    public void onAccountSetButtonPressed() {
        accountSettings.setVisible(true);
        tables.setVisible(false);
        reports.setVisible(false);
        accountsAdmin.setVisible(false);
        statsAdmin.setVisible(false);
    }

    @FXML private Pane accountSettings;
        @FXML private TextField userName;
        @FXML private TextField role;

        // TODO Смена пароля
        @FXML private Button showPasswordA;
            @FXML private PasswordField oldPasswordHide;
            @FXML private TextField oldPasswordShown;
        @FXML private Button showPasswordB;
            @FXML private PasswordField newPasswordHide;
            @FXML private TextField newPasswordShown;
        @FXML private Button showPasswordC;
            @FXML private PasswordField newPasswordHideA;
            @FXML private TextField newPasswordShownA;

        @FXML private Button changePassword;

    // TODO Проверка старого пароля,                ОК - Дальше, НЕОК - Неправильный пароль
    // TODO Смотрим чтобы новые пароли совпадали,   ОК - Дальше, НЕОК - Пароли не совпадают
    // TODO Ищем пользователя в users
    // TODO Меняем пароль,                          Уведомление "Успешная смена пароля"
    public void onChangePasswordPressed(ActionEvent event) {
        String oldPassword = this.oldPasswordHide.getText();
        String newPassword = newPasswordHide.getText();

        if (oldPassword.isEmpty()) {
            oldPasswordHide.setPromptText("ВЫ не ввели пароль");
            oldPasswordShown.setPromptText("Вы не ввели пароль");
        }


    }

    // Метод для кнопок скрытия пароля
    public void hideButton(PasswordField hide, TextField show) {
        if (hide.isVisible()) {
            hide.setVisible(false);
            show.setText(hide.getText());
            show.setVisible(true);
        }
        else if (show.isVisible()) {
            show.setVisible(false);
            hide.setText(show.getText());
            hide.setVisible(true);
        }
    }
    // Нажатие кнопок скрытия, вызывают метод hideButton
    public void showPressedA() { hideButton(oldPasswordHide, oldPasswordShown);}
    public void showPressedB() { hideButton(newPasswordHide, newPasswordShownA);}
    public void showPressedC() { hideButton(newPasswordHideA, newPasswordShownA);}


        // TODO Привязать Email


    // Работа с данными
    public void onTablesButtonPressed(ActionEvent event) {
        accountSettings.setVisible(false);
        tables.setVisible(true);
        reports.setVisible(false);
        accountsAdmin.setVisible(false);
        statsAdmin.setVisible(false);
    }
    @FXML private Pane tables;

    // Работа с отчётами
    public void onReportsButtonPressed(ActionEvent event) {
        accountSettings.setVisible(false);
        tables.setVisible(false);
        reports.setVisible(true);
        accountsAdmin.setVisible(false);
        statsAdmin.setVisible(false);
    }
    @FXML private Pane reports;

    // Работа с аккаунтами
    public void onAccAButtonPressed(ActionEvent event) {
        accountSettings.setVisible(false);
        tables.setVisible(false);
        reports.setVisible(false);
        accountsAdmin.setVisible(true);
        statsAdmin.setVisible(false);
    }
    @FXML private Pane accountsAdmin;

    // Статистика сервера
    public void onStatsAButtonPressed(ActionEvent event) {
        accountSettings.setVisible(false);
        tables.setVisible(false);
        reports.setVisible(false);
        accountsAdmin.setVisible(false);
        statsAdmin.setVisible(true);
    }
    @FXML private Pane statsAdmin;














}
