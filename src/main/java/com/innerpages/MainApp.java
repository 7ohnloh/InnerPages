package com.innerpages;

import com.innerpages.controller.NavigationController;
import com.innerpages.dao.CategoryDao;
import com.innerpages.dao.CommentDao;
import com.innerpages.dao.DatabaseManager;
import com.innerpages.dao.WeeklyEntryDao;
import com.innerpages.model.Category;
import com.innerpages.model.Comment;
import com.innerpages.model.WeeklyEntry;
import com.innerpages.view.SidebarView;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public class MainApp extends Application {
    private final CategoryDao categoryDao = new CategoryDao();
    private final WeeklyEntryDao weeklyEntryDao = new WeeklyEntryDao();
    private final CommentDao commentDao = new CommentDao();

    private BorderPane root;
    private ListView<Category> sidebar;
    private Category selectedCategory;
    private WeeklyEntry selectedEntry;

    @Override
    public void start(Stage stage) {
        DatabaseManager.initializeDatabase();

        Label titleLabel = new Label("InnerPages");
        titleLabel.getStyleClass().add("app-title");

        sidebar = SidebarView.createSidebar(categoryDao.findAll());
        configureCategoryContextMenu();
        sidebar.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                selectCategory(newValue);
            }
        });

        Button addCategoryButton = new Button("+ Add Category");
        addCategoryButton.getStyleClass().addAll("button", "button-primary");
        addCategoryButton.setMaxWidth(Double.MAX_VALUE);
        addCategoryButton.setOnAction(event -> addCategory());

        VBox leftPane = new VBox(12, titleLabel, sidebar, addCategoryButton);
        leftPane.getStyleClass().add("sidebar-pane");
        VBox.setVgrow(sidebar, Priority.ALWAYS);

        root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.setLeft(leftPane);
        root.setCenter(createWelcomeView());

        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(getClass().getResource("/innerpages.css").toExternalForm());
        stage.setTitle("InnerPages");
        stage.setScene(scene);
        stage.show();
    }

    private void configureCategoryContextMenu() {
        sidebar.setCellFactory(listView -> {
            ListCell<Category> cell = new ListCell<>() {
                @Override
                protected void updateItem(Category category, boolean empty) {
                    super.updateItem(category, empty);
                    setText(empty || category == null ? null : category.getName());
                    setContextMenu(empty || category == null ? null : createCategoryMenu(this));
                    getStyleClass().remove("category-cell");
                    if (!empty && category != null) {
                        getStyleClass().add("category-cell");
                    }
                }
            };
            return cell;
        });
    }

    private ContextMenu createCategoryMenu(ListCell<Category> cell) {
        MenuItem renameItem = new MenuItem("Rename Category");
        renameItem.setOnAction(event -> {
            Category category = cell.getItem();
            if (category != null) {
                sidebar.getSelectionModel().select(category);
                renameCategory(category);
            }
        });

        MenuItem deleteItem = new MenuItem("Delete Category");
        deleteItem.setOnAction(event -> {
            Category category = cell.getItem();
            if (category != null) {
                sidebar.getSelectionModel().select(category);
            }
            deleteSelectedCategory();
        });

        return new ContextMenu(renameItem, deleteItem);
    }

    private Node createWelcomeView() {
        Label contentLabel = new Label("Welcome to InnerPages");
        contentLabel.getStyleClass().add("welcome-title");
        Label helperLabel = new Label("Choose a category to view journal entries and notes.");
        helperLabel.getStyleClass().add("muted-label");
        VBox view = new VBox(8, contentLabel, helperLabel);
        view.getStyleClass().add("content-pane");
        return view;
    }

    private void selectCategory(Category category) {
        selectedCategory = category;
        selectedEntry = null;
        new NavigationController().navigateTo(category.getName());
        renderCategoryView(category);
    }

    private void renderCategoryView(Category category) {
        Label heading = new Label(category.getName());
        heading.getStyleClass().add("page-heading");

        Button addEntryButton = new Button("+ Add Weekly Entry");
        addEntryButton.getStyleClass().addAll("button", "button-primary");
        addEntryButton.setOnAction(event -> addWeeklyEntry(category));

        HBox header = new HBox(12, heading, addEntryButton);
        header.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(heading, Priority.ALWAYS);

        VBox entryList = new VBox(10);
        entryList.getStyleClass().add("entry-list");
        for (WeeklyEntry entry : weeklyEntryDao.findByCategoryId(category.getId())) {
            entryList.getChildren().add(createWeeklyEntryCard(entry));
        }
        if (entryList.getChildren().isEmpty()) {
            Label emptyLabel = new Label("No weekly entries yet.");
            emptyLabel.getStyleClass().add("empty-label");
            entryList.getChildren().add(emptyLabel);
        }

        VBox content = new VBox(18, header, entryList);
        content.getStyleClass().add("content-pane");

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.getStyleClass().add("content-scroll");
        scrollPane.setFitToWidth(true);
        root.setCenter(scrollPane);
    }

    private Node createWeeklyEntryCard(WeeklyEntry entry) {
        Label title = new Label(entry.getTitle());
        title.getStyleClass().add("card-title");

        Label createdAt = new Label("Created: " + entry.getCreatedAt());
        createdAt.getStyleClass().add("meta-label");
        Label updatedAt = new Label("Last Updated: " + entry.getUpdatedAt());
        updatedAt.getStyleClass().add("meta-label");

        Button openButton = new Button("> Open");
        openButton.getStyleClass().addAll("button", "button-secondary");
        openButton.setOnAction(event -> openWeeklyEntry(entry));

        Button renameButton = new Button("Rename");
        renameButton.getStyleClass().addAll("button", "button-secondary");
        renameButton.setOnAction(event -> renameWeeklyEntry(entry));

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().addAll("button", "button-danger");
        deleteButton.setOnAction(event -> deleteWeeklyEntry(entry));

        HBox actions = new HBox(8, openButton, renameButton, deleteButton);
        actions.setAlignment(Pos.CENTER_LEFT);
        VBox timestamps = new VBox(3, createdAt, updatedAt);
        VBox card = new VBox(10, title, timestamps, actions);
        card.getStyleClass().add("entry-card");
        return card;
    }

    private void openWeeklyEntry(WeeklyEntry entry) {
        WeeklyEntry currentEntry = weeklyEntryDao.findById(entry.getId());
        if (currentEntry == null) {
            showAlert(Alert.AlertType.ERROR, "Missing entry", "This weekly entry no longer exists.");
            renderSelectedCategory();
            return;
        }

        selectedEntry = currentEntry;
        renderEntryDetailView(currentEntry);
    }

    private void renderEntryDetailView(WeeklyEntry entry) {
        Label heading = new Label(entry.getTitle());
        heading.getStyleClass().add("page-heading");

        Button backButton = new Button("< Back");
        backButton.getStyleClass().addAll("button", "button-secondary");
        backButton.setOnAction(event -> renderSelectedCategory());

        Button summariseButton = new Button("Summarise Comments");
        summariseButton.getStyleClass().addAll("button", "button-secondary");
        summariseButton.setDisable(true);

        HBox topActions = new HBox(8, backButton, summariseButton);
        topActions.setAlignment(Pos.CENTER_LEFT);

        VBox commentsList = new VBox(10);
        commentsList.getStyleClass().add("entry-list");
        for (Comment comment : commentDao.findByWeeklyEntryId(entry.getId())) {
            commentsList.getChildren().add(createCommentCard(comment));
        }
        if (commentsList.getChildren().isEmpty()) {
            Label emptyLabel = new Label("No notes yet.");
            emptyLabel.getStyleClass().add("empty-label");
            commentsList.getChildren().add(emptyLabel);
        }

        TextArea newCommentArea = new TextArea();
        newCommentArea.getStyleClass().add("note-input");
        newCommentArea.setPromptText("Write a note...");
        newCommentArea.setPrefRowCount(4);

        Button addNoteButton = new Button("+ Add Note");
        addNoteButton.getStyleClass().addAll("button", "button-primary");
        addNoteButton.setOnAction(event -> {
            String content = newCommentArea.getText().trim();
            if (content.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Invalid note", "Note content cannot be empty.");
                return;
            }
            commentDao.create(entry.getId(), content);
            renderEntryDetailView(weeklyEntryDao.findById(entry.getId()));
        });

        VBox content = new VBox(14, heading, topActions, commentsList, newCommentArea, addNoteButton);
        content.getStyleClass().add("content-pane");

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.getStyleClass().add("content-scroll");
        scrollPane.setFitToWidth(true);
        root.setCenter(scrollPane);
    }

    private Node createCommentCard(Comment comment) {
        Label createdAt = new Label(comment.getCreatedAt() == null ? "" : comment.getCreatedAt());
        createdAt.getStyleClass().add("meta-label");

        Label content = new Label(comment.getContent());
        content.getStyleClass().add("note-content");
        content.setWrapText(true);

        Button editButton = new Button("Edit");
        editButton.getStyleClass().addAll("button", "button-secondary");
        editButton.setOnAction(event -> editComment(comment));

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().addAll("button", "button-danger");
        deleteButton.setOnAction(event -> deleteComment(comment));

        HBox actions = new HBox(8, editButton, deleteButton);
        actions.setAlignment(Pos.CENTER_LEFT);
        VBox card = new VBox(8, createdAt, content, actions);
        card.getStyleClass().add("note-card");
        return card;
    }

    private void addCategory() {
        Optional<String> result = showTextInput("Add Category", "Create a new category", "Category name:", "");
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

            refreshSidebar(categoryName);
        });
    }

    private void renameCategory(Category category) {
        Optional<String> result = showTextInput("Rename Category", "Rename category", "Category name:", category.getName());
        result.ifPresent(nameInput -> {
            String categoryName = nameInput.trim();
            if (categoryName.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Invalid category", "Category name cannot be empty.");
                return;
            }

            boolean updated = categoryDao.update(category.getId(), categoryName);
            if (!updated) {
                showAlert(Alert.AlertType.ERROR, "Duplicate category", "A category with that name already exists.");
                return;
            }

            refreshSidebar(categoryName);
        });
    }

    private void deleteSelectedCategory() {
        Category category = sidebar.getSelectionModel().getSelectedItem();
        if (category == null) {
            showAlert(Alert.AlertType.ERROR, "No category selected", "Select a category before deleting.");
            return;
        }

        Optional<ButtonType> result = showConfirmation(
                "Delete Category",
                "Delete " + category.getName() + "?",
                "This will also delete its weekly entries and notes."
        );
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        categoryDao.delete(category.getId());
        selectedCategory = null;
        selectedEntry = null;
        SidebarView.refreshSidebar(sidebar, categoryDao.findAll());
        root.setCenter(createWelcomeView());
    }

    private void addWeeklyEntry(Category category) {
        Optional<String> titleResult = showTextInput("Add Weekly Entry", "Create a weekly entry", "Title:", "");
        if (titleResult.isEmpty()) {
            return;
        }

        String title = titleResult.get().trim();
        if (title.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Invalid entry", "Weekly entry title cannot be empty.");
            return;
        }

        weeklyEntryDao.create(category.getId(), title);
        renderCategoryView(category);
    }

    private void renameWeeklyEntry(WeeklyEntry entry) {
        Optional<String> result = showTextInput("Rename Weekly Entry", "Rename weekly entry", "Title:", entry.getTitle());
        result.ifPresent(titleInput -> {
            String title = titleInput.trim();
            if (title.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Invalid entry", "Weekly entry title cannot be empty.");
                return;
            }
            weeklyEntryDao.rename(entry.getId(), title);
            renderSelectedCategory();
        });
    }

    private void deleteWeeklyEntry(WeeklyEntry entry) {
        Optional<ButtonType> result = showConfirmation(
                "Delete Weekly Entry",
                "Delete " + entry.getTitle() + "?",
                "This will also delete its notes."
        );
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        weeklyEntryDao.delete(entry.getId());
        selectedEntry = null;
        renderSelectedCategory();
    }

    private void editComment(Comment comment) {
        Optional<String> result = showMultilineInput("Edit Note", "Edit note", comment.getContent());
        result.ifPresent(contentInput -> {
            String content = contentInput.trim();
            if (content.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Invalid note", "Note content cannot be empty.");
                return;
            }
            commentDao.update(comment.getId(), content);
            renderSelectedEntry();
        });
    }

    private void deleteComment(Comment comment) {
        Optional<ButtonType> result = showConfirmation("Delete Note", "Delete this note?", "This cannot be undone.");
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        commentDao.delete(comment.getId());
        renderSelectedEntry();
    }

    private void refreshSidebar(String selectCategoryName) {
        SidebarView.refreshSidebar(sidebar, categoryDao.findAll());
        sidebar.getItems().stream()
                .filter(category -> category.getName().equals(selectCategoryName))
                .findFirst()
                .ifPresent(category -> sidebar.getSelectionModel().select(category));
    }

    private void renderSelectedCategory() {
        if (selectedCategory == null) {
            root.setCenter(createWelcomeView());
            return;
        }

        sidebar.getItems().stream()
                .filter(category -> category.getId() == selectedCategory.getId())
                .findFirst()
                .ifPresentOrElse(this::selectCategory, () -> root.setCenter(createWelcomeView()));
    }

    private void renderSelectedEntry() {
        if (selectedEntry == null) {
            renderSelectedCategory();
            return;
        }

        WeeklyEntry currentEntry = weeklyEntryDao.findById(selectedEntry.getId());
        if (currentEntry == null) {
            renderSelectedCategory();
            return;
        }

        selectedEntry = currentEntry;
        renderEntryDetailView(currentEntry);
    }

    private Optional<String> showTextInput(String title, String header, String content, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        return dialog.showAndWait();
    }

    private Optional<String> showMultilineInput(String title, String header, String defaultValue) {
        TextArea textArea = new TextArea(defaultValue);
        textArea.setPrefRowCount(6);

        javafx.scene.control.Dialog<String> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.getDialogPane().setContent(textArea);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(buttonType -> buttonType == ButtonType.OK ? textArea.getText() : null);
        return dialog.showAndWait();
    }

    private Optional<ButtonType> showConfirmation(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        return alert.showAndWait();
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
