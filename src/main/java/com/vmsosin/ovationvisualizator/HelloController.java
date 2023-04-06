package com.vmsosin.ovationvisualizator;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * Class for choosing file directory.
 */
public class HelloController {
    // Text on first view.
    @FXML
    Text errorData;
    // Path to catalog.
    @FXML
    TextField path;

    /**
     * Opening by path.
     */
    @FXML
    protected void openByPath() {
        openFile(new File(path.getText()));
    }

    /**
     * Opening from computer`s memory.
     */
    @FXML
    protected void openByFile() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Open Files");
        File file = chooser.showDialog(errorData.getScene().getWindow());

        if (file != null) {
            openFile(file);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Attention!");
            alert.setContentText("Choose directory with Postscript-files!");

            alert.show();
        }
    }

    /**
     * Try to open files.
     * @param file this file.
     */
    private void openFile(File file) {
        if (file.exists()) {
            try {
                Stage closingStage = (Stage) errorData.getScene().getWindow();
                closingStage.close();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("show-view.fxml"));
                Stage stage = new Stage(StageStyle.DECORATED);
                stage.setScene(new Scene(loader.load()));
                stage.setTitle("Show visualization");
                stage.setResizable(false);

                ShowController controller = loader.getController();
                controller.initData(List.of(
                        Objects.requireNonNull(file.listFiles(
                                (e) -> e.isFile() && e.canRead() && e.getName().endsWith(".ps")
                        ))
                ));

                stage.show();
            } catch (Exception exception) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Attention!");
                alert.setContentText("This directory has files of the wrong format!" + "\n" +
                        "Please select directory with a standard .ps file from Ovation Prime.");

                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Attention!");
            alert.setContentText("The path is incorrect! Try again!");

            alert.show();
        }
    }
}
