package org.openjfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import org.openjfx.SqliteConnection;
/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException{
        SqliteConnection sqlite_conn = new SqliteConnection();
        sqlite_conn.connect();
       scene = new Scene(App.loadFXML("login"));
       scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
       stage.setScene(scene);
       stage.setTitle("Login");
       stage.show();

    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}