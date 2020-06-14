package org.openjfx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteConnection {
    public Connection conn = null;

    public SqliteConnection(){
        connect();
//        The code below can be used to initialize the database
//        dropExistingTable();
//        createTables();
//        insertDataIntoTables();
    }
    public void connect(){
        String db_name = "src/main/resources/database/ct_acceptance.db";
        String url = "jdbc:sqlite:"+db_name;
        boolean show_alert = false;
        String header = "";
        String content = "";
        try{
            Class.forName("org.sqlite.JDBC");
            this.conn = DriverManager.getConnection(url);
        }
        catch(SQLException e){
            show_alert = true;
            header = "Error While Connecting to Database: SQL Exception";
            content = e.getMessage();
        }
        catch( ClassNotFoundException e){
            show_alert = true;
            header = "Error While Connecting to Database: Class Not Found";
            content = e.getMessage();
        }
        catch(Exception e){
            show_alert = true;
            header = "Error While Connecting to Database: Exception";
            content = e.getMessage();
        }
        if(show_alert){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText(header);
            a.setContentText(content);
            a.setTitle("Connection Error");
            a.show();
        }
    }

    public void disconnect(){
            try{
                if(this.conn!=null){
                    this.conn.close();
                }
            }
            catch (SQLException e){
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setHeaderText("Error While Closing Connection to Database: ");
                a.setContentText(e.getMessage());
                a.setTitle("Connection Error");
                a.show();
            }
    }

    private boolean dropExistingTable(){
        String[] drop_tables = new String[2];
        drop_tables[0] = "DROP TABLE IF EXISTS ct_users_tbl;";
        drop_tables[1] = "DROP TABLE IF EXISTS ct_user_details_tbl;";
        //create table if the tables does not exist
        try {
            for (String query : drop_tables) {
                Statement stmt = this.conn.createStatement();
                stmt.executeUpdate(query);
                stmt.close();
            }
            return true;
        }
        catch (SQLException e){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Create Table ERROR");
            a.setHeaderText("Error While Dropping Tables If Exist Database: SQL Exception Found");
            a.setContentText(e.getMessage());
            a.show();
        }
        return false;
    }
    private boolean createTables(){
        String[] tables = new String[2];
        tables[0] = "CREATE TABLE IF NOT EXISTS ct_users_tbl(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name VARCHAR(255) NOT NULL UNIQUE," +
                "password VARCHAR(255) NOT NULL,"+
                "created_at TIME DEFAULT CURRENT_TIMESTAMP," +
                "created_date DATE DEFAULT CURRENT_TIMESTAMP"+
                ");";
        tables[1] = "CREATE TABLE IF NOT EXISTS ct_user_details_tbl(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL UNIQUE," +
                "age INTEGER,"+
                "gender VARCHAR(20)," +
                "salary REAL," +
                "FOREIGN KEY(user_id) REFERENCES ct_users_tbl(id)"+
                ");";
        //create table if the tables does not exist
        try {
            for (String query : tables) {
                Statement stmt = this.conn.createStatement();
                stmt.executeUpdate(query);
                stmt.close();
            }
            return true;
        }
        catch (SQLException e){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Create Table ERROR");
            a.setHeaderText("Error While Creating Tables In Database: SQL Exception Found");
            a.setContentText(e.getMessage());
            a.show();
        }
        return false;

    }

    private boolean insertDataIntoTables(){
        String[] insert_ct_users = new String[2];
        insert_ct_users[0] = "INSERT INTO " +
                "ct_users_tbl(name,password)" +
                "VALUES (\"Nischal\",\"Nisch@l123\")";
        insert_ct_users[1] = "INSERT INTO " +
                "ct_users_tbl(name,password)" +
                "VALUES (\"Niruta\",\"Nirut@123\")";
        String[] insert_ct_details = new String[2];
        insert_ct_details[0] = "INSERT INTO " +
                "ct_user_details_tbl(user_id,age,gender,salary)" +
                "VALUES (1,24,\"Male\",20000)";
        insert_ct_details[1] = "INSERT INTO " +
                "ct_user_details_tbl(user_id,age,gender,salary)" +
                "VALUES (2,21,\"Female\",15000)";
        //create table if the tables does not exist
        try {
            for (String query : insert_ct_users) {
                Statement stmt = this.conn.createStatement();
                stmt.executeUpdate(query);
                stmt.close();
            }
            for (String query : insert_ct_details) {
                Statement stmt = this.conn.createStatement();
                stmt.executeUpdate(query);
                stmt.close();
            }
            return true;
        }
        catch (SQLException e){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Insert Data Into Table ERROR");
            a.setHeaderText("Error While Inserting Data Into Tables of Database: SQL Exception Found");
            a.setContentText(e.getMessage());
            a.show();
        }
        return false;
    }

}
