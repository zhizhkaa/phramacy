package com.pharmacy;

import com.pharmacy.classes.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONException;

import java.io.IOException;
import java.util.Optional;

public class AuthController {
    @FXML private Label notification;   // Для отладки

    @FXML private TextField userName;           // Поле логин
    @FXML private PasswordField passwordHide;   // Поле пароля (точки вместо текста)
    @FXML private TextField passwordShown;      // Поле пароля (текст)

    @FXML private VBox authPane;

    // Если true - активно поле passwordShown
    // Если false - активно поле passwordHide
    private boolean showPassword = false;

    @FXML
    protected void onForgotPassword() throws JSONException {
        ButtonType resetPassword = new ButtonType("Сбросить пароль", ButtonBar.ButtonData.OK_DONE);
        Alert alert = new Alert(Alert.AlertType.INFORMATION, null, resetPassword);
        alert.setTitle("Забыл пароль");
        alert.setHeaderText(null);
        Label forgotLabel = new Label("Пожалуйста, обратитесь к вашему системному" +
                                        "\nадминистратору для восстановления доступа или смены пароля.\n" +
                                        "\nИли сбросьте пароль посредством\nпривязанной к аккаунту электронной почты:");
        forgotLabel.setWrapText(true);
        alert.getDialogPane().setContent(forgotLabel);

        Optional<ButtonType> result = alert.showAndWait();

        // Если закрыли окно
        if (result.isEmpty()) {
            notification.setText("Вышли из уведомления");
        }
        else if (result.get() == resetPassword) {
            notification.setText("\"Отправили\" уведомление");
            Alert newPassword = new Alert(Alert.AlertType.INFORMATION, null, ButtonType.OK);

            // TODO: вместо этого нужно окно для ввода логина, проверка логина, вывод нового пароля
            newPassword.setTitle("Новый пароль");
            newPassword.setHeaderText(null);
            newPassword.setContentText("Новый пароль:\n kfdfkdk");
            newPassword.showAndWait();
        }

    }

    @FXML
    protected void onSignUpButtonPress() throws IOException, JSONException {


        String login = userName.getText().trim();
        String password = "";
        // Проверка на ввод логина
        if (login.isEmpty()) { userName.setPromptText("Вы не ввели логин"); }
        else {userName.setPromptText("Логин");}

        // Проверка на ввод пароля
        if (showPassword && passwordShown.getText().trim().isEmpty()) {
            passwordShown.setPromptText("Вы не ввели пароль");
        }
        else {
            passwordShown.setPromptText("Пароль");
            password = passwordShown.getText();
        }
        if (!showPassword && passwordHide.getText().trim().isEmpty()) {
            passwordHide.setPromptText("Вы не ввели пароль");
        }
        else {
            passwordHide.setPromptText("Пароль");
            password = passwordHide.getText();
        }
        try {
            System.out.println(login + password);
            // TODO если хеш пароля совпадает с хешем в файле у пользователя и если есть такой клиент
            if (!login.isEmpty() && !password.isEmpty()) {
                User user = new User(login, password);
                if ((user.getLogin() == login) && (user.getPassword() == password)) {
                    Stage stage = (Stage) authPane.getScene().getWindow();
                    PharmacyApplication.showMain(user, stage);
                }
                else {
                    notification.setText("Неправильный логин или пароль");
                }
            }
        }

        catch (JSONException e) {
            notification.setText("Нет такого пользователя");
        }




        // Проверка логина и пароля
        // TODO тут нужен какой нибудь BufferedReader чтобы читать из json файла
    }


    @FXML
    protected void onShowButtonPress() {
        /*  При нажатии на кнопку с глазиком получает пароль
            из активного поля и вставляет его в неактивное
            Так же меняет видимость полей на противоположную */

        String password;

        if (!showPassword) {
            password = passwordHide.getText().trim();
            passwordShown.setText(password);
        }
        else {
            password = passwordShown.getText().trim();
            passwordHide.setText(password);
        }
        passwordHide.setVisible(showPassword);
        passwordShown.setVisible(!showPassword);

        showPassword = !showPassword;
    }
}