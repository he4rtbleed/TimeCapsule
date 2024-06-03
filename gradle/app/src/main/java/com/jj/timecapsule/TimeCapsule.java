package com.jj.timecapsule;

public class TimeCapsule {
    private String title;
    private String openDate;

    public TimeCapsule(String title, String openDate) {
        this.title = title;
        this.openDate = openDate;
    }

    public String getTitle() {
        return title;
    }

    public String getOpenDate() {
        return openDate;
    }
}
