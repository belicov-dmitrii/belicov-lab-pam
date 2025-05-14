package com.example.belicov_lab_pam;

public class Event {
    private String title;
    private String description;
    private long datetime;

    public Event(String title, String description, long datetime) {
        this.title = title;
        this.description = description;
        this.datetime = datetime;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getDatetime() {
        return datetime;
    }
}
