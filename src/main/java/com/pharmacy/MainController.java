package com.pharmacy;

import com.pharmacy.classes.User;
import com.pharmacy.classes.MySQLDriver;

import java.util.Map;
import java.util.HashMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
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

    // Для соответвствия имёе таблиц к запросам на их вывод
    static private Map<String, String> tableChoiceQueries = new HashMap<String, String>();
    // Работа с данными
    public void onTablesButtonPressed(ActionEvent event) {
        accountSettings.setVisible(false);
        tables.setVisible(true);
        reports.setVisible(false);
        accountsAdmin.setVisible(false);
        statsAdmin.setVisible(false);

        // TODO сделать заполнение tableChoice при инициализации формы MainController
        if (tableChoice.getItems().isEmpty()) {
            tableChoice.getItems().add("Активное вещество");
            tableChoiceQueries.put("Активное вещество", "SELECT * FROM active_substance");
            tableChoice.getItems().add("Состав акт. веществ препарата");
            tableChoiceQueries.put("Состав акт. веществ препарата", "SELECT * FROM active_substance_comp");
            tableChoice.getItems().add("Вспомогательное вещество");
            tableChoiceQueries.put("Вспомогательное вещество", "SELECT * FROM secondary_substance");
            tableChoice.getItems().add("Состав всп. веществ препарата");
            tableChoiceQueries.put("Состав всп. веществ препарата", "SELECT * FROM secondary_substance_comp");
            tableChoice.getItems().add("Исследование");
            tableChoiceQueries.put("Исследование", "SELECT * FROM research");
            tableChoice.getItems().add("Лекарственное средство");
            tableChoiceQueries.put("Лекарственное средство", "SELECT * FROM medicine");
            tableChoice.getItems().add("Поставщик веществ");
            tableChoiceQueries.put("Поставщик веществ", "SELECT * FROM supplier");
            tableChoice.getItems().add("Поставка веществ");
            tableChoiceQueries.put("Поставка веществ", "SELECT * FROM supplies");
            tableChoice.getItems().add("Производственный цех");
            tableChoiceQueries.put("Производственный цех", "SELECT * FROM workshops");
            tableChoice.getItems().add("Должности");
            tableChoiceQueries.put("Должности", "SELECT * FROM positions");
            tableChoice.getItems().add("Сотрудники");
            tableChoiceQueries.put("Сотрудники", "SELECT * FROM employees");
            tableChoice.getItems().add("Партия препарата");
            tableChoiceQueries.put("Партия препарата", "SELECT * FROM medicine_batch");
            tableChoice.getItems().add("Производство препарата");
            tableChoiceQueries.put("Производство препарата", "SELECT * FROM medicine_production");
            tableChoice.getItems().add("Операции");
            tableChoiceQueries.put("Операции", "SELECT * FROM operations");
            tableChoice.getItems().add("Операции по складу");
            tableChoiceQueries.put("Операции по складу", "SELECT * FROM warehouse_operations");
            tableChoice.getItems().add("Склад. партии препарата");
            tableChoiceQueries.put("Склад. партии препарата", "SELECT * FROM medicine_warehouse");
            tableChoice.getItems().add("Заказчик");
            tableChoiceQueries.put("Заказчик", "SELECT * FROM customers");
            tableChoice.getItems().add("Сбыт");
            tableChoiceQueries.put("Сбыт", "SELECT * FROM sales");
            tableChoice.getItems().add("Сбыт партии");
            tableChoiceQueries.put("Сбыт партии", "SELECT * FROM medicine_sale");
        }
    }
    @FXML private Pane tables;
        @FXML private TableView tvTest;
        @FXML private ChoiceBox tableChoice;

    // Для связи с сервером БД
    private MySQLDriver driver = new MySQLDriver("jdbc:mysql://localhost:3306/pharmacy", "root", "mikeqwer2246");
    // Для вывода таблиц в текущем окне
    public void onDisplayTableButtonPressed(ActionEvent event) {
        // Удаляем существующие данные в табл
        tvTest.getItems().clear();
        tvTest.getColumns().clear();
        driver.buildData(tvTest, tableChoiceQueries.get(tableChoice.getSelectionModel().getSelectedItem()));
    }

    // Для вывода таблиц в новом окне
    public void onDisplayWindowedTableButtonPressed(ActionEvent event) {
        // Подготовка окна
        StackPane layout = new StackPane();
        Scene scene = new Scene(layout, 600, 400);
        Stage stage = new Stage();
        stage.setTitle(tableChoice.getSelectionModel().getSelectedItem().toString());
        stage.setScene(scene);
        TableView tw = new TableView();
        // Добавление таблицы
        layout.getChildren().add(tw);
        // Заполнение таблицы
        driver.buildData(tw, tableChoiceQueries.get(tableChoice.getSelectionModel().getSelectedItem()));
        stage.show();
    }


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