package com.vmsosin.ovationvisualizator;

import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import org.ghost4j.display.PageRaster;
import org.ghost4j.document.DocumentException;
import org.ghost4j.document.PSDocument;
import org.ghost4j.renderer.RendererException;
import org.ghost4j.renderer.SimpleRenderer;
import org.ghost4j.util.ImageUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Main stage with visualization.
 */
public class ShowController {
    // ComboBox for choosing files.
    @FXML
    public ComboBox<File> fileComboBox;
    // Button to show previous image.
    @FXML
    public ImageView back;
    // Button to show next image.
    @FXML
    public ImageView next;
    // Main canvas for visualization.
    @FXML
    Canvas canvas;
    // List of directory files.
    List<File> files;
    // Renderer for image high quality.
    SimpleRenderer renderer = new SimpleRenderer();
    // Image after parsing.
    PSDocument currentDocument;
    // Image before saving.
    Image currentImage;
    // Scaling parameter.
    double scale = 1;

    /**
     * Main stage initialization.
     * @param files files from chose directory.
     */
    public void initData(List<File> files) {
        this.files = files;

        // Add files from directory to ComboBox.
        fileComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(File file) {
                return file.getName();
            }

            @Override
            public File fromString(String s) {
                return files.stream().filter(u -> u.getName().equals(s)).findFirst().get();
            }
        });

        initComboBox();

        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("back.png")));
        back.setImage(image);
        next.setImage(image);

        fileComboBox.setValue(files.get(0));

        // For image scale.
        canvas.setOnScroll((event) -> {
            if (event.getDeltaY() > 0 && scale < 3) {
                scale += 0.1;
            } else if (event.getDeltaY() < 0 && scale > 0.3) {
                scale -= 0.1;
            }

            canvas.setScaleX(scale);
            canvas.setScaleY(scale);
        });
    }

    /**
     * ComboBox initialization.
     */
    public void initComboBox() {
        fileComboBox.getSelectionModel().selectedItemProperty().addListener((opt, old, curr) -> {
            try {
                changeFile(curr);
            } catch (IOException | DocumentException | RendererException ignored) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Attention!");
                alert.setContentText("Choosing file is corrupted");

                alert.show();
            }
        });

        try {
            fileComboBox.setItems(FXCollections.observableList(files));
        } catch (Exception ignored) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Attention!");
            alert.setContentText("An unexpected error occurred");

            alert.show();
        }
    }

    /**
     * Select next file.
     */
    public void nextFile() {
        fileComboBox.getSelectionModel().selectNext();
    }

    /**
     * Select previous file.
     */
    public void previousFile() {
        fileComboBox.getSelectionModel().selectPrevious();
    }

    /**
     * Method for saving image.
     */
    public void saveAsPng() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("test.png");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image files (*.png)", "*.png")
        );

        File file = fileChooser.showSaveDialog(next.getScene().getWindow());

        if (file == null) {
            return;
        }

        ImageIO.write(SwingFXUtils.fromFXImage(currentImage, null), "png", file);
    }

    /**
     * Method with change file logic.
     * @param file selected file.
     */
    public void changeFile(File file) throws IOException, DocumentException, RendererException {
        renderer.setResolution(100);
        currentDocument = new PSDocument();
        currentDocument.load(file);

        List<PageRaster> raster = renderer.run(currentDocument, 0, currentDocument.getPageCount());
        currentImage = SwingFXUtils.toFXImage(
                (BufferedImage) ImageUtil.converterPageRasterToImage(raster.get(0)),
                null
        );
        canvas.getGraphicsContext2D().drawImage(currentImage, 0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * When user wants to select another directory.
     */
    public void reload() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("start-view.fxml"));
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Open Files");
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Method for quickly showing button.
     */
    public void showGif() throws IOException, DocumentException, RendererException {
        List<WritableImage> images = new ArrayList<>();

        for (File file : files) {
            PSDocument document = new PSDocument();
            document.load(file);
            List<PageRaster> raster = renderer.run(document, 0, document.getPageCount());
            images.add(SwingFXUtils.toFXImage(
                    (BufferedImage) ImageUtil.converterPageRasterToImage(raster.get(0)),
                    null
                    )
            );
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("gif-view.fxml"));

        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(new Scene(loader.load()));

        GifController controller = loader.getController();
        stage.setTitle("Show quickly");
        stage.setResizable(false);
        stage.show();

        controller.initData(images);
    }
}
