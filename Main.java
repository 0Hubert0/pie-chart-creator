package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        final double[] total = {0};
        List <TextField> partsList = new ArrayList<>();
        List <Double> values = new ArrayList<>();
        List <Double> percentages = new ArrayList<>();

        AnchorPane root = new AnchorPane();

        Scene scene = new Scene(root, 1200, 800);

        Rectangle background = new Rectangle(scene.getWidth(), scene.getHeight());
        background.setFill(Color.DARKGRAY);

        Text title = new Text("Pie Chart Creator");
        title.setFont(Font.font(25));
        title.setLayoutX(scene.getWidth()/2);
        title.setLayoutY(100);

        ScrollPane parts = new ScrollPane();
        parts.setMinWidth(200);
        parts.setLayoutX(scene.getWidth()/2-100);
        parts.setLayoutY(300);
        parts.setMaxHeight(200);

        VBox content = new VBox();
        parts.setContent(content);

        HBox addHBox = new HBox();
        content.getChildren().add(addHBox);

        Button addButton = new Button("add");
        addButton.setLayoutX((addHBox.getWidth()-addButton.getWidth())/2);
        addHBox.getChildren().add(addButton);
        addButton.setOnAction(event -> {
            HBox hBox1 = new HBox();
            TextField partTextField = new TextField();
            Button insertButton = new Button("insert");
            insertButton.setOnAction(event1 -> {
                partsList.add(partTextField);
            });
            addHBox.setLayoutY(addHBox.getLayoutY()+100);
            hBox1.getChildren().addAll(partTextField, insertButton);
            content.getChildren().add(hBox1);
        });

        Button plotButton = new Button("plot");
        plotButton.setLayoutX((scene.getWidth()-plotButton.getWidth())/2);
        plotButton.setLayoutY(700);
        plotButton.setOnAction(event -> {
            total[0] = 0;
            for (TextField current : partsList) {
                String value = current.getText();
                try {
                    double numericalValue = Double.parseDouble(value);
                    total[0] += numericalValue;
                    values.add(numericalValue);
                } catch (Exception e) {
                    System.out.println("Wrong input");
                }
            }

            for (Double value : values) {
                percentages.add(value / total[0]);
            }

            AnchorPane chartRoot = new AnchorPane();

            Scene chartScene = new Scene(chartRoot, 600, 600);

            Rectangle chartBackground = new Rectangle(scene.getWidth(), scene.getHeight());
            chartBackground.setFill(Color.DARKGRAY);

            Canvas chartCanvas = new Canvas(300, 300);
            chartCanvas.setLayoutX(150);
            chartCanvas.setLayoutY(150);
            GraphicsContext gr = chartCanvas.getGraphicsContext2D();

            chartRoot.getChildren().addAll(chartBackground, chartCanvas);

            gr.clearRect(0, 0, chartCanvas.getWidth(), chartCanvas.getHeight());
            gr.setFill(Color.PAPAYAWHIP);
            gr.fillRect(0,0, chartCanvas.getWidth(), chartCanvas.getHeight());

            double angle = 90;

            for (Double percentage : percentages) {
                double startingX = 150 + 5 * Math.cos(angle),
                        startingY = 150 + 5 * Math.sin(angle);

                //System.out.println(startingX + ", " + startingY);

                gr.beginPath();
                gr.moveTo(startingX, startingY);
                double tempAngle = percentage * 360;
                System.out.println(tempAngle);
                gr.arc(startingX, startingY, 95, 95, angle, Math.toDegrees(tempAngle) * 95);
                System.out.println(angle + ", " + Math.toRadians(tempAngle) * 95);
                gr.lineTo(startingX, startingY);
                gr.stroke();
                angle -= tempAngle;
                //TODO choose whether I want to have angles in degrees or radians(canvas uses degrees, Math library radians)
            }

            Stage chartStage = new Stage();
            chartStage.setScene(chartScene);
            chartStage.show();
        });

        root.getChildren().addAll(background, title, parts, plotButton);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
