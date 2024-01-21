package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
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
        final HBox[] chosen = {null};
        List <TextField> partsList = new ArrayList<>();

        AnchorPane root = new AnchorPane();

        Scene scene = new Scene(root, 1200, 800);

        Rectangle background = new Rectangle(scene.getWidth(), scene.getHeight());
        background.setFill(Color.DARKGRAY);

        Text title = new Text("Pie Chart Creator");
        title.setFont(Font.font(25));
        title.setLayoutX(scene.getWidth()/2);
        title.setLayoutY(100);

        ScrollPane parts = new ScrollPane();
        parts.setMinWidth(202);
        parts.setLayoutX(scene.getWidth()/2-100);
        parts.setLayoutY(300);
        parts.setMaxHeight(200);

        VBox content = new VBox();
        parts.setContent(content);

        HBox addHBox = new HBox();
        addHBox.setMinWidth(244);
        content.getChildren().add(addHBox);

        Button addButton = new Button("add");
        addHBox.getChildren().add(addButton);
        addHBox.setAlignment(Pos.CENTER);
        addButton.setOnAction(event -> {
            HBox hBox1 = new HBox();
            StackPane textStack = new StackPane();
            Text numberAdded = new Text();
            numberAdded.setVisible(false);
            TextField partTextField = new TextField();
            chosen[0] = hBox1;
            partTextField.setOnMouseClicked(event1 -> {
                chosen[0] = hBox1;
            });
            textStack.getChildren().addAll(numberAdded, partTextField);
            StackPane stack = new StackPane();
            stack.setOnMouseEntered(event1 -> stack.setCursor(Cursor.HAND));
            stack.setOnMouseExited(event1 -> stack.setCursor(Cursor.HAND));
            Text back = new Text("back");
            back.setVisible(false);
            Button insertButton = new Button("insert");
            insertButton.setOnAction(event1 -> {
                numberAdded.setText(partTextField.getText());
                numberAdded.setVisible(true);
                partTextField.setVisible(false);
                partsList.add(partTextField);
                back.setVisible(true);
                insertButton.setVisible(false);
            });
            back.setOnMouseClicked(event1 -> {
                for (int i = 0; i < partsList.size(); i++) {
                    if(partsList.get(i)==partTextField) {
                        partsList.remove(partTextField);
                        break;
                    }
                }
                back.setVisible(false);
                insertButton.setVisible(true);
                numberAdded.setVisible(false);
                partTextField.setVisible(true);
            });
            stack.getChildren().addAll(back, insertButton);
            hBox1.getChildren().addAll(textStack, stack);
            content.getChildren().add(hBox1);
        });

        Button plotButton = new Button("plot");
        plotButton.setLayoutX((scene.getWidth()-plotButton.getWidth())/2);
        plotButton.setLayoutY(700);
        plotButton.setOnAction(event -> {
            List <Double> values = new ArrayList<>();
            List <Double> percentages = new ArrayList<>();
            final boolean[] decreasePlay = {false};
            final int[] cycleCounter = {0};
            final int[] selectedPartIndex = {-1};
            final double[] selectedPartAngle = new double[1];
            final double[] selectedPartExtent = new double[1];
            double total = 0;
            Color[] colours = {Color.RED, Color.MEDIUMSLATEBLUE, Color.BLUEVIOLET, Color.DARKKHAKI, Color.INDIANRED};

            for (TextField current : partsList) {
                String value = current.getText();
                try {
                    double numericalValue = Double.parseDouble(value);
                    total += numericalValue;
                    values.add(numericalValue);
                } catch (Exception e) {
                    System.out.println("Wrong input");
                }
            }

            for (Double value : values) {
                percentages.add(value / total);
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

            draw(chartCanvas, gr, percentages, colours, selectedPartIndex[0]);

            Timeline chartAnimationGrow = new Timeline(new KeyFrame(Duration.millis(10), event1 -> {
                drawCorrected(percentages, cycleCounter, selectedPartIndex, selectedPartAngle,
                        selectedPartExtent, colours, chartCanvas, gr);
                cycleCounter[0]++;
                decreasePlay[0] = true;
                if(cycleCounter[0] > 9) cycleCounter[0] = 9;
            }));

            chartAnimationGrow.setCycleCount(10);

            Timeline chartAnimationDecrease = new Timeline(new KeyFrame(Duration.millis(10), event1 -> {
                drawCorrected(percentages, cycleCounter, selectedPartIndex, selectedPartAngle,
                        selectedPartExtent, colours, chartCanvas, gr);
                cycleCounter[0]--;
                if(cycleCounter[0] == 0) decreasePlay[0] = false;
                if(cycleCounter[0] < 0) cycleCounter[0] = 0;
            }));

            chartAnimationDecrease.setCycleCount(10);

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
                            selectedPartAngle[0] = (totalPercentages*360) % 360;
                            selectedPartExtent[0] = (percentages.get(i)*360);
                            animation=true;
                            break;
                        }
                        totalPercentages+=percentages.get(i);
                    }
                    if(animation) {
                        chartRoot.setCursor(Cursor.HAND);
                        chartAnimationGrow.play();
                    }
                    else {
                        chartRoot.setCursor(Cursor.DEFAULT);
                        chartAnimationGrow.pause();
                        if (decreasePlay[0]) chartAnimationDecrease.play();
                    }
                }
                else {
                    chartRoot.setCursor(Cursor.DEFAULT);
                    chartAnimationGrow.pause();
                    if (decreasePlay[0]) chartAnimationDecrease.play();
                }
            });

            Stage chartStage = new Stage();
            chartStage.setScene(chartScene);
            chartStage.show();
        });

        scene.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER && chosen[0]!=null){
                StackPane textStack = (StackPane) (chosen[0].getChildren().get(0));
                Text numberAdded = (Text) textStack.getChildren().get(0);
                TextField partTextField = (TextField) textStack.getChildren().get(1);

                StackPane stack = (StackPane) (chosen[0].getChildren().get(1));
                Text back = (Text) stack.getChildren().get(0);
                Button insertButton = (Button) stack.getChildren().get(1);

                numberAdded.setText(partTextField.getText());
                numberAdded.setVisible(true);
                partTextField.setVisible(false);
                partsList.add(partTextField);
                back.setVisible(true);
                insertButton.setVisible(false);
            }
        });

        //Timeline actualize = new Timeline(new KeyFrame(Duration.millis(10), event -> {
        //    addHBox.setAlignment(Pos.CENTER);
        //}));

        //actualize.setCycleCount(-1);
        //actualize.play();

        root.getChildren().addAll(background, title, parts, plotButton);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }

    public void drawCorrected(List<Double> percentages, int[] cycleCounter, int[] selectedPartIndex,
                              double[] selectedPartAngle, double[] selectedPartExtent, Color[] colours,
                              Canvas chartCanvas, GraphicsContext gr) {
        draw(chartCanvas, gr, percentages, colours, selectedPartIndex[0]);
        gr.setFill(colours[selectedPartIndex[0]]);
        gr.fillArc(50- cycleCounter[0], 50- cycleCounter[0], 200+2* cycleCounter[0],
                200+2* cycleCounter[0], selectedPartAngle[0] + 90,
                selectedPartExtent[0], ArcType.ROUND);
    }

    public static void draw(Canvas chartCanvas, GraphicsContext gr, List<Double> percentages,
                            Color[] colours, int selectedPartIndex) {
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
            gr.setLineWidth(2);
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
