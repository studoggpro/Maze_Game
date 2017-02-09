/*package com.stukeenan.gametest2.model;

import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

import java.util.*;
import java.util.stream.Collectors;

public class CreatePath {
    public static Node exitDoor;
    public static Map<Node, Node> mazeMap = new HashMap<>(64);
    private static Random rand = new Random();


    public static void CreatePath(){
        shuffleDoors();
        createExitDoor();
    }

    public static void createExitDoor() {
        int totalDoors = Door.doorMap.size();
        int exitDoorIndex = rand.nextInt(totalDoors);
        exitDoor = Door.doorMap.entrySet().stream().map(Map.Entry::getValue)
                .collect(Collectors.toList()).get(exitDoorIndex);
    }

    public static void shuffleDoors(){
        Collections.shuffle(Door.bottomDoorList);
        Collections.shuffle(Door.leftDoorList);
        Collections.shuffle(Door.rightDoorList);
        Collections.shuffle(Door.topDoorList);
    }

    private Map<Node, Node> createMazeMap(){
        Node key = new Rectangle();
        Node value = new Rectangle();
        int startDoor = rand.nextInt(4);
        LinkedList<Node> list = new LinkedList<>();
        switch (startDoor){
            case 0: list = Door.topDoorList;
                break;
            case 1: list = Door.rightDoorList;
                break;
            case 2: list = Door.bottomDoorList;
                break;
            case 3: list = Door.leftDoorList;
                break;
            default: if (!list.isEmpty()){
                list.clear();
            }
                break;
        }
        key = list.stream().filter(e -> e.getId().contains("room1")).findAny().orElse(null);
        if (key.getId().contains("Top")){

        }
        if (key.getId().contains("Right")){

        }
        if (key.getId().contains("Bottom")){

        }
        if (key.getId().contains("Left")){

        }

        return mazeMap;
    }
}*/
