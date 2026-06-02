package com.innerpages.view;

import javafx.collections.FXCollections;
import javafx.scene.control.ListView;

public class SidebarView {
    public static ListView<String> createSidebar() {
        ListView<String> sidebar = new ListView<>(FXCollections.observableArrayList(
                "School",
                "Exchange",
                "Personal"
        ));
        sidebar.setPrefWidth(160);
        return sidebar;
    }
}
