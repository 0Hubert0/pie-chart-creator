package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Cursor;
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
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

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
            final int[] selectedPartIndex = {-1};
            final double[] selectedPartAngle = new double[1];
            final double[] selectedPartExtent = new double[1];
            Color[] colours = {Color.RED, Color.MEDIUMSLATEBLUE, Color.BLUEVIOLET, Color.DARKKHAKI, Color.INDIANRED};
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

            //draw(chartCanvas, gr, percentages, colours, selectedPartIndex[0]);
            gr.fillArc(47, 45, 206, 210, 90, 30, ArcType.ROUND);
            gr.setFill(Color.RED);
            gr.fillArc(50, 50, 200, 200, 90, 30, ArcType.ROUND);

            Timeline chartAnimationGrow = new Timeline(new KeyFrame(Duration.millis(40), event1 -> {
                //draw(chartCanvas, gr, percentages, colours, selectedPartIndex);
                //gr.fillArc(50, 50, 200, 200, angle, tempAngle, ArcType.ROUND);
            }));

            Timeline chartAnimationDecrease = new Timeline(new KeyFrame(Duration.millis(40), event1 -> {
                //draw(chartCanvas, gr, percentages, colours, selectedPartIndex);
                //gr.fillArc(50, 50, 200, 200, angle, tempAngle, ArcType.ROUND);
            }));

            chartCanvas.setOnMouseMoved(event1 -> {
                if((event1.getX()-150)*(event1.getX()-150)+(event1.getY()-150)*(event1.getY()-150)<100*100){
                    double tempAngle = Math.atan(-(event1.getY()-150)/ (event1.getX()-150)), totalPercentages=0;
                    if(event1.getX()<150) tempAngle+=Math.PI;
                    tempAngle= tempAngle*180/Math.PI-90;
                    if(tempAngle<0) tempAngle+=360;
                    boolean animation = false;
                    for (int i = 0; i<percentages.size(); i++) {
                        if (tempAngle > (totalPercentages * 360 + 1) % 360
                                && tempAngle < ((totalPercentages+percentages.get(i)) * 360 - 1) % 360) {
                            selectedPartIndex[0] = i;
                            selectedPartAngle[0] = (totalPercentages*360+1) % 360;
                            selectedPartExtent[0] = (percentages.get(i)*360);
                            animation=true;
                            break;
                        }
                        totalPercentages+=percentages.get(i);
                    }
                    if(animation) chartRoot.setCursor(Cursor.HAND);
                    else chartRoot.setCursor(Cursor.DEFAULT);
                }
                else chartRoot.setCursor(Cursor.DEFAULT);
            });

            Stage chartStage = new Stage();
            chartStage.setScene(chartScene);
            chartStage.show();
        });

        root.getChildren().addAll(background, title, parts, plotButton);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }

    public static void draw(Canvas chartCanvas, GraphicsContext gr, List<Double> percentages,
                            Color[] colours, int selectedPartIndex){
        gr.clearRect(0, 0, chartCanvas.getWidth(), chartCanvas.getHeight());
        gr.setFill(Color.PAPAYAWHIP);
        gr.fillRect(0,0, chartCanvas.getWidth(), chartCanvas.getHeight());

        double angle = 90;

        for (int i = 0; i< percentages.size(); i++) {
            gr.setFill(colours[i]);
            gr.beginPath();
            double tempAngle = percentages.get(i) * 360;
            if(selectedPartIndex!=i) gr.fillArc(50, 50, 200, 200, angle, tempAngle, ArcType.ROUND);
            angle += tempAngle;
        }

        angle=Math.PI/2;

        for(Double percentage : percentages){
            gr.setStroke(Color.PAPAYAWHIP);
            gr.setLineWidth(3);
            gr.beginPath();
            gr.moveTo(150, 150);
            gr.lineTo(150+100*Math.cos(angle), 150-100*Math.sin(angle));
            gr.stroke();
            double tempAngle = percentage * 2*Math.PI;
            angle +=tempAngle;
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
