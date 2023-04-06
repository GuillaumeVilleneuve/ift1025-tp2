package com.example.client_fx;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class GUIView extends Application {
    // attributes

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("interface.fxml"));
        Scene scene = new Scene(fxmlLoader.load());


        stage.setTitle("Inscription UDEM");
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);
        stage.setResizable(true);

        stage.show();
    }


    public static void run() throws IOException {
        launch();
    }

}
