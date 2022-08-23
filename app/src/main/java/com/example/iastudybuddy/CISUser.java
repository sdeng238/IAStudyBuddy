package com.example.iastudybuddy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

//changed name of class from User to com.example.iastudybuddy.CISUser
public class CISUser implements Serializable {
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
        crownsNumber = 0;
        this.email = email;
        friendsUsernames = new ArrayList<>();
        personalBestMinutes = 0;
        rank = "bronze";
        requestUsernames = new ArrayList<>();
        subjects = new ArrayList<>();
        tasks = new ArrayList<>();
        todayTasksCompleted = 0;
        this.todayTotalFocusTime = new ArrayList<>();
        todayTotalFocusTime.add(0);
        todayTotalFocusTime.add(0);
        todayTotalFocusTime.add(0);
        totalFocusMinutes = 0;
        uid = UUID.randomUUID().toString();
        this.username = username;
    }

    public int getCrownsNumber() {
        return crownsNumber;
    }

    public void setCrownsNumber(int crownsNumber) {
        this.crownsNumber = crownsNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getFriendsUsernames() {
        return friendsUsernames;
    }

    public void setFriendsUsernames(ArrayList<String> friendsUsernames) {
        this.friendsUsernames = friendsUsernames;
    }

    public int getPersonalBestMinutes() {
        return personalBestMinutes;
    }

    public void setPersonalBestMinutes(int personalBestMinutes) {
        this.personalBestMinutes = personalBestMinutes;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public ArrayList<String> getRequestUsernames() {
        return requestUsernames;
    }

    public void setRequestUsernames(ArrayList<String> requestUsernames) {
        this.requestUsernames = requestUsernames;
    }

    public ArrayList<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(ArrayList<String> subjects) {
        this.subjects = subjects;
    }

    public ArrayList<String> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<String> tasks) {
        this.tasks = tasks;
    }

    public int getTodayTasksCompleted() {
        return todayTasksCompleted;
    }

    public void setTodayTasksCompleted(int todayTasksCompleted) {
        this.todayTasksCompleted = todayTasksCompleted;
    }

    public ArrayList<Integer> getTodayTotalFocusTime() {
        return todayTotalFocusTime;
    }

    public void setTodayTotalFocusTime(ArrayList<Integer> todayTotalFocusTime) {
        this.todayTotalFocusTime = todayTotalFocusTime;
    }

    public int getTotalFocusMinutes() {
        return totalFocusMinutes;
    }

    public void setTotalFocusMinutes(int totalFocusMinutes) {
        this.totalFocusMinutes = totalFocusMinutes;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
