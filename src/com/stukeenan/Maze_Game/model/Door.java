package com.stukeenan.Maze_Game.model;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Stu on 10/16/2016.
 */
public class Door {
    public static LinkedList<Node> bottomDoorList = new LinkedList<>();
    public static LinkedList<Node> leftDoorList = new LinkedList<>();
    public static LinkedList<Node> rightDoorList = new LinkedList<>();
    public static LinkedList<Node> topDoorList = new LinkedList<>();
    public static Map<String, Node> doorMap = new HashMap<>(64);

    public void createDoor(GridPane room, double dX, double dY){
        int hdX = ((int)dX + 2) / 2;
        int hdY = ((int)dY + 2) / 2;
        for (Location side : Location.values()) {
            Rectangle door = new Rectangle(65, 65);
            door.getStyleClass().add("door");
            door.setId(String.valueOf(side) + "Door" + room.getId());
            switch (side){
                case TOP:
                    room.add(door, hdX, 0);
                    GridPane.setHalignment(door, HPos.CENTER);
                    GridPane.setValignment(door, VPos.TOP);
                    topDoorList.add(door);
                    break;
                case BOTTOM:
                    room.add(door, hdX, (int)dY + 1);
                    GridPane.setHalignment(door, HPos.CENTER);
                    GridPane.setValignment(door, VPos.BOTTOM);
                    bottomDoorList.add(door);
                    break;
                case LEFT:
                    room.add(door, 0, hdY);
                    GridPane.setHalignment(door, HPos.LEFT);
                    leftDoorList.add(door);
                    break;
                case RIGHT:
                    room.add(door, (int)dX + 1, hdY);
                    GridPane.setHalignment(door, HPos.RIGHT);
                    rightDoorList.add(door);
                    break;
                default:
                    System.out.println("invalid door id");
                    break;
            }
            doorMap.put(door.getId(), door);
        }
    }

    public Door(){
    }
}
