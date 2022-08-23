package com.example.iastudybuddy;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

//CHANGE NAME FROM TASK TO CISTASK
public class CISTask {
    private boolean complete;
    private Timestamp creationDay;
    private String name;
    private String subject;
    private String uid;

    public CISTask() {
    }

    public CISTask(String name, String subject) {
        this.complete = false;
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        this.creationDay = ts;
        this.name = name;
        this.subject = subject;
        this.uid = UUID.randomUUID().toString();
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public Timestamp getCreationDay() {
        return creationDay;
    }

    public void setCreationDay(Timestamp creationDay) {
        this.creationDay = creationDay;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
