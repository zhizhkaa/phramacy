package com.pharmacy;

import com.pharmacy.classes.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


        // Старый пароль
            @FXML private PasswordField oldPasswordHide;
            @FXML private TextField oldPasswordShown;
        // Новый пароль
            @FXML private PasswordField newPasswordHide;
            @FXML private TextField newPasswordShown;
        // Повторить новый пароль
            @FXML private PasswordField newPasswordHideA;
            @FXML private TextField newPasswordShownA;

    // Нажатие на кнопку "Сменить пароль"
    public void onChangePasswordPressed() throws JSONException {
        String oldPassword = "";
        String newPassword = "";

        // Проверка на ввод
        if ((oldPasswordShown.getText().trim().isEmpty()) && (oldPasswordHide.getText().trim().isEmpty())) {
            oldPasswordHide.setPromptText("Вы не ввели пароль");
            oldPasswordShown.setPromptText("Вы не ввели пароль");
        }
        else if ((newPasswordShown.getText().trim().isEmpty()) && (newPasswordHide.getText().trim().isEmpty())) {
            newPasswordShown.setPromptText("Вы не ввели новый пароль");
            newPasswordHide.setPromptText("Вы не ввели новый пароль");
        }
        else if ((newPasswordShownA.getText().trim().isEmpty()) && (newPasswordHideA.getText().trim().isEmpty())) {
            newPasswordShownA.setPromptText("Вы не ввели пароль повторно");
            newPasswordHideA.setPromptText("Вы не ввели пароль повторно");
        }
        else {
            if (oldPasswordHide.isVisible()) { oldPassword = oldPasswordHide.getText().trim(); }
            else if (oldPasswordShown.isVisible()) { oldPassword = oldPasswordShown.getText().trim(); }

            if (newPasswordHide.isVisible()) { newPassword = newPasswordHide.getText().trim(); }
            else if (newPasswordShown.isVisible()) { newPassword = newPasswordShown.getText().trim(); }

            //noinspection ConstantConditions
            if (oldPassword.equals(newPassword) && (!(oldPassword.isEmpty()) || !(newPassword.isEmpty()))) { System.out.println("Старый и новый пароли совпадают"); }
            else { // Если не работает добавь if (!(oldPassword.equals(newPassword) && (!(oldPassword.isEmpty()) || !(newPassword.isEmpty()))))
                String passwordJSON = user.getPassword();
                if (oldPassword.equals(passwordJSON)) {
                    System.out.println("Доступ разрешён, меняем пароль");
                    user.setPassword(newPassword);

                    Alert info = new Alert(Alert.AlertType.INFORMATION, null, ButtonType.OK);
                    info.setTitle("Успешная смена пароля");
                    info.setHeaderText(null);
                    info.setContentText("Вы успешно сменили пароль");

                    oldPasswordHide.clear();
                    oldPasswordShown.clear();
                    newPasswordHide.clear();
                    newPasswordShown.clear();
                    newPasswordHideA.clear();
                    newPasswordShownA.clear();

                    info.show();
                }
                else { System.out.println("Неверный пароль"); }
            }
        }
    }

    // Метод для кнопок "Скрыть/показать"
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
    public void showPressedB() { hideButton(newPasswordHide, newPasswordShown);}
    public void showPressedC() { hideButton(newPasswordHideA, newPasswordShownA);}

    @FXML private TextField emailField;

    // Нажатие кнопки "Привязать email
    // TODO можно добавить поле с email'oм, хотя это и не важно
    public void onAddEmailButtonPressed() throws JSONException {
        String s = emailField.getText();
        if (s.isEmpty()) { emailField.setPromptText("Вы не ввели e-mail"); }
        else if (!checkEmail(s)) {
            emailField.clear();
            emailField.setPromptText("Проверьте правильность ввода email");
        }
        else if (checkEmail(s)) {
            Alert info = new Alert(Alert.AlertType.INFORMATION, null, ButtonType.CANCEL);

            JSONObject object = User.readJSON();
            JSONObject userObject = object.getJSONObject(user.getLogin());
            userObject.put("email", s);

            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(User.class.getResource("/users.json").getPath()));
                out.write(object.toString());
                out.close();

                info.setTitle("Успех");
                info.setContentText("Вы успешно привязали email");
                info.setHeaderText(null);
                info.show();
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }


        }
    }

    public boolean checkEmail(String s) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(s);

        return mat.matches();
    }


    // Работа с данными
    public void onTablesButtonPressed() {
        accountSettings.setVisible(false);
        tables.setVisible(true);
        reports.setVisible(false);
        accountsAdmin.setVisible(false);
        statsAdmin.setVisible(false);
    }
    @FXML private Pane tables;

    // Работа с отчётами
    public void onReportsButtonPressed() {
        accountSettings.setVisible(false);
        tables.setVisible(false);
        reports.setVisible(true);
        accountsAdmin.setVisible(false);
        statsAdmin.setVisible(false);
    }
    @FXML private Pane reports;

    // Работа с аккаунтами
    public void onAccAButtonPressed() {
        accountSettings.setVisible(false);
        tables.setVisible(false);
        reports.setVisible(false);
        accountsAdmin.setVisible(true);
        statsAdmin.setVisible(false);
    }
    @FXML private Pane accountsAdmin;

    // Статистика сервера
    public void onStatsAButtonPressed() {
        accountSettings.setVisible(false);
        tables.setVisible(false);
        reports.setVisible(false);
        accountsAdmin.setVisible(false);
        statsAdmin.setVisible(true);
    }
    @FXML private Pane statsAdmin;














}
