package com.pharmacy.classes;

import java.sql.*;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Iterator;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.Callback;

// Используется для подключения к БД для работы с кокретной таблицей
public class MySQLDriver {
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
                nextQuery = preparedQueries.poll();
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
                            i.remove();
                        }
                        rs.insertRow();
                        break;
                    default:
                        System.out.println("executePreparedQueries(): wrong queryTypes value");
                        break;
                }
            }
        }
        // Если появляется ошибка - заканчивается соединение с MySQL
        catch (Exception e) {
            System.out.println(e.getMessage());
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
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
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
                        preparedQueries.add(new PreparedQuery(cell_row+1, col.getText(), cellEditEvent.getNewValue()));
                    }
                });
                col.setCellFactory(TextFieldTableCell.forTableColumn());
                tv.getColumns().addAll(col);
                System.out.println("Column ["+i+"] ");
            }
            // Data added to ObservableList
            while(rs.next()){
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                    //Iterate Column
                    row.add(rs.getString(i));
                }
                System.out.println("Row [1] added "+row );
                data.add(row);
            }
            //FINALLY ADDED TO TableView
            tv.setItems(data);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}