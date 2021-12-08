package com.example.pharmacy;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AuthController {
    @FXML
    private Label notification;
    @FXML
    TextField userName;
    @FXML
    PasswordField password;

    //TODO Сделать окно "Забыл пароль"
    //protected void onForgotAction()

    //TODO Сделать чтобы показывался и скрывался пароль
    //protected void onShowPassword()

    @FXML
    protected void onSignUpButtonPress() {

        //TODO Сделать простенькую авторизацию c user:user


        if (userName.getText().trim().isEmpty()) { notification.setText("БЛЯДЬ"); }
    }
}