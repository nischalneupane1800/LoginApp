package org.openjfx;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.openjfx.SqliteConnection;

import javax.xml.transform.Result;

public class LoginController {
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button login_btn;

    @FXML
    public void login(ActionEvent event) throws IOException{
        if(username.getText().isEmpty()){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Username Is not Valid");
            a.setContentText("Username Should not be empty ");
            a.show();
        }
        else if(!validatePassword(password.getText())){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Password Is not Valid");
            a.setHeaderText("Password must follow the following rules: ");
            a.setContentText("Password is required\n" +
                    "At least one digit [0-9]\n" +
                    "At least one lowercase character [a-z]\n" +
                    "At least one uppercase character [A-Z]\n" +
                    "At least 8 characters in length, but no more than 32");
            a.show();
        }
        else if(checkLogin(username.getText(),password.getText())){
            SqliteConnection  sqlite_conn = new SqliteConnection();
            try{
                String sql = "SELECT ct_users_tbl.id as id " +
                        "FROM ct_users_tbl " +
                        "INNER JOIN ct_user_details_tbl " +
                        "ON ct_users_tbl.id = ct_user_details_tbl.user_id " +
                        "WHERE name='"+username.getText()+"'";

                Statement stmt = sqlite_conn.conn.createStatement();
                ResultSet result = stmt.executeQuery(sql);
                int res_count = 0;
                String id = null;
                if(result.next()) {
                    id =  Integer.toString(result.getInt("id"));
                }
                result.close();
                stmt.close();
                sqlite_conn.disconnect();
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Successful Login");
                a.setHeaderText("Logged in");
                a.setContentText("Login Successful !!!");
                a.show();
                final String user_id = id;
                a.setOnCloseRequest(close_event ->{
                    Stage stage = (Stage) login_btn.getScene().getWindow();
                    stage.close();
                    try {
                        showUserInformationWindow(user_id);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            }
            catch(SQLException e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("User Information Retrieval Error");
                a.setHeaderText("Error While retrieving User Information in LoginController : SQLException");
                a.setContentText(e.getMessage());
                a.show();
            }
        }
    }

    private void showUserInformationWindow(String id) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("user_information" + ".fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(scene);
        stage.setTitle("User Information");
        UserInformationController user_info_controller = fxmlLoader.<UserInformationController>getController();
        user_info_controller.initData(id);
        stage.show();

    }
    private boolean validatePassword(String pass){
        /*
        At least one digit [0-9]
        At least one lowercase character [a-z]
        At least one uppercase character [A-Z]
        At least 8 characters in length, but no more than 32
         */
        int count = 0;

        if( 8 <= pass.length() && pass.length() <= 32  )
        {
            if( pass.matches(".*\\d.*") )
                count ++;
            if( pass.matches(".*[a-z].*") )
                count ++;
            if( pass.matches(".*[A-Z].*") )
                count ++;
//            some problem faced while using regex expression
//            if( pass.matches(".*\\[*.!@#$%^&()\\{\\}[]:\";'<>,.?/~`_+-=|\\\\].*") )
//            count ++;
        }

        return count >= 3;
    }

    private boolean checkLogin(String username, String password){
        SqliteConnection  sqlite_conn = new SqliteConnection();
        try{
            String sql = "SELECT * FROM ct_users_tbl WHERE name='"+username+"' AND " +
                    "password='"+password+"';";
            Statement stmt = sqlite_conn.conn.createStatement();
            ResultSet result = stmt.executeQuery(sql);
            int res_count = 0;
            if(result.next()) {
                res_count += 1;
            }
            result.close();
            stmt.close();
            sqlite_conn.disconnect();
            if(res_count==1){
                return true;
            }
        }
        catch(SQLException e){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("User Validation Error");
            a.setHeaderText("Error While Validating Username and Password : SQLException");
            a.setContentText(e.getMessage());
            a.show();
            return false;
        }
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Login Is not Valid");
        a.setContentText("Username or Password does not match");
        a.show();
        return false;
    }

}
