package com.pharmacy;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AuthController {
    @FXML private Label notification;
    @FXML private TextField userName;

    @FXML private PasswordField passwordHide;
    @FXML private TextField passwordShown;

    @FXML private Button showButton;

    // Если true - активно passwordShown
    // Если false - активно passwordHide
    private boolean showPassword = false;

    //TODO Сделать окно "Забыл пароль"
    //protected void onForgotAction()

    @FXML
    protected void onSignUpButtonPress() {
        //TODO Сделать авторизацию
        // хранить будем в формате логин:пароль:ид_в_БД

        String login = userName.getText().trim();
        String password = "";

        // Проверка на ввод логина
        if (login.isEmpty()) { userName.setPromptText("Вы не ввели логин"); }
        else {userName.setPromptText("Логин");}

        // Проверка на ввод пароля
        if (showPassword && passwordShown.getText().trim().isEmpty()) { passwordShown.setPromptText("Вы не ввели пароль"); }
        else {
            passwordShown.setPromptText("Пароль");
            password = passwordShown.getText();
        }
        if (!showPassword && passwordHide.getText().trim().isEmpty()) { passwordHide.setPromptText("Вы не ввели пароль"); }
        else {
            passwordHide.setPromptText("Пароль");
            password = passwordHide.getText();
        }
        notification.setText(password);

        if (!login.isEmpty() && !password.isEmpty()) {
            if (password.equals("user")) { notification.setText("Успех"); } // TODO если хеш пароля совпадает с хешем в файле у пользователя и если есть такой клиент


        }
        // Проверка логина и пароля
        // TODO тут нужен какой нибудь BufferedReader чтобы читать из файла



    }

    @FXML
    protected void onShowButtonPress() {
        // При нажатии на кнопку с глазиком получает пароль
        // из активного поля и вставляет его в неактивное
        // Так же меняет видимость полей на противоположную

        String password;

        if (!showPassword) {
            password = passwordHide.getText().trim();
            passwordShown.setText(password);
        }
        else if (showPassword) {
            password = passwordShown.getText().trim();
            passwordHide.setText(password);
        }
        passwordHide.setVisible(showPassword);
        passwordShown.setVisible(!showPassword);

        showPassword = !showPassword;
    }

    public void checkUser(String login, String password) {

    }
}