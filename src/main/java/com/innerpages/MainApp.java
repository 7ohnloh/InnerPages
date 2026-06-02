package com.innerpages;

import com.innerpages.controller.NavigationController;
import com.innerpages.dao.DatabaseManager;
import com.innerpages.view.SidebarView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        DatabaseManager.initializeDatabase();

        Label titleLabel = new Label("InnerPages");
        titleLabel.setFont(Font.font(20));

        Label contentLabel = new Label("Welcome to InnerPages");
        contentLabel.setFont(Font.font(18));

        var sidebar = SidebarView.createSidebar();
        sidebar.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                contentLabel.setText("Welcome to InnerPages\n\nSelected: " + newValue);
                new NavigationController().navigateTo(newValue);
            }
        });

        VBox leftPane = new VBox(12, titleLabel, sidebar);
        leftPane.setPadding(new Insets(12));
        leftPane.setStyle("-fx-background-color: #f0f0f0;");

        BorderPane root = new BorderPane();
        root.setLeft(leftPane);
        root.setCenter(contentLabel);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 700, 450);
        stage.setTitle("InnerPages");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
