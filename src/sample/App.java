package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("view.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("DSA Signature master");
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}