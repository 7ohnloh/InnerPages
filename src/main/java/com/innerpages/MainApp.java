package com.innerpages;

import com.innerpages.controller.NavigationController;
import com.innerpages.dao.CategoryDao;
import com.innerpages.dao.DatabaseManager;
import com.innerpages.model.Category;
import com.innerpages.view.SidebarView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Optional;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        DatabaseManager.initializeDatabase();
        CategoryDao categoryDao = new CategoryDao();

        Label titleLabel = new Label("InnerPages");
        titleLabel.setFont(Font.font(20));

        Label contentLabel = new Label("Welcome to InnerPages");
        contentLabel.setFont(Font.font(18));

        var sidebar = SidebarView.createSidebar(categoryDao.findAll());
        sidebar.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                contentLabel.setText("Welcome to InnerPages\n\nSelected: " + newValue.getName());
                new NavigationController().navigateTo(newValue.getName());
            }
        });

        Button addCategoryButton = new Button("+ Add Category");
        addCategoryButton.setMaxWidth(Double.MAX_VALUE);
        addCategoryButton.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Category");
            dialog.setHeaderText("Create a new category");
            dialog.setContentText("Category name:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(nameInput -> {
                String categoryName = nameInput.trim();
                if (categoryName.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Invalid category", "Category name cannot be empty.");
                    return;
                }

                boolean created = categoryDao.create(categoryName);
                if (!created) {
                    showAlert(Alert.AlertType.ERROR, "Duplicate category", "A category with that name already exists.");
                    return;
                }

                SidebarView.refreshSidebar(sidebar, categoryDao.findAll());
                sidebar.getItems().stream()
                        .filter(category -> category.getName().equals(categoryName))
                        .findFirst()
                        .ifPresent(category -> sidebar.getSelectionModel().select(category));
            });
        });

        VBox leftPane = new VBox(12, titleLabel, sidebar, addCategoryButton);
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

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
