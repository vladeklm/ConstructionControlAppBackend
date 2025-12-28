package com.example.constructioncontrol.model;

public enum MaterialType {
    WOOD("Клеёный брус"),
    GAS_CONCRETE("Газобетон"),
    FRAME("Каркас"),
    BRICK("Кирпич"),
    SIP("СИП-панели");

    private final String displayName;

    MaterialType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
