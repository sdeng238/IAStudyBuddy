package com.example.iastudybuddy;

import java.util.ArrayList;
import java.util.UUID;

//changed name of class from User to com.example.iastudybuddy.CISUser
public class CISUser {
    private int crownsNumber;
    private String email;
    private ArrayList<String> friendsUsernames;
    private int personalBestMinutes;
    private String rank;
    private ArrayList<String> requestUsernames;
    private ArrayList<String> subjects;
    private ArrayList<String> tasks;
    private int todayTasksCompleted;
    private ArrayList<Integer> todayTotalFocusTime; //merged h, min, s into an ArrayList
    private int totalFocusMinutes; //deleted total focus seconds
    private String uid;
    private String username;

    public CISUser() {

    }

    public CISUser(String email, String username) {
        this.crownsNumber = 0;
        this.email = email;
        this.friendsUsernames = new ArrayList<>();
        this.personalBestMinutes = 0;
        this.rank = "bronze";
        this.requestUsernames = new ArrayList<>();
        this.subjects = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.todayTasksCompleted = 0;
        this.todayTotalFocusTime = new ArrayList<>();
        todayTotalFocusTime.add(0);
        todayTotalFocusTime.add(0);
        todayTotalFocusTime.add(0);
        this.totalFocusMinutes = 0;
        this.uid = UUID.randomUUID().toString();
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public ArrayList<String> getTasks() {
        return tasks;
    }

    public ArrayList<Integer> getTodayTotalFocusTime() {
        return todayTotalFocusTime;
    }
}
