package com.example.iastudybuddy;

import java.util.ArrayList;
import java.util.UUID;

/**
 * This class specifies the instance variables of the Subject class, and also includes getters and setters
 * for the instance variables to access and modify them.
 *
 * @author Shirley Deng
 * @version 0.1
 */
public class Subject {
    private String colour;
    private String name;
    private String uid;
    private String ownerEmail; //new added!!!!

    public Subject() {
    }

    public Subject(String colour, String name, String ownerEmail) {
        this.colour = colour;
        this.name = name;
        this.uid = UUID.randomUUID().toString();
        this.ownerEmail = ownerEmail;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
