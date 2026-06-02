package com.innerpages.view;

import com.innerpages.model.Category;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;

import java.util.List;

public class SidebarView {
    public static ListView<Category> createSidebar(List<Category> categories) {
        ListView<Category> sidebar = new ListView<>(FXCollections.observableArrayList(categories));
        sidebar.getStyleClass().add("category-list");
        sidebar.setPrefWidth(210);
        return sidebar;
    }

    public static void refreshSidebar(ListView<Category> sidebar, List<Category> categories) {
        sidebar.setItems(FXCollections.observableArrayList(categories));
    }
}
