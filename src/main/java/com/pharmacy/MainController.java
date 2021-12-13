package com.pharmacy;

import com.pharmacy.classes.User;
import com.pharmacy.classes.MySQLDriver;

import java.sql.*;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.JSONException;
import org.w3c.dom.Text;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainController {


    /*
    Я немного изменил структуру этого дока.
     */

    private User user;
    @FXML private Label authName;

    // Кнопки недоступные пользователю
    @FXML private Button accountsButton;
    @FXML private Button statsButton;

    // Метод задаёт юзера для сеанса
    public void setUser(User user) throws JSONException {
        this.user = user;
        userName.setText(user.getLogin());
        int access = user.getAccess();
        authName.setText("Авторизован:\n" + user.getLogin());

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

    // Для соответвствия имён таблиц к их именам в БД
    static private Map<String, String> tableChoiceQueries = new HashMap<String, String>();
    // Работа с данными
    public void onTablesButtonPressed() {
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
    String mysql_user = "root"; // TODO можно запросить в начале программы и сохранить в Jsone
    String mysql_pass = "password";

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
    public void onReportsButtonPressed() {
        accountSettings.setVisible(false);
        tables.setVisible(false);
        reports.setVisible(true);
        accountsAdmin.setVisible(false);
        statsAdmin.setVisible(false);
    }
    @FXML private Pane reports;


    // Работа с аккаунтами
    public void onAccAButtonPressed() throws JSONException {
        accountSettings.setVisible(false);
        tables.setVisible(false);
        reports.setVisible(false);
        accountsAdmin.setVisible(true);
        statsAdmin.setVisible(false);

        updateChoiceBox(userLogins);

        userLogins.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                try {
                    int intAccess = User.readJSON().getJSONObject(t1).getInt("access");
                    if (intAccess == 0) {
                        chosenUserAccess.setText("Пользователь");
                    }
                    else {
                        chosenUserAccess.setText("Администратор");
                    }

                    Connection conn = DriverManager.getConnection(mysql_url, mysql_user, mysql_pass) ;
                    Statement stmt = conn.createStatement();
                    String query = "select surname, name, middle_name from employees where employee_id=" + User.readJSON().getJSONObject(t1).getInt("id") + ";";
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        String surname = rs.getString("surname");
                        String name = rs.getString("name");
                        String middle_name = rs.getString("middle_name");
                        userFIO.setText(surname + " " + name + " " + middle_name);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateChoiceBox(ChoiceBox box) throws JSONException {
        // Заполнение ChoiceBox с пользователями
        if (!box.getItems().equals(null)) { box.getItems().clear(); } // TODO вот на эту строку ругается
        JSONObject object = User.readJSON();
        Iterator<String> logins = object.keys();

        while (logins.hasNext()) {
            String login = logins.next();
            if (object.get(login) instanceof JSONObject) {
                userLogins.getItems().add(login);
            }
        }
    }

    @FXML private Pane accountsAdmin;

    // Создание аккаунта
        @FXML private TextField newUserLogin;
        @FXML private TextField newUserFIO;
        @FXML private TextField newUserPhone;
        @FXML private ToggleGroup newUserAccess;

        // Поля Login, Access заносятся в json
        // Поля FIO, Position заносятся в БД

    // Нажатие кноки "Создать аккаунт"
    public void onCreateNewUserPressed() throws JSONException, SQLException {
        //TODO Нужна проверить существует ли еще пользователь с таким именем
        //  Нужно проверить заполнены ли все поля , если не заполнены .setPromptTex("не ввели логин")
        //  Если что-то еще обнаружиться то и они нужны
        String login = newUserLogin.getText();
        String password = User.generatePassword();
        String[] FIO = newUserFIO.getText().split(" ");
        String phone = newUserPhone.getText();
        int intAccess;
        int position;

        RadioButton selectedRadioButton = (RadioButton) newUserAccess.getSelectedToggle();
        String access = selectedRadioButton.getText();
        if (access.equals("пользователь")) {
            intAccess = 0;
            position = 10;
        }
        else {
            intAccess = 1;
            position = 11;
        }

        String surname = FIO[0];
        String name = FIO[1];
        String middle = FIO[2];

        String query = "insert into employees (surname, name, middle_name, phone_number, position_id)"
                + " values ('" + surname + "', '" + name + "', '" + middle + "', '" + phone + "', '" + position + "');";
        System.out.println(query);

        Connection conn = DriverManager.getConnection(mysql_url, mysql_user, mysql_pass) ;
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);

        ResultSet rs = preparedStatement.getGeneratedKeys();
        rs.next();
        int id = rs.getInt(1);

        conn.close();

        JSONObject object = User.readJSON();
        JSONObject userObject = new JSONObject();
        userObject.put("password", password);
        userObject.put("access", intAccess);
        userObject.put("id", id);
        object.put(login, userObject);

        User.updateJSON(object);

        updateChoiceBox(userLogins);
        Alert newUserAlert = new Alert(Alert.AlertType.INFORMATION, null, ButtonType.OK);
        newUserAlert.setTitle("Новый пользователь");
        newUserAlert.setHeaderText("Создан новый пользователь");
        newUserAlert.setContentText("Логин: " + login + "\nПароль: " + password + "\nДоступ: " + access);
        newUserAlert.show();
    }

        @FXML private ChoiceBox<String> userLogins;
        @FXML private TextField userFIO;
        @FXML private TextField chosenUserAccess;

    // Нажатие кнопки "Удалить аккаунт"
    //TODO Здесь нужна проверка на то что ты сам себя не удалишь
    public void onDeleteUserButtonPressed() throws JSONException, SQLException {
        String login = userLogins.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления аккаунта");
        alert.setHeaderText("Вы уверены что хотите удалить аккаунт " + login + "?");
        alert.setContentText("Нажмите ОК для подтверждения");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            JSONObject object = User.readJSON();
            JSONObject user = object.getJSONObject(login);
            int userId = user.getInt("id");

            // Удаляем из бд
            String query = "DELETE from employees where employee_id=" + userId + ";";
            Connection conn = DriverManager.getConnection(mysql_url, mysql_user, mysql_pass) ;
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.execute(query);
            conn.close();

            // Удаляем из JSON
            object.remove(login);
            User.updateJSON(object);

            // TODO добавить окошечко о том что пользователь удалён
        }

    }

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