package com.example.ourpro.bottomnav.finance;

public class HistoryItem {
    private final String title;
    private final String amount;

    public HistoryItem(String title, String amount) {
        this.title = title;
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public String getAmount() {
        return amount;
    }
}