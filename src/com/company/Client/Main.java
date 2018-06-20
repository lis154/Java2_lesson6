package com.company.Client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");

        Scene scene =  new Scene(root, 300, 275);


        primaryStage.setScene(scene);
        // scene.getStylesheets().add((getClass().getResource("/css/style.css")).toExternalForm());
        primaryStage.show();

      //  primaryStage.setOnCloseRequest(e -> Platform.exit());
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                System.out.println("Stage is closing");
                System.exit(0);
            }
        });

    }




    public static void main(String[] args) {
        launch(args);
    }
}
