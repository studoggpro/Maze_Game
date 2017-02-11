package com.stukeenan.gametest2.model;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Stu on 10/16/2016.
 */
public class Door {
    private ArrayList<String> parentIDList = new ArrayList<>();
    public static LinkedList<Node> bottomDoorList = new LinkedList<>();
    public static LinkedList<Node> leftDoorList = new LinkedList<>();
    public static LinkedList<Node> rightDoorList = new LinkedList<>();
    public static LinkedList<Node> topDoorList = new LinkedList<>();
    private boolean closedLoopCheckOneDone = false;
    public static Map<String, Node> doorMap = new HashMap<>(64);
    public static Map<Node, Node> mazeMap = new HashMap<>(64);
    public static Node exitDoor;

    public void createDoor(GridPane room, double dX, double dY){
        int hdX = ((int)dX + 2) / 2;
        int hdY = ((int)dY + 2) / 2;
        for (Location side : Location.values()) {
            Rectangle door = new Rectangle(65, 65, Paint.valueOf("#074f57"));
            door.setId(String.valueOf(side) + "Door" + room.getId());
            switch (side){
                case TOP:
                    room.add(door, hdX, 0);
                    room.setHalignment(door, HPos.CENTER);
                    room.setValignment(door, VPos.TOP);
                    topDoorList.add(door);
                    break;
                case BOTTOM:
                    room.add(door, hdX, (int)dY + 1);
                    room.setHalignment(door, HPos.CENTER);
                    room.setValignment(door, VPos.BOTTOM);
                    bottomDoorList.add(door);
                    break;
                case LEFT:
                    room.add(door, 0, hdY);
                    room.setHalignment(door, HPos.LEFT);
                    leftDoorList.add(door);
                    break;
                case RIGHT:
                    room.add(door, (int)dX + 1, hdY);
                    room.setHalignment(door, HPos.RIGHT);
                    rightDoorList.add(door);
                    break;
                default:
                    System.out.println("invalid door id");
                    break;
            }
            doorMap.put(door.getId(), door);
        }
    }

    public void createExitDoor() {
        Random rand = new Random();
        int totalDoors = doorMap.size();
        int exitDoorIndex = rand.nextInt(totalDoors);
        exitDoor = doorMap.entrySet().stream().map(Map.Entry::getValue)
                .collect(Collectors.toList()).get(exitDoorIndex);
    }

    public Door(){
    }

    private void checkForClosedLoops(LinkedList<Node> list1, LinkedList<Node> list2) {
        for (int i = 0; i < list1.size(); i++) {
            String parent1 = list1.get(i).getParent().getId();
            String parent2 = list2.get(i).getParent().getId();
            if (!closedLoopCheckOneDone) {
                if (parent1.equals(parent2)) {
                    if (!parentIDList.contains(parent1)) {
                        parentIDList.add(parent1);
                    } else {
                        System.out.println("closed loop found");
                        Collections.shuffle(list2);
                        checkForClosedLoops(list1, list2);
                    }
                }
            } else {
                if (parentIDList.size() < list1.size()) {
                    parentIDList.add(parent1.concat(parent2));
                } else if (parentIDList.size() == list1.size()){
                    for (String s : parentIDList){
                        if (parent1.equals(parent2)){
                            String testString = parent1.concat(parent2);
                            if (s.equals(testString)){
                                System.out.println("shuffled");
                                closedLoopCheckOneDone = false;
                                shuffleDoors();
                                break;
                            }
                        } else if (s.contains(parent1) && s.contains(parent2)){
                            System.out.println("shuffled");
                            closedLoopCheckOneDone = false;
                            shuffleDoors();
                            break;
                        }
                    }
                }
            }
        }
    }

    public void shuffleDoors() {
        Collections.shuffle(topDoorList);
        Collections.shuffle(bottomDoorList);
        Collections.shuffle(leftDoorList);
        Collections.shuffle(rightDoorList);
        checkForClosedLoops(topDoorList, bottomDoorList);
        checkForClosedLoops(leftDoorList, rightDoorList);
        closedLoopCheckOneDone = true;
        parentIDList.clear();
        checkForClosedLoops(topDoorList, leftDoorList);
        checkForClosedLoops(bottomDoorList, rightDoorList);
        if (!topDoorList.isEmpty() && !bottomDoorList.isEmpty() &&
                !leftDoorList.isEmpty() && !rightDoorList.isEmpty()) {
            for (int i = 0; i < topDoorList.size(); i++) {
                mazeMap.put(topDoorList.get(i), bottomDoorList.get(i));
                mazeMap.put(leftDoorList.get(i), rightDoorList.get(i));

            }
        }
    }
}
