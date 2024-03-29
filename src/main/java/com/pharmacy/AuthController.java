package com.pharmacy;

import com.pharmacy.classes.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONException;

import java.io.IOException;
import java.util.Optional;

public class AuthController {
    @FXML
    private Label notification;   // Для отладки

    @FXML
    private TextField userName;           // Поле логин
    @FXML
    private PasswordField passwordHide;   // Поле пароля (точки вместо текста)
    @FXML
    private TextField passwordShown;      // Поле пароля (текст)

    @FXML
    private VBox authPane;

    // Если true - активно поле passwordShown
    // Если false - активно поле passwordHide
    private boolean showPassword = false;

    @FXML
    protected void onForgotPassword() {
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
            // notification.setText("\"Отправили\" уведомление");
            TextInputDialog newPassword = new TextInputDialog();
            newPassword.setTitle("Сброс пароля");
            newPassword.setHeaderText(null);
            newPassword.setContentText("Введите логин:");
            Optional<String> login = newPassword.showAndWait();
            login.ifPresentOrElse((value) -> {
                try {
                    String new_password = User.resetPassword(login.get());

                    Alert passwordResetAlert = new Alert(Alert.AlertType.INFORMATION);
                    if (new_password != null) {
                        TextField passwordAreaA = new TextField();

                        passwordAreaA.setEditable(false);
                        passwordAreaA.setText(String.format("Новый пароль: %s", new_password));

                        passwordResetAlert.setTitle("Сброс пароля: успешно");
                        passwordResetAlert.setHeaderText("Успешно сброшен пароль.");
                        passwordResetAlert.getDialogPane().setContent(passwordAreaA);
                        passwordResetAlert.showAndWait();
                    }
                }
                catch (JSONException e) {
                    newPassword.setContentText("Вы не ввели логин");
                }
            },
                    () -> {
                    notification.setText("Вы не ввели логин");
                    System.out.println("ПУСТО");
            });
        }
    }

    @FXML
    protected void onSignUpButtonPress() throws IOException {

        String login = userName.getText().trim();
        String password = "";

        // Проверка на ввод логина
        if (login.isEmpty()) {
            userName.setPromptText("Вы не ввели логин");
        }
        else {
            userName.setPromptText("Логин");
        }

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

        if (!login.isEmpty() && !password.isEmpty()) {
            try {
                User user = new User(login, password);

                if ((password.equals(user.getPassword()))) {
                    Stage stage = (Stage) authPane.getScene().getWindow();
                    stage.setResizable(true);
                    PharmacyApplication.showMain(user, stage);
                } else {
                    notification.setText("Неправильный логин/пароль");
                }
            } catch (JSONException e) {
                notification.setText("Нет такого пользователя");
            }
        }
    }

    @FXML
    protected void onShowButtonPress () {
        if (passwordHide.isVisible()) {
            passwordHide.setVisible(false);
            passwordShown.setText(passwordHide.getText());
            passwordShown.setVisible(true);
        }
        else if (passwordShown.isVisible()) {
            passwordShown.setVisible(false);
            passwordHide.setText(passwordShown.getText());
            passwordHide.setVisible(true);
        }
    }
}

