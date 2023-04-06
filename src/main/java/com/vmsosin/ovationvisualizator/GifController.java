package com.vmsosin.ovationvisualizator;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.WritableImage;

import java.util.List;

/**
 * Class for stage with images quick showing.
 */
public class GifController {
    // Main canvas for visualization.
    @FXML
    public Canvas canvas;
    // Stop/play button.
    @FXML
    public Button buttonStop;
    // Slider for speed choosing.
    @FXML
    public Slider slider;
    // List of directory images.
    List<WritableImage> images;
    // Thread for uninterruptible visualizing.
    Thread thread;
    // Checker.
    boolean isPlaying = true;

    /**
     * Initializer where the main logic of the stage is described.
     * @param images all images for showing.
     */
    public void initData(List<WritableImage> images) {
        this.images = images;

        thread = new Thread(() -> {
            boolean start = true;
            int index = 0;

            while (start) {
                try {
                    Thread.sleep((int)slider.getValue());
                } catch (InterruptedException ignored) {}

                if (!isPlaying) {
                    continue;
                }

                canvas.getGraphicsContext2D().drawImage(
                        images.get(index),
                        0,
                        0,
                        canvas.getWidth(),
                        canvas.getHeight()
                );

                ++index;

                if (index == images.size()) {
                    index = 0;
                }
            }
        });

        thread.start();
    }

    /**
     * Method for button click.
     */
    public void clickStart() {
        buttonStop.setText(isPlaying ? "Play" : "Stop");
        isPlaying = !isPlaying;
    }
}
