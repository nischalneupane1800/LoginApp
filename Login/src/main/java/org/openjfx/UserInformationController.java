package org.openjfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;


public class UserInformationController {
    @FXML
    private Text name;
    @FXML
    private Text gender;
    @FXML
    private Text age;
    @FXML
    private Text salary;
    @FXML
    private Button ok_btn;

//    this is not a fxml component
    private String user_id = null;

    @FXML
    private void pressOkBtn(ActionEvent event){
        FileChooser file_chooser = new FileChooser();
        FileChooser.ExtensionFilter excel_ext_filter =
                new FileChooser.ExtensionFilter("Excel 1997 (*.xls)","*.xls");
        file_chooser.getExtensionFilters().add(excel_ext_filter);
        File file = file_chooser.showSaveDialog((Stage) this.ok_btn.getScene().getWindow());
        if(file != null){
            String[] user_info = getUserInformation(this.user_id);
            try {
                this.writeWorkbook(file,user_info);
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Saved Successfully");
                a.setHeaderText("File Saved Successfully");
                a.setContentText("Excel File \""+file.getAbsoluteFile()+"\" Saved Successfully");
                a.show();
            } catch (IOException e) {
                System.out.println("Cannot Save Workbook While Pressing OK Button");
                e.printStackTrace();
            }
        }
    }

    public void initData(String id){
        this.user_id = id;
        String[] user_info = getUserInformation(id);
        this.name.setText(user_info[0]);
        this.age.setText(user_info[1]);
        this.gender.setText(user_info[2]);
        this.salary.setText(user_info[3]);
    }

    private String[] getUserInformation(String id){
        SqliteConnection sqlite_conn = new SqliteConnection();
        String[] user_info = new String[4];
        try {
            String sql = "SELECT ct_users_tbl.name as name, " +
                    "ct_user_details_tbl.age as age, " +
                    "ct_user_details_tbl.gender as gender, " +
                    "ct_user_details_tbl.salary as salary " +
                    "FROM ct_users_tbl " +
                    "INNER JOIN ct_user_details_tbl " +
                    "ON ct_users_tbl.id = ct_user_details_tbl.user_id " +
                    "WHERE ct_users_tbl.id=" + id + ";";
            Statement stmt = sqlite_conn.conn.createStatement();
            ResultSet result = stmt.executeQuery(sql);
            if (result.next()) {
                user_info[0] = result.getString("name");
                user_info[1] = Integer.toString(result.getInt("age"));
                user_info[2] = result.getString("gender");
                user_info[3] = Double.toString(result.getDouble("salary"));
            }
            result.close();
            stmt.close();
            sqlite_conn.disconnect();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("User Information Retrieval Error");
            a.setHeaderText("Error While retrieving User Information in UserInformationController: SQLException");
            a.setContentText(e.getMessage());
            a.showAndWait();
        }
        return user_info;
    }

    public void writeWorkbook(File file,String[] data) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("User Information");

        Object[][] bookData = {
                {"Name", "Age", "Gender","Salary"},
                {data[0],data[1],data[2],data[3]},
        };

        int rowCount = 0;

        for (Object[] aBook : bookData) {
            Row row = sheet.createRow(++rowCount);

            int columnCount = 0;

            for (Object field : aBook) {
                Cell cell = row.createCell(++columnCount);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
            }

        }

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            workbook.write(outputStream);
        }
    }
}
