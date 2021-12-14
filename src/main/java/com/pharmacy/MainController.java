package com.pharmacy;

import com.pharmacy.classes.MySQLDriver;
import com.pharmacy.classes.User;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
        String newPasswordA = "";

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

            if (newPasswordHide.isVisible()) { newPasswordA = newPasswordHideA.getText().trim(); }
            else if (newPasswordShownA.isVisible()) { newPasswordA = newPasswordShownA.getText().trim(); }

            if (newPasswordHideA.isVisible()) { newPassword = newPasswordHide.getText().trim(); }
            else if (newPasswordShown.isVisible()) { newPassword = newPasswordShown.getText().trim(); }

            //noinspection ConstantConditions
            if (!newPassword.equals(newPasswordA)) {
                Alert info = new Alert(Alert.AlertType.ERROR, null, ButtonType.OK);
                info.setTitle("Ошибка при смене пароля");
                info.setHeaderText(null);
                info.setContentText("Неверно повторён новый пароль");
                info.showAndWait();
            }
            else if (oldPassword.equals(newPassword) && (!(oldPassword.isEmpty()) || !(newPassword.isEmpty()))) {
                System.out.println("onChangePasswordPressed(): Старый и новый пароли совпадают");
                Alert info = new Alert(Alert.AlertType.ERROR, null, ButtonType.OK);
                info.setTitle("Ошибка при смене пароля");
                info.setHeaderText(null);
                info.setContentText("Старый и новый пароли совпадают");
                info.showAndWait();
            }
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
        @FXML private Button closeButton;

    // Для связи с сервером БД
    // TODO Придумать для url, root, password ввод
    String mysql_url = "jdbc:mysql://localhost:3306/pharmacy";
    String mysql_user = "root"; // TODO можно запросить в начале программы и сохранить в Jsone пока что через настройки
    String mysql_pass = "password";

    public void onMySQLAccessConfigButtonPressed(ActionEvent event) {
        VBox layout = new VBox();
        Scene scene = new Scene(layout, 300, 200);
        Stage stage = new Stage();
        stage.setTitle("Параметры входа MySQL");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(tables.getScene().getWindow());
        stage.setScene(scene);
        Label lb = new Label("Введите имя пользователя и пароль MySQL:");
        TextField tfUser = new TextField(mysql_user);
        TextField tfPass = new TextField();
        tfUser.setPromptText("Имя пользователя");
        tfPass.setPromptText("Пароль");
        Button save = new Button("Сохранить");
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (tfUser.getText().isEmpty() || tfPass.getText().isEmpty()) {
                    Alert inputError = new Alert(Alert.AlertType.ERROR, null, ButtonType.OK);
                    inputError.setTitle(stage.getTitle() + " - Ошибка");
                    inputError.setHeaderText("Ошибка при заполнении");
                    inputError.setContentText("Вы не заполнили все поля");
                    inputError.showAndWait();
                }
                else {
                    try {
                        Connection conn = DriverManager.getConnection(mysql_url, tfUser.getText(), tfPass.getText());
                        mysql_user = tfUser.getText();
                        mysql_pass = tfPass.getText();
                        stage.close();
                    }
                    catch (Exception e) {
                        Alert inputError = new Alert(Alert.AlertType.ERROR, null, ButtonType.OK);
                        inputError.setTitle(stage.getTitle() + " - Ошибка");
                        inputError.setHeaderText("Ошибка входа");
                        inputError.setContentText("Неверные данные входа: " + e.getMessage());
                        inputError.showAndWait();
                    }
                }
            }
        });
        layout.getChildren().add(lb);
        layout.getChildren().add(tfUser);
        layout.getChildren().add(tfPass);
        layout.getChildren().add(save);
        stage.show();
    }

    // Для вывода таблиц в текущем окне
    public void onDisplayTableButtonPressed(ActionEvent event) {
        /* TODO Вывод окна с подтверждением на смену таблицы без сохранения
            ИЛИ (сложнее) сделать, чтобы появлялся новый элемент TableView */
        Object tableAlias = tableChoice.getSelectionModel().getSelectedItem();
        String tableName = tableChoiceQueries.get(tableAlias);
        MySQLDriver driver = new MySQLDriver(mysql_url, mysql_user, mysql_pass, tableName);
        // Удаляем существующие данные в табл
        tvTest.getItems().clear();
        tvTest.getColumns().clear();
        driver.buildData(tvTest, "default_select");
        saveAllButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (driver.executePreparedQueries()) {
                    Alert saveInfo = new Alert(Alert.AlertType.INFORMATION, null, ButtonType.OK);
                    saveInfo.setTitle("Сохранение таблицы");
                    saveInfo.setHeaderText("Успешно сохранена таблица " + tableAlias.toString());
                    saveInfo.setContentText("Нажмите ОК, чтобы продолжить");
                    saveInfo.showAndWait();
                }
            }
        });
        closeButton.setOnAction(e-> {
            if (driver.hasUnsavedChanges()) {
                Alert saveInfo = new Alert(Alert.AlertType.INFORMATION, null, ButtonType.YES, ButtonType.NO);
                saveInfo.setTitle("Сохранение таблицы");
                saveInfo.setHeaderText("Вы не сохранили изменения в таблице " + tableAlias);
                saveInfo.setContentText("Сохранить изменения?");
                Optional<ButtonType> result = saveInfo.showAndWait();
                if (result.get() == ButtonType.YES) {
                    driver.executePreparedQueries();
                    tvTest.getItems().clear();
                    tvTest.getColumns().clear();
                }
                if (result.get() == ButtonType.NO) {
                    tvTest.getItems().clear();
                    tvTest.getColumns().clear();
                }
            }
            else {
                tvTest.getItems().clear();
                tvTest.getColumns().clear();
            }
        });

    }

    // Для вывода таблицы в новом окне
    public void onDisplayWindowedTableButtonPressed(ActionEvent event) {
        Object tableAlias = tableChoice.getSelectionModel().getSelectedItem();
        String tableName = tableChoiceQueries.get(tableAlias);
        MySQLDriver driver = new MySQLDriver(mysql_url, mysql_user, mysql_pass, tableName);
        // Подготовка окна
        VBox layout = new VBox();
        Scene scene = new Scene(layout, 600, 400);
        Stage stage = new Stage();
        stage.setTitle(tableChoice.getSelectionModel().getSelectedItem().toString());
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
                List<TextField> textfields = new ArrayList<>(); // список полей вставки
                List<String> columns = driver.getColumnsNames(); // список столбцов таблицы
                // Создаём окно со вставкой
                VBox newRow = new VBox();
                Stage inputStage = new Stage();
                Scene inputScene = new Scene(newRow, 400, 300);
                inputStage.setTitle(stage.getTitle() + " - Новая строка...");
                inputStage.initModality(Modality.WINDOW_MODAL);
                inputStage.initOwner(layout.getScene().getWindow());
                inputStage.setScene(inputScene);
                // Кнопка ОК - отправка запроса
                Button ok = new Button("ОК");
                ok.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        HashMap<String, Object> values = new HashMap(); // для хранения пары имя столбца, новое значение
                        List<String> inputs = new ArrayList<String>();
                        for (Iterator<TextField> i_tf = textfields.iterator(); i_tf.hasNext();) {
                            TextField e_tf = i_tf.next();
                            if (!e_tf.getText().isEmpty()) {
                                inputs.add(e_tf.getText());
                            }
                            else {
                                inputs.add(" ");
                            }
                        }
                        // Если хотя бы одно поле заполнено
                        ObservableList new_row = FXCollections.observableArrayList(); // новая строка для структуры данных
                        Iterator<String> i_col = columns.iterator();
                        for (Iterator<String> i_input = inputs.iterator(); i_input.hasNext() && i_col.hasNext();) {
                            String colName = i_col.next();
                            String e_text = i_input.next();
                            if (!" ".equals(e_text)) {
                                values.put(colName, e_text);
                            }
                            new_row.add(e_text);
                        }
                        if (!values.isEmpty()) {
                            // Сохранение как запроса к бд
                            driver.inputRow(tv, values, new_row);
                            // Выход из формы заполнения вставки
                            inputStage.close();
                        }
                        else {
                            Alert inputError = new Alert(Alert.AlertType.ERROR, null, ButtonType.OK);
                            inputError.setTitle(inputStage.getTitle() + " - Ошибка");
                            inputError.setHeaderText("Ошибка при вводе строки");
                            inputError.setContentText("Вы не заполнили ни одного поля");
                            inputError.showAndWait();
                        }
                    }
                });
                // Итерация по всем полям - заполнение списка полей
                for (Iterator<TableColumn> i = tv.getColumns().iterator(); i.hasNext();) {
                    TableColumn e = i.next();
                    TextField tf = new TextField();
                    tf.setPromptText(e.getText());
                    textfields.add(tf);
                    newRow.getChildren().add(tf);
                }
                newRow.getChildren().add(ok);
                inputStage.show();
            }
        });
        // При закрытии таблицы
        stage.setOnCloseRequest(e -> {
            if (driver.hasUnsavedChanges()) {
                Alert saveInfo = new Alert(Alert.AlertType.INFORMATION, null, ButtonType.YES, ButtonType.NO);
                saveInfo.setTitle("Сохранение таблицы");
                saveInfo.setHeaderText("Вы не сохранили изменения в таблице " + tableAlias.toString());
                saveInfo.setContentText("Сохранить изменения?");
                Optional<ButtonType> result = saveInfo.showAndWait();
                if (result.get() == ButtonType.YES) {
                    driver.executePreparedQueries();
                }
            }
        });
        // Добавление элементов
        layout.getChildren().add(menuBar);
        layout.getChildren().add(tv);
        // Заполнение таблицы
        driver.buildData(tv, "default_select");
        stage.show();
    }

    // Работа с отчётами
    public void onReportsButtonPressed() {
        accountSettings.setVisible(false);
        tables.setVisible(false);
        reports.setVisible(true);
        accountsAdmin.setVisible(false);
        statsAdmin.setVisible(false);

        // TODO сделать заполнение cbReports при инициализации формы MainController
        if (cbReports.getItems().isEmpty()) {
            cbReports.getItems().add("Форма договора с производителем веществ");
            cbReports.getItems().add("Производство серий препаратов цехами");
            cbReports.getItems().add("Форма по разработке фармацевтической продукции");
            cbReports.getItems().add("Поставщики веществ");
            cbReports.getItems().add("Вещества и их поставщики");
            cbReports.getItems().add("Список всех веществ компании");

            cbReports.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                    tvReports.getItems().clear();
                    tvReports.getColumns().clear();
                    MySQLDriver driver = new MySQLDriver(mysql_url, mysql_user, mysql_pass);
                    String choice = cbReports.getItems().get((Integer) number2).toString();
                    InputStream is;
                    String query;
                    ArrayList<String> values;
                    switch (choice) {
                        case "Форма договора с производителем веществ":
                            is = MainController.class.getResourceAsStream("/queries/query1.sql");
                            query = new BufferedReader(
                                    new InputStreamReader(is, StandardCharsets.UTF_8))
                                    .lines()
                                    .collect(Collectors.joining("\n"));
                            values = driver.getColumn("medicine_warehouse", "serial");
                            for (int i = 0; i < values.size(); i++) {
                                if (i!=values.size()-1) {
                                    query = query.replace("GENERATE_PIVOT\n", "(SELECT COUNT(*) FROM Src WHERE serial = " + values.get(i) + " AND ИД=Src.employee_id)\"серия "  +values.get(i) + "\",\n GENERATE_PIVOT\n");
                                }
                                else {
                                    query = query.replace("GENERATE_PIVOT\n", "(SELECT COUNT(*) FROM Src WHERE serial = " + values.get(i) + " AND ИД=Src.employee_id)\"серия " + values.get(i) + "\"\n");
                                }
                            }
                            driver.buildData(tvReports, query);
                            break;
                        case "Производство серий препаратов цехами":
                            is = MainController.class.getResourceAsStream("/queries/query2.sql");
                            query = new BufferedReader(
                                    new InputStreamReader(is, StandardCharsets.UTF_8))
                                    .lines()
                                    .collect(Collectors.joining("\n"));
                            values = driver.getColumn("medicine_warehouse", "serial");
                            for (int i = 0; i < values.size(); i++) {
                                if (i!=values.size()-1) {
                                    query = query.replace("GENERATE_PIVOT\n", "(SELECT COUNT(*) FROM Src WHERE serial = " + values.get(i) + " AND ЦЕХ=Src.workshop_number)\"серия "  +values.get(i) + "\",\n GENERATE_PIVOT\n");
                                }
                                else {
                                    query = query.replace("GENERATE_PIVOT\n", "(SELECT COUNT(*) FROM Src WHERE serial = " + values.get(i) + " AND ЦЕХ=Src.workshop_number)\"серия " + values.get(i) + "\"\n");
                                }
                            }
                            driver.buildData(tvReports, query);
                            break;
                        case "Форма по разработке фармацевтической продукции":
                            is = MainController.class.getResourceAsStream("/queries/query3.sql");
                            query = new BufferedReader(
                                    new InputStreamReader(is, StandardCharsets.UTF_8))
                                    .lines()
                                    .collect(Collectors.joining("\n"));
                            driver.buildData(tvReports, query);
                            break;
                        case "Поставщики веществ":
                            is = MainController.class.getResourceAsStream("/queries/query4.sql");
                            query = new BufferedReader(
                                    new InputStreamReader(is, StandardCharsets.UTF_8))
                                    .lines()
                                    .collect(Collectors.joining("\n"));
                            driver.buildData(tvReports, query);
                            break;
                        case "Вещества и их поставщики":
                            is = MainController.class.getResourceAsStream("/queries/query5.sql");
                            query = new BufferedReader(
                                    new InputStreamReader(is, StandardCharsets.UTF_8))
                                    .lines()
                                    .collect(Collectors.joining("\n"));
                            driver.buildData(tvReports, query);
                            break;
                        case "Список всех веществ компании":
                            is = MainController.class.getResourceAsStream("/queries/query6.sql");
                            query = new BufferedReader(
                                    new InputStreamReader(is, StandardCharsets.UTF_8))
                                    .lines()
                                    .collect(Collectors.joining("\n"));
                            driver.buildData(tvReports, query);
                            break;
                    }
                }
            });
        }
    }

    @FXML private Pane reports;
        @FXML private ChoiceBox cbReports;
        @FXML private TableView tvReports;

    public void onSaveButtonPressed () throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet spreadsheet = workbook.createSheet("sample");

        Row row = spreadsheet.createRow(0);


        for (int j = 0; j < tvReports.getColumns().size(); j++) {
            TableColumn currentName = (TableColumn) tvReports.getColumns().get(j);
            row.createCell(j).setCellValue(currentName.getText());
        }

        for (int i = 0; i < tvReports.getItems().size(); i++) {
            row = spreadsheet.createRow(i + 1);
            ObservableList<String> currentRow = (ObservableList<String>) tvReports.getItems().get(i);
            for (int j = 0; j < currentRow.size(); j++) {
                row.createCell(j).setCellValue(currentRow.get(j));
            }
        }

        FileOutputStream fileOut = null;
        try {
            FileChooser chooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XLS files (*.xls)", "*.xls");
            chooser.getExtensionFilters().add(extFilter);
            File file = chooser.showSaveDialog(reports.getScene().getWindow());

            if (file != null) {
                fileOut = new FileOutputStream(file);
                workbook.write(fileOut);
                fileOut.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }






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
        if (!box.getItems().equals(null)) { box.getItems().clear(); } // Ругается на строку, но что уж поделать
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

    // Нажатие кноки "Создать аккаунт"
    public void onCreateNewUserPressed() throws JSONException, SQLException {
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
        JSONObject object = User.readJSON();

        if (newUserLogin.getText().trim().isEmpty()) {
            newUserLogin.setPromptText("Вы не ввели логин");
        }
        else if (newUserFIO.getText().trim().isEmpty()) {
            newUserFIO.setPromptText("Вы не ввели ФИО");
        }
        else if (newUserPhone.getText().trim().isEmpty()) {
            newUserPhone.setPromptText("Не заполнено");
        }
        else if (!newUserLogin.getText().trim().isEmpty() && !newUserFIO.getText().trim().isEmpty() && !newUserPhone.getText().trim().isEmpty()) {
            if (object.has(login)) {
                newUserLogin.clear();
                newUserLogin.setPromptText("Пользователь с таким логином уже существует");
            }
            else {
                // Заполнение базы
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
                // Изменения в JSON'e

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
        }
    }

        @FXML private ChoiceBox<String> userLogins;
        @FXML private TextField userFIO;
        @FXML private TextField chosenUserAccess;

    // Нажатие кнопки "Удалить аккаунт"
    public void onDeleteUserButtonPressed() throws JSONException, SQLException {
        String login = userLogins.getSelectionModel().getSelectedItem();
        if (login.equals(user.getLogin())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Вы не можете удалить свой аккаунт");
            alert.setContentText(null);
            alert.show();
        }
        else {
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
                Connection conn = DriverManager.getConnection(mysql_url, mysql_user, mysql_pass);
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                preparedStatement.execute(query);
                conn.close();
                // Удаляем из JSON
                object.remove(login);
                User.updateJSON(object);
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Удаление пользователя");
                alert1.setHeaderText("Вы удалили пользователя: " + login);
                alert.setContentText(null);
                alert.show();
            }
        }
    }

    // Статистика сервера
    public void onStatsAButtonPressed() {
        accountSettings.setVisible(false);
        tables.setVisible(false);
        reports.setVisible(false);
        accountsAdmin.setVisible(false);
        statsAdmin.setVisible(true);

        MySQLDriver driver = new MySQLDriver(mysql_url, mysql_user, mysql_pass);
        ArrayList<String> variables = new ArrayList<>(Arrays.asList("Queries", "Uptime", "maxQueryTime", "avgQueryTime", "Max_used_connections", "Open_tables"));
        ArrayList<String> values = driver.getServerStatus(variables);

        StringBuilder uptimeOut = new StringBuilder();
        int uptimeIn = Integer.parseInt(values.get(1));
        uptimeOut.append((int)uptimeIn/3600);
        uptimeOut.append(" час. ");
        uptimeOut.append((int)(uptimeIn%3600/60));
        uptimeOut.append(" мин. ");
        uptimeOut.append((int)(uptimeIn%3600%60));
        uptimeOut.append(" сек.");

        queryCount.setText(values.get(0));
        uptime.setText(uptimeOut.toString());
        maxQueryTime.setText(values.get(2));
        avgQueryTime.setText(values.get(3));
        userCount.setText(values.get(4));
        openedTables.setText(values.get(5));
    }
    @FXML private Pane statsAdmin;
        @FXML private TextField userCount;
        @FXML private TextField queryCount;
        @FXML private TextField uptime;
        @FXML private TextField maxQueryTime;
        @FXML private TextField avgQueryTime;
        @FXML private TextField openedTables;
        @FXML private TextField lastBackupDate;
    // Кнопка выключения
    public void onShutdownButtonPressed(ActionEvent event) {
        // Создаём окно со вставкой
        VBox sdBox = new VBox();
        Stage sdStage = new Stage();
        Scene sdScene = new Scene(sdBox, 400, 300);
        sdStage.setTitle("Выключение сервера");
        sdStage.initModality(Modality.WINDOW_MODAL);
        sdStage.initOwner(sdBox.getScene().getWindow());
        sdStage.setScene(sdScene);
        // Текстовое поле подтверждения
        TextField tf = new TextField();
        tf.setPromptText("Напишите выключить, чтобы подтвердить");
        // Кнопка ОК - отправка запроса
        Button ok = new Button("ОК");
        ok.setOnAction(e -> {
            if ("выключить".equalsIgnoreCase(tf.getText())) {
                try {
                    //Runtime.getRuntime().exec(new String[] {"cmd.exe", "/c", "sc", "stop", "MySQL80"});
                    //sdStage.close();
                    Process process = Runtime.getRuntime().exec("runas /profile /user:Administrator \"cmd.exenet stop mysql80\"");
                    System.out.println("Остановили");
                    sdStage.close();
                }
                catch (Exception ex) {
                    Alert inputError = new Alert(Alert.AlertType.ERROR, null, ButtonType.OK);
                    inputError.setTitle(sdStage.getTitle() + " - Ошибка");
                    inputError.setHeaderText("Ошибка при выключении:");
                    inputError.setContentText(ex.getMessage());
                    inputError.showAndWait();
                    System.out.println(ex.getMessage());
                }
            }
            else {
                Alert inputError = new Alert(Alert.AlertType.ERROR, null, ButtonType.OK);
                inputError.setTitle(sdStage.getTitle() + " - Ошибка");
                inputError.setHeaderText("Ошибка при выключении");
                inputError.setContentText("Введите \"выключить\" в поле ввода, чтобы выключить сервер");
                inputError.showAndWait();
            }
        });
        // Составление формы
        sdBox.getChildren().add(tf);
        sdBox.getChildren().add(ok);
        sdStage.show();
    }
    // Кнопка перезагрузки
    public void onRebootButtonPressed(ActionEvent event) {

    }
    // Кнопка резервного копирования
    public void onBackupButtonPressed(ActionEvent event) {

    }
}