package com.pharmacy.classes;

import java.sql.*;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Callback;

public class MySQLDriver {
    public static void main(String[] args) {
        String sqlSelectAllPersons = "SELECT * FROM positions";
        String connectionUrl = "jdbc:mysql://localhost:3306/pharmacy";

        try (Connection conn = DriverManager.getConnection(connectionUrl, "root", "mikeqwer2246");
             PreparedStatement ps = conn.prepareStatement(sqlSelectAllPersons);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("position_id");
                String name = rs.getString("name");

                System.out.println(id + name);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Для заполнения TableView данными запроса
    // src: https://stackoverflow.com/questions/18941093/how-to-fill-up-a-tableview-with-database-data
    public static void buildData(TableView tv, String query) {
        String connectionUrl = "jdbc:mysql://localhost:3306/pharmacy";
        ObservableList<ObservableList> data = FXCollections.observableArrayList();

        try (Connection conn = DriverManager.getConnection(connectionUrl, "root", "mikeqwer2246");
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            // TABLE COLUMN ADDED DYNAMICALLY
            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
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