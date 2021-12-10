package com.pharmacy;

import com.pharmacy.classes.User;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import org.json.JSONException;

import java.io.IOException;

public class PharmacyApplication extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(PharmacyApplication.class.getResource("/authorization.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Авторизация");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void showMain(User user, Stage stage) throws IOException, JSONException {
        FXMLLoader fxmlLoader = new FXMLLoader(PharmacyApplication.class.getResource("/main.fxml"));
        stage.hide();
        SplitPane root = fxmlLoader.load();
        stage.getScene().setRoot(root);
        stage.setTitle("ИС \"Фармацевтическая компания\"");
        stage.show();

        MainController mainController = fxmlLoader.getController();
        mainController.setUser(user);
    }

    public static void main(String[] args) {
        launch();
    }
}