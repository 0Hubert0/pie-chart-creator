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

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
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
            addHBox.setLayoutY(addHBox.getLayoutY()+100);
            hBox1.getChildren().addAll(partTextField, insertButton);
            content.getChildren().add(hBox1);
        });

        Button plotButton = new Button("plot");
        plotButton.setLayoutX((scene.getWidth()-plotButton.getWidth())/2);
        plotButton.setLayoutY(700);
        plotButton.setOnAction(event -> {
            AnchorPane chartRoot = new AnchorPane();

            Scene chartScene = new Scene(chartRoot, 600, 600);

            Canvas chartCanvas = new Canvas(300, 300);
            GraphicsContext gr = chartCanvas.getGraphicsContext2D();

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
