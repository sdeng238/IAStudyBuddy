package com.example.iastudybuddy;

public class Subject {
    private String colour;
    private String name;
    private int[] todayFocusTime; //merged h, min, s into an array
    private String uid;

    public Subject(String colour, String name, int[] todayFocusTime, String uid) {
        this.colour = colour;
        this.name = name;
        this.todayFocusTime = todayFocusTime;
        this.uid = uid;
    }
}
