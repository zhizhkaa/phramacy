package com.pharmacy;

import com.pharmacy.classes.User;
import com.pharmacy.classes.MySQLDriver;

import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.JSONException;
import org.w3c.dom.Text;


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

    // Для соответвствия имён таблиц к их именам в БД
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
            tableChoiceQueries.put("Активное вещество", "active_substance");
            tableChoice.getItems().add("Состав акт. веществ препарата");
            tableChoiceQueries.put("Состав акт. веществ препарата", "active_substance_comp");
            tableChoice.getItems().add("Вспомогательное вещество");
            tableChoiceQueries.put("Вспомогательное вещество", "secondary_substance");
            tableChoice.getItems().add("Состав всп. веществ препарата");
            tableChoiceQueries.put("Состав всп. веществ препарата", "secondary_substance_comp");
            tableChoice.getItems().add("Исследование");
            tableChoiceQueries.put("Исследование", "research");
            tableChoice.getItems().add("Лекарственное средство");
            tableChoiceQueries.put("Лекарственное средство", "medicine");
            tableChoice.getItems().add("Поставщик веществ");
            tableChoiceQueries.put("Поставщик веществ", "supplier");
            tableChoice.getItems().add("Поставка веществ");
            tableChoiceQueries.put("Поставка веществ", "supplies");
            tableChoice.getItems().add("Производственный цех");
            tableChoiceQueries.put("Производственный цех", "workshops");
            tableChoice.getItems().add("Должности");
            tableChoiceQueries.put("Должности", "positions");
            tableChoice.getItems().add("Сотрудники");
            tableChoiceQueries.put("Сотрудники", "employees");
            tableChoice.getItems().add("Партия препарата");
            tableChoiceQueries.put("Партия препарата", "medicine_batch");
            tableChoice.getItems().add("Производство препарата");
            tableChoiceQueries.put("Производство препарата", "medicine_production");
            tableChoice.getItems().add("Операции");
            tableChoiceQueries.put("Операции", "operations");
            tableChoice.getItems().add("Операции по складу");
            tableChoiceQueries.put("Операции по складу", "warehouse_operations");
            tableChoice.getItems().add("Склад. партии препарата");
            tableChoiceQueries.put("Склад. партии препарата", "medicine_warehouse");
            tableChoice.getItems().add("Заказчик");
            tableChoiceQueries.put("Заказчик", "customers");
            tableChoice.getItems().add("Сбыт");
            tableChoiceQueries.put("Сбыт", "sales");
            tableChoice.getItems().add("Сбыт партии");
            tableChoiceQueries.put("Сбыт партии", "medicine_sale");
        }
    }
    @FXML private Pane tables;
        @FXML private TableView<String> tvTest;
        @FXML private ChoiceBox tableChoice;
        @FXML private Button saveAllButton;

    // Для связи с сервером БД
    // TODO Придумать для url, root, password ввод
    String mysql_url = "jdbc:mysql://localhost:3306/pharmacy";
    String mysql_user = "root";
    String mysql_pass = "mikeqwer2246";

    // Для вывода таблиц в текущем окне
    public void onDisplayTableButtonPressed(ActionEvent event) {
        /* TODO Вывод окна с подтверждением на смену таблицы без сохранения
            ИЛИ (сложнее) сделать, чтобы появлялся новый элемент TableView */
        String tableName = tableChoiceQueries.get(tableChoice.getSelectionModel().getSelectedItem());
        MySQLDriver driver = new MySQLDriver(mysql_url, mysql_user, mysql_pass, tableName);
        // Удаляем существующие данные в табл
        tvTest.getItems().clear();
        tvTest.getColumns().clear();
        driver.buildData(tvTest);
        saveAllButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                driver.executePreparedQueries();
            }
        });
    }

    // Для вывода таблицы в новом окне
    public void onDisplayWindowedTableButtonPressed(ActionEvent event) {
        String tableName = tableChoiceQueries.get(tableChoice.getSelectionModel().getSelectedItem());
        MySQLDriver driver = new MySQLDriver(mysql_url, mysql_user, mysql_pass, tableName);
        // Подготовка окна
        VBox layout = new VBox();
        Scene scene = new Scene(layout, 600, 400);
        Stage stage = new Stage();
        stage.setTitle(tableChoice.getSelectionModel().getSelectedItem().toString() + " - " + tableName);
        stage.setScene(scene);
        // Подготовка TableView
        TableView tv = new TableView();
        tv.setEditable(true);
        // MenuBar
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Таблица");
        menuBar.getMenus().add(menu);
        MenuItem save = new MenuItem("Сохранить");
        MenuItem delete = new MenuItem("Удалить строку");
        MenuItem insert = new MenuItem("Новая строка...");
        menu.getItems().add(save);
        menu.getItems().add(delete);
        menu.getItems().add(insert);
        // Кнопка сохранения - вызывает метод, который обрабатывает выполнение сохранённых команд драйвера
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                driver.executePreparedQueries();
            }
        });
        // Кнопка удаления - фиксирует изменение в структуре данных драйвера data и добавляет в очередь команд
        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Object selectedItem = tv.getSelectionModel().getSelectedItem();
                driver.deleteRow(tv, tv.getItems().indexOf(selectedItem));
                tv.getItems().remove(selectedItem);
            }
        });
        // Кнопка вставки - аналогично кнопке удаления
        // Для вставки вызывает новую форму ввода с кол-вом полей таблицы драйвера, после нажатия ОК фиксирует изменения
        //и добавляет команду вставки в очередь команд
        insert.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Для последующей вставки
                List<TextField> inputs = new ArrayList<>(); // список полей вставки
                List<TableColumn> columns = new ArrayList<>(); // список столбцов таблицы
                // Создаём окно со вставкой
                VBox newRow = new VBox();
                Stage inputStage = new Stage();
                Scene inputScene = new Scene(newRow, 400, 300);
                inputStage.initModality(Modality.WINDOW_MODAL);
                inputStage.initOwner(layout.getScene().getWindow());
                inputStage.setScene(inputScene);
                // Кнопка ОК - отправка запроса
                Button ok = new Button("ОК");
                ok.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        HashMap<String, Object> values = new HashMap(); // для хранения пары имя столбца, новое значение
                        // Если хотя бы одно поле заполнено
                        if (!inputs.isEmpty()) {
                            ObservableList new_row = FXCollections.observableArrayList(); // новая строка для структуры данных
                            Iterator<TableColumn> i_col = columns.iterator();
                            for (Iterator<TextField> i_text = inputs.iterator(); i_text.hasNext() && i_col.hasNext();) {
                                TableColumn e_col = i_col.next();
                                TextField e_text = i_text.next();
                                values.put(e_col.getText(), e_text.getText());
                                new_row.add(e_text.getText());
                            }
                            // Сохранение как запроса к бд
                            driver.inputRow(tv, values, new_row);
                            // Выход из формы заполнения вставки
                            inputStage.close();
                        }

                    }
                });
                // Итерация по всем полям - заполнение списков
                for (Iterator<TableColumn> i = tv.getColumns().iterator(); i.hasNext();) {
                    TableColumn e = i.next();
                    TextField tf = new TextField(e.getText());
                    inputs.add(tf);
                    columns.add(e);
                    newRow.getChildren().add(tf);
                }
                newRow.getChildren().add(ok);
                inputStage.show();
            }
        });
        // Добавление элементов
        layout.getChildren().add(menuBar);
        layout.getChildren().add(tv);
        // Заполнение таблицы
        driver.buildData(tv);
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