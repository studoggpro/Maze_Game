package com.stukeenan.Maze_Game.model;

import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;

/**
 * Created by Stu on 3/11/2017.
 */
public class Player implements Serializable {

    private final SimpleStringProperty name;
    private final SimpleStringProperty time;
    private final SimpleStringProperty mazeLength;

    public Player(String name, String time, String mazeLength) {
        this.name = new SimpleStringProperty(name);
        this.time = new SimpleStringProperty(time);
        this.mazeLength = new SimpleStringProperty(mazeLength);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getTime() {
        return time.get();
    }

    public void setTime(String time) {
        this.time.set(time);
    }

    public String getMazeLength() {
        return mazeLength.get();
    }

    public void setMazeLength(String mazeLength) {
        this.mazeLength.set(mazeLength);
    }
}
