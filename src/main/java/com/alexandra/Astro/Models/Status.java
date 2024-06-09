package com.alexandra.Astro.Models;

public enum Status {
    Confirmed("Подтверждёно"),
    Unconfirmed("Не подтверждёно");

    private final String title;

    Status(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
