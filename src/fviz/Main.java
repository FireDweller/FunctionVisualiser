package fviz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("f(x)VizView.fxml"));
        Scene scene = new Scene(root, 850, 640);
        scene.getStylesheets().add("fviz/style.css");

        stage.setResizable(false);
        stage.setTitle("F(x)Viz - Function Visualizer");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);

    }
}
