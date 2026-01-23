package com.example.sakhi;

public class Feedback {
    private String message;
    private int rating;

    public Feedback(String message, int rating) {
        this.message = message;
        this.rating = rating;
    }

    public String getMessage() {
        return message;
    }

    public int getRating() {
        return rating;
    }
}
