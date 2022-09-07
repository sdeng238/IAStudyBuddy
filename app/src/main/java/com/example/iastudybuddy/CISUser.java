package com.example.iastudybuddy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * This class specifies the instance variables of the CISUser class, and also includes getters and setters
 * for the instance variables to access and modify them.
 *
 * @author Shirley Deng
 * @version 0.1
 */
//changed name of class from User to com.example.iastudybuddy.CISUser
public class CISUser implements Serializable {
    private String email;
    private ArrayList<String> friendsUID;
    private int personalBestMinutes;
    private String rank;
    private ArrayList<String> requestsUID;
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
        this.email = email;
        friendsUID = new ArrayList<>();
        personalBestMinutes = 0;
        rank = "bronze";
        requestsUID = new ArrayList<>();
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public ArrayList<String> getFriendsUID() {
        return friendsUID;
    }

    public void setFriendsUID(ArrayList<String> friendsUID) {
        this.friendsUID = friendsUID;
    }

    public ArrayList<String> getRequestsUID() {
        return requestsUID;
    }

    public void setRequestsUID(ArrayList<String> requestsUID) {
        this.requestsUID = requestsUID;
    }
}
