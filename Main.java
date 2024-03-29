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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        double gapsBetweenVisuals = 20;
        final boolean[] increased = {false};
        final HBox[] chosen = {null};
        List <TextField> partsList = new ArrayList<>();
        List <HBox> rows = new ArrayList<>();

        AnchorPane root = new AnchorPane();

        Scene scene = new Scene(root, 1200, 800);

        Rectangle background = new Rectangle(scene.getWidth(), scene.getHeight());
        background.setFill(Color.DARKGRAY);

        Text title = new Text("Pie Chart Creator");
        title.setFont(Font.font("Comic Sans MS", 80));
        title.setWrappingWidth(700);
        title.setTextAlignment(TextAlignment.CENTER);
        title.setLayoutX((scene.getWidth()-title.getWrappingWidth())/2);
        title.setLayoutY(100);

        ScrollPane parts = new ScrollPane();
        parts.setStyle("-fx-background-color: rgb(169, 169, 169); -fx-background: rgb(169, 169, 169)");
        parts.setMinWidth(202);
        parts.setLayoutY(300);
        parts.setMaxHeight(400);

        VBox content = new VBox();
        parts.setContent(content);

        HBox addHBox = new HBox();
        addHBox.setMinWidth(244);
        content.getChildren().add(addHBox);

        Button addButton = new Button("add");
        addButton.setFont(Font.font(18));
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
            if(content.getHeight()>360 && !increased[0]) {
                increased[0] =true;
                parts.setMinWidth(parts.getMinWidth()+62);
            }
            rows.add(hBox1);
        });

        Button plotButton = new Button("plot");
        plotButton.setFont(Font.font(18));
        plotButton.setLayoutX((scene.getWidth()-plotButton.getWidth())/2);
        plotButton.setLayoutY(720);
        plotButton.setOnAction(event -> {
            List <Double> values = new ArrayList<>();
            List <Double> percentages = new ArrayList<>();
            final boolean[] decreasePlay = {false};
            final int[] cycleCounter = {0};
            final int[] selectedPartIndex = {-1};
            final double[] selectedPartAngle = new double[1];
            final double[] selectedPartExtent = new double[1];
            double total = 0;
            List<Color> coloursList = new ArrayList<>();

            for (TextField current : partsList) {
                coloursList.add(Color.rgb((int)(Math.random()*256), (int)(Math.random()*256), (int)(Math.random()*256), 1));
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

            Canvas chartCanvas = new Canvas(chartScene.getWidth(), chartScene.getHeight());
            GraphicsContext gr = chartCanvas.getGraphicsContext2D();

            Rectangle infoRectangle = new Rectangle(100, 50, 20, 20);
            //TODO if the info rectangle will be starting in the right half of the graph, bedzie sie
            // zaczynal w przedluzeniu kata
            // else przedluzenie kata bedzie wskazywalo na jego top right wierzcholek

            chartRoot.getChildren().addAll(chartCanvas, infoRectangle);

            draw(chartCanvas, gr, percentages, coloursList, selectedPartIndex[0]);

            Timeline chartAnimationGrow = new Timeline(new KeyFrame(Duration.millis(10), event1 -> {
                drawCorrected(percentages, cycleCounter, selectedPartIndex, selectedPartAngle,
                        selectedPartExtent, coloursList, chartCanvas, gr);
                cycleCounter[0]++;
                decreasePlay[0] = true;
                if(cycleCounter[0] > 9) cycleCounter[0] = 9;
            }));

            chartAnimationGrow.setCycleCount(10);

            Timeline chartAnimationDecrease = new Timeline(new KeyFrame(Duration.millis(10), event1 -> {
                drawCorrected(percentages, cycleCounter, selectedPartIndex, selectedPartAngle,
                        selectedPartExtent, coloursList, chartCanvas, gr);
                cycleCounter[0]--;
                if(cycleCounter[0] == 0) decreasePlay[0] = false;
                if(cycleCounter[0] < 0) cycleCounter[0] = 0;
            }));

            chartAnimationDecrease.setCycleCount(10);

            double width = chartCanvas.getWidth()/2, height = chartCanvas.getHeight()/2;

            chartCanvas.setOnMouseMoved(event1 -> {
                if((event1.getX()-width)*(event1.getX()-width)+(event1.getY()-height)*(event1.getY()-height)<width*height/4){
                    double tempAngle = Math.atan(-(event1.getY()-height)/ (event1.getX()-width)), totalPercentages=0;
                    if(event1.getX()<width) tempAngle+=Math.PI;
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
            chartStage.setResizable(false);
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

                int index = -1;

                for (int i = 0; i < rows.size(); i++) {
                    if (rows.get(i) == chosen[0]) {
                        index = i;
                        break;
                    }
                }

                int tempIndex = index+1;
                if(tempIndex>=rows.size()) tempIndex=0;
                while(tempIndex!=index){

                    StackPane tempStack = (StackPane) (rows.get(tempIndex).getChildren().get(0));
                    Text tempText = (Text) tempStack.getChildren().get(0);
                    if(tempText.isVisible()) tempIndex++;
                    else{
                        chosen[0] = rows.get(tempIndex);
                        break;
                    }
                    if(tempIndex>=rows.size()) tempIndex=0;
                }
                if(tempIndex==index) chosen[0] = null;
            }
        });

        Timeline actualize = new Timeline(new KeyFrame(Duration.millis(10), event -> {
            title.setLayoutX((scene.getWidth()-title.getWrappingWidth())/2);
            if(!increased[0]) parts.setLayoutX((scene.getWidth()-parts.getMinWidth())/2-22);
            else parts.setLayoutX((scene.getWidth()-parts.getMinWidth())/2+9);
            plotButton.setLayoutX((scene.getWidth()-plotButton.getWidth())/2);
            background.setWidth(scene.getWidth());
            background.setHeight(scene.getHeight());
        }));

        actualize.setCycleCount(-1);
        actualize.play();

        root.getChildren().addAll(background, title, parts, plotButton);

        Stage stage = new Stage();
        stage.setMinWidth(700);
        stage.setMinHeight(parts.getMaxHeight()+400+plotButton.getHeight()+2*gapsBetweenVisuals);
        stage.setScene(scene);
        stage.show();
    }

    public void drawCorrected(List<Double> percentages, int[] cycleCounter, int[] selectedPartIndex,
                              double[] selectedPartAngle, double[] selectedPartExtent, List<Color> coloursList,
                              Canvas chartCanvas, GraphicsContext gr) {
        double width = chartCanvas.getWidth()/2, height = chartCanvas.getHeight()/2;
        draw(chartCanvas, gr, percentages, coloursList, selectedPartIndex[0]);
        gr.setFill(coloursList.get(selectedPartIndex[0]));
        gr.fillArc(width/2 - cycleCounter[0], height/2 - cycleCounter[0], width + 2*cycleCounter[0],
                height + 2*cycleCounter[0], selectedPartAngle[0] + 90,
                selectedPartExtent[0], ArcType.ROUND);
    }

    public static void draw(Canvas chartCanvas, GraphicsContext gr, List<Double> percentages,
                            List<Color> colours, int selectedPartIndex) {
        gr.clearRect(0, 0, chartCanvas.getWidth(), chartCanvas.getHeight());
        gr.setFill(Color.PAPAYAWHIP);
        gr.fillRect(0,0, chartCanvas.getWidth(), chartCanvas.getHeight());

        double angle = 90;
        double width = chartCanvas.getWidth()/2, height = chartCanvas.getHeight()/2;

        for (int i = 0; i< percentages.size(); i++) {
            gr.setFill(colours.get(i));
            gr.beginPath();
            double tempAngle = percentages.get(i) * 360;
            if(selectedPartIndex!=i) gr.fillArc(width/2, height/2, width, height, angle, tempAngle, ArcType.ROUND);
            angle += tempAngle;
        }

        angle=Math.PI/2;

        for(Double percentage : percentages){
            gr.setStroke(Color.PAPAYAWHIP);
            gr.setLineWidth(2);
            gr.beginPath();
            gr.moveTo(width, height);
            gr.lineTo(width*(1+Math.cos(angle)), height*(1-Math.sin(angle)));
            gr.stroke();
            double tempAngle = percentage * 2*Math.PI;
            angle +=tempAngle;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
