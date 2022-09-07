package com.example.iastudybuddy;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

/**
 * This class specifies the instance variables of the CISTask class, and also includes getters and setters
 * for the instance variables to access and modify them.
 *
 * @author Shirley Deng
 * @version 0.1
 */
public class CISTask {
    private boolean complete;
    private Date creationDay;
    private String name;
    private String subject;
    private String uid;
    private String ownerEmail;

    public CISTask() {
    }

    public CISTask(String name, String subject, String ownerEmail) {
        this.complete = false;
        //Firebase will convert Java Date object into Firebase Timestamp field
        this.creationDay = new Date();
        this.name = name;
        this.subject = subject;
        this.uid = UUID.randomUUID().toString();
        this.ownerEmail = ownerEmail;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public Date getCreationDay() {
        return creationDay;
    }

    public void setCreationDay(Date creationDay) {
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

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }
}
