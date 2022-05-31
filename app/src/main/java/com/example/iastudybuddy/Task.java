package com.example.iastudybuddy;

import java.sql.Timestamp;

public class Task {
    private boolean complete;
    private Timestamp creationDay;
    private String name;
    private String subject;
    private String uid;

    public Task(boolean complete, Timestamp creationDay, String name, String subject, String uid) {
        this.complete = complete;
        this.creationDay = creationDay;
        this.name = name;
        this.subject = subject;
        this.uid = uid;
    }
}
