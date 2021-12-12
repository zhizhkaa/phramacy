package com.pharmacy.classes;

import java.sql.*;
import java.util.*;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.Callback;

// Используется для подключения к БД для работы с кокретной таблицей
public class MySQLDriver {
    private List<String> columns = new ArrayList<String>();
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();  // Структура для хранения таблицы

    private String connectionUrl;
    private String user;
    private String password;
    private String tableName;
    private Queue<PreparedQuery> preparedQueries = new LinkedList<PreparedQuery>();

    //  Типы запросов
    enum queryTypes {Update, Delete, Insert};
    //  Класс для запоминания изменений - для их последующего применения при сохранении
    // Тип запроса присваивается в зависимости от параметров при вызове конструктора
    private class PreparedQuery {
        private queryTypes type;
        private int row;
        private String field;
        private Object newValue;
        private HashMap<String, Object> values;

        // Update query
        public PreparedQuery(int row, String field, Object newValue) {
            this.type = queryTypes.Update;
            this.row = row;
            this.field = field;
            this.newValue = newValue;
        }
        // Delete query
        public PreparedQuery(int row) {
            this.type = queryTypes.Delete;
            this.row = row;
        }
        // Insert query
        public PreparedQuery(HashMap<String, Object> values) {
            this.type = queryTypes.Insert;
            this.values = values;
        }
    }

    // Конструктор, инициализация данных для подключения и указание конкретной таблицы, с которой работаем
    // connectionUrl example = jdbc:mysql://localhost:3306/pharmacy
    public MySQLDriver(String connectionUrl, String user, String password, String tableName) {
        this.connectionUrl = connectionUrl;
        this.user = user;
        this.password = password;
        this.tableName = tableName;
    }

    public List<String> getColumnsNames() {
        return columns;
    }

    // Для выполнения сохранённых/подготовленных запросов
    public void executePreparedQueries() {
        // Подтягиваем таблицу с MySQL
        final String selectQuery = "SELECT * FROM " + this.tableName;
        PreparedQuery nextQuery;
        try (Connection conn = DriverManager.getConnection(this.connectionUrl, this.user, this.password);
             PreparedStatement ps = conn.prepareStatement(selectQuery,
                     ResultSet.TYPE_SCROLL_SENSITIVE,
                     ResultSet.CONCUR_UPDATABLE)) {
            ResultSet rs = ps.executeQuery(); // можно думать, что это курсор - позволяет изменять данные бд на основе запроса ps
            // Пока есть запросы в очереди - обрабатываем
            while (!preparedQueries.isEmpty()) {
                nextQuery = preparedQueries.peek();
                switch (nextQuery.type) {
                    case Update:
                        rs.absolute(nextQuery.row);
                        rs.updateObject(nextQuery.field, nextQuery.newValue);
                        rs.updateRow();
                        break;
                    case Delete:
                        rs.absolute(nextQuery.row);
                        rs.deleteRow();
                        break;
                    case Insert:
                        rs.moveToInsertRow();
                        for (Iterator<Map.Entry<String, Object>> i = nextQuery.values.entrySet().iterator(); i.hasNext();) {
                            Map.Entry<String, Object> e = i.next();
                            rs.updateObject(e.getKey(), e.getValue());
                        }
                        rs.insertRow();
                        break;
                    default:
                        System.out.println("executePreparedQueries(): wrong queryTypes value");
                        break;
                }
                preparedQueries.remove();
            }
        }
        // Если появляется ошибка - заканчивается соединение с MySQL
        catch (Exception e) {
            PreparedQuery failedQuery = preparedQueries.poll();
            StringBuilder failedQueryInfo = new StringBuilder();
            switch (failedQuery.type) {
                case Update:
                    failedQueryInfo.append("Запрос на изменение ");
                    failedQueryInfo.append("поля: ");
                    failedQueryInfo.append(this.getColumnAlias(failedQuery.field));
                    failedQueryInfo.append(' ');
                    failedQueryInfo.append(failedQuery.field);
                    failedQueryInfo.append(" на строке номер ");
                    failedQueryInfo.append(failedQuery.row);
                    break;
                case Delete:
                    failedQueryInfo.append("Запрос на удаление строки номер ");
                    failedQueryInfo.append(failedQuery.row);
                    break;
                case Insert:
                    failedQueryInfo.append("Запрос на вставку новой строки со значениями:\n");
                    for (Iterator<Map.Entry<String, Object>> i = failedQuery.values.entrySet().iterator(); i.hasNext();) {
                        Map.Entry<String, Object> entry = i.next();
                        failedQueryInfo.append(this.getColumnAlias(entry.getKey()));
                        failedQueryInfo.append(' ');
                        failedQueryInfo.append(entry.getKey());
                        failedQueryInfo.append('=');
                        failedQueryInfo.append(entry.getValue());
                        failedQueryInfo.append('\n');
                    }
                    break;
            }
            System.out.println(e.getMessage());
            Alert executeError = new Alert(Alert.AlertType.ERROR, null, ButtonType.OK);
            executeError.setTitle(this.tableName + " - Ошибка");
            executeError.setHeaderText("Ошибка при выполнении запроса в БД\n" +
                    failedQueryInfo.toString() +
                    "\nЧтобы сохранить остальные изменения снова нажмите Сохранить");
            executeError.setContentText(e.getMessage());
            executeError.showAndWait();
        }
    }

    // Для удаления строки
    public void deleteRow(TableView tv, int row_index) {
        // Сохранение изменения в ObservableList data
        data.remove(row_index);
        // сохранение запроса
        preparedQueries.add(new PreparedQuery(row_index+1));
    }

    // Для вставки новой строки
    public void inputRow(TableView tv, HashMap<String, Object> values, ObservableList new_row) {
        // Сохранение изменения в ObservableList data
        data.add(new_row);
        // сохранение запроса
        preparedQueries.add(new PreparedQuery(values));
    }

    // Для возврата синонима названия столбца
    public String getColumnAlias(String col_name) {
        // TODO Заполнить для всех таблиц
        switch (tableName) {
            case "employees":
                switch (col_name) {
                    case "employee_id": return "Код Сотрудника";
                    case "surname": return "Фамилия";
                    case "name": return "Имя";
                    case "middle_name": return "Отчество";
                    case "phone_number": return "Номер телефона";
                    case "position_id": return "Код Должности";
                }
            case "medicine":
                switch (col_name) {
                    case "medicine_id": return "Код Препарата";
                    case "trade_name": return "Торговое Название";
                    case "international_name": return "Международное Название";
                    case "chemical_name": return "Химическое Название";
                    case "atc_code": return "Код АТХ";
                }
        }
        return col_name;
    }

    // Для заполнения TableView данными запроса
    // src: https://stackoverflow.com/questions/18941093/how-to-fill-up-a-tableview-with-database-data
    public void buildData(TableView tv) {
        final String query = "SELECT * FROM " + tableName;

        try (Connection conn = DriverManager.getConnection(this.connectionUrl, this.user, this.password); // Подключение к БД
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            // TABLE COLUMN ADDED DYNAMICALLY - в зависимости от данных таблицы
            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                //We are using non property style for making dynamic table
                final int j = i;
                String col_name = rs.getMetaData().getColumnName(i+1);
                columns.add(col_name);
                TableColumn col = new TableColumn(this.getColumnAlias(col_name));
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
                // Changes handler - для сохранения значений в структуре ObservableList при изменении ячейки
                col.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent cellEditEvent) {
                        // Сохранение изменения в ObservableList data
                        int cell_row = cellEditEvent.getTablePosition().getRow();
                        int cell_col = cellEditEvent.getTablePosition().getColumn();
                        data.get(cell_row).set(cell_col, cellEditEvent.getNewValue());
                        // сохранение запроса
                        preparedQueries.add(new PreparedQuery(cell_row+1, col_name, cellEditEvent.getNewValue()));
                    }
                });
                col.setCellFactory(TextFieldTableCell.forTableColumn());
                tv.getColumns().addAll(col);
            }
            // Data added to ObservableList
            while(rs.next()){
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                    //Iterate Column
                    row.add(rs.getString(i));
                }
                System.out.println("MySQLDriver.buildRow added: "+row );
                data.add(row);
            }
            //FINALLY ADDED TO TableView
            tv.setItems(data);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            Alert connectionError = new Alert(Alert.AlertType.ERROR, null, ButtonType.OK);
            connectionError.setTitle(this.tableName + " - Ошибка");
            connectionError.setHeaderText("Ошибка при получении таблицы");
            connectionError.setContentText(e.getMessage());
            connectionError.showAndWait();
        }
    }
}