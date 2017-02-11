package com.stukeenan.gametest2.model;

import javafx.scene.Node;

import java.util.*;

public class CreatePath {
    private static Node currentKey;
    public static Node exitDoor;
    private static Node value;
    private static int mazeLength = 3;
    public static Map<Node, Node> mazeMap = new HashMap<>(64);
    private static Random rand = new Random();
    private static String room;


    public static void CreatePath(){
        shuffleDoors();
        createMazeMap();
    }


    public static void shuffleDoors(){
        Collections.shuffle(Door.bottomDoorList);
        Collections.shuffle(Door.leftDoorList);
        Collections.shuffle(Door.rightDoorList);
        Collections.shuffle(Door.topDoorList);
    }

    private static Map<Node, Node> createMazeMap(){
        for (int i = 0; i <= mazeLength; i++) {
            if (i == 0){
                room = "room1";
                currentKey = key();
                value = value();
            }
            if (i > 0 && i < mazeLength){
                room = value.getParent().getId();
                currentKey = key();
                value = value();
            }
            if (i == mazeLength){
                room = value.getParent().getId();
                exitDoor = key();
                break;
            }
            mazeMap.put(currentKey, value);
        }

        return mazeMap;
    }

    private static Node key(){
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
        Node key = list.stream().filter(e -> e.getId().contains(room)).findAny().orElse(null);
        list.remove(key);
        return key;
    }

    private static Node value(){
        if (currentKey.getId().contains(String.valueOf(Location.TOP))){
            value = Door.bottomDoorList.poll();
        }
        if (currentKey.getId().contains(String.valueOf(Location.RIGHT))){
            value = Door.leftDoorList.poll();
        }
        if (currentKey.getId().contains(String.valueOf(Location.BOTTOM))){
            value = Door.topDoorList.poll();
        }
        if (currentKey.getId().contains(String.valueOf(Location.LEFT))){
            value = Door.rightDoorList.poll();
        }
        return value;
    }
}
