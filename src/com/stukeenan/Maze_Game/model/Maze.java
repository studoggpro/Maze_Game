package com.stukeenan.Maze_Game.model;

import javafx.scene.Node;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Maze {
    private int count = 1;
    private int oppositeDoor = 0;
    private int pathLength = 2;
    public int numberOfRooms = Room.mazeLength;
    private int[] key = new int[2];
    private int[] value = new int[2];
    private LinkedList<Node> doorList = new LinkedList<>();
    private LinkedList<Integer> remainingDoors = new LinkedList<>();
    private LinkedList<Integer> remainingBottomDoorList = new LinkedList<>();
    private LinkedList<Integer> remainingLeftDoorList = new LinkedList<>();
    private LinkedList<Integer> remainingRightDoorList = new LinkedList<>();
    private LinkedList<Integer> remainingTopDoorList = new LinkedList<>();
    private List<Integer> currentRoomRemainingDoors = new ArrayList<>(4);
    private Random rand = new Random();
    public String[][] mazeArray = new String[Room.mazeLength][4];
    public static Map<Node, Node> mazeMap = new HashMap<>(64);
    public static Node exitDoor;

    /**mazeArray x = room#
     * y=Door:
     * Top Door = 0
     * Right Door = 1
     * Bottom Door = 2
     * Left Door = 3**/

    public Maze(){}


    public void createMaze(){
        createPath();
        fillRemainingDoorLists();
        fillRemainingMazeArrayNulls(remainingTopDoorList, 0,
                                    remainingBottomDoorList, 2);
        fillRemainingMazeArrayNulls(remainingRightDoorList, 1,
                                    remainingLeftDoorList, 3);

        assignRemainingDoors(Door.topDoorList, Door.bottomDoorList);
        assignRemainingDoors(Door.rightDoorList, Door.leftDoorList);
    }



    private void assignRemainingDoors(LinkedList<Node> keyList, LinkedList<Node> valueList){
        Collections.shuffle(keyList);
        Collections.shuffle(valueList);
        Node remainingKey;
        Node remainingValue;
        int maxSize = Math.max(keyList.size(), valueList.size());
        for (int i = 0; i < maxSize; i++) {
            if (keyList.isEmpty()){
                remainingKey = null;
            } else {
                remainingKey = keyList.poll();
            }
            if (valueList.isEmpty()){
                remainingValue = null;
            } else {
                remainingValue = valueList.poll();
            }
            mazeMap.put(remainingKey, remainingValue);
        }
    }

    private void createPath(){
        for (int i = 0; i <= pathLength ; i++) {
            if (i == 0){
                key[0] = 0;
                key[1] = rand.nextInt(4);
                fillMazeArray(key[0], key[1], "key");
                setValue();
                mazeMap.put(fillMazeMap(key), fillMazeMap(value));
            }
            if (i > 0 && i < pathLength){
                setKey();
                setValue();
                mazeMap.put(fillMazeMap(key), fillMazeMap(value));
            }
            if (i == pathLength){
                exitDoor();
            }
            count++;
        }
    }

    private List<Integer> doorCountByRoom(int room){
        return IntStream.range(0, 4)
                .filter(i -> Arrays.asList(mazeArray[room]).get(i) == null)
                .boxed()
                .collect(Collectors.toList());
    }

    private void doorCountBySide(int side){
        if (!remainingDoors.isEmpty()) {
            remainingDoors.clear();
        }
        for (int i = 0; i < numberOfRooms; i++) {
            if (mazeArray[i][side] == null){
                remainingDoors.add(i);
            }
        }
    }

    private void exitDoor(){
        key[0] = value[0];
        currentRoomRemainingDoors = doorCountByRoom(value[0]);
        key[1] = currentRoomRemainingDoors.get(rand.nextInt(currentRoomRemainingDoors.size()));
        fillMazeArray(key[0], key[1], "exit");
        exitDoor = fillMazeMap(key);
    }

    private void fillMazeArray(int x, int y, String kV){
        if (kV.equals("key")){
            mazeArray[x][y] = count + "a";
        }
        if (kV.equals("value")){
            mazeArray[x][y] = count + "b";
        }
        if (kV.equals("exit")){
            mazeArray[x][y] = count + "e";
        }
    }

    private Node fillMazeMap(int[] xy){
        switch (xy[1]) {
            case 0:
                doorList = Door.topDoorList;
                break;
            case 1:
                doorList = Door.rightDoorList;
                break;
            case 2:
                doorList = Door.bottomDoorList;
                break;
            case 3:
                doorList = Door.leftDoorList;
                break;
            default:
                System.out.println("fillMazeMap default");
                break;
        }
        Node node = doorList.stream().filter(n -> n.getId().toLowerCase().matches(String.format("[a-z]+%d", xy[0])))
                .findAny()
                .orElse(null);
        doorList.remove(node);
        return node;
    }

    private void fillRemainingDoorLists() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < numberOfRooms; j++) {
                if (mazeArray[j][i] == null){
                    switch (i){
                        case 0: remainingTopDoorList.add(j);
                            break;
                        case 1: remainingRightDoorList.add(j);
                            break;
                        case 2: remainingBottomDoorList.add(j);
                            break;
                        case 3: remainingLeftDoorList.add(j);
                            break;
                        default:
                            System.out.println("fill remaining doors default???");
                            break;
                    }
                }
            }
        }
    }

    private void fillRemainingMazeArrayNulls(LinkedList<Integer> keyList, int keySide,
                                             LinkedList<Integer> valueList, int valueSide){
        Collections.shuffle(keyList);
        Collections.shuffle(valueList);
        int remainingKey;
        int remainingValue;
        int minSize = Math.min(keyList.size(), valueList.size());
        for (int i = 0; i < minSize; i++) {
            remainingKey = keyList.poll();
            fillMazeArray(remainingKey, keySide, "key");
            remainingValue = valueList.poll();
            fillMazeArray(remainingValue, valueSide, "value");
            count++;
        }
    }

    private void lookAhead(){
        for (int doorId : currentRoomRemainingDoors) {
            setOppositeDoorSwitch(doorId);
            int fullRoomCount = 0;
            List<Integer> futureDoors;
            for (Iterator<Integer> it = remainingDoors.iterator(); it.hasNext();) {
                Integer integer = it.next();
                futureDoors = doorCountByRoom(integer);
                if (futureDoors.size() == 1){
                    fullRoomCount++;
                }
                if (fullRoomCount == remainingDoors.size()){
                    it.remove();
                }
            }
        }
    }

    private void setKey(){
        key[0] = value [0];
        currentRoomRemainingDoors = doorCountByRoom(value[0]);
        if (currentRoomRemainingDoors.size() == 1){
            key[1] = currentRoomRemainingDoors.get(0);
        } else {
            lookAhead();
            key[1] = currentRoomRemainingDoors.get(rand.nextInt(currentRoomRemainingDoors.size()));
        }
        fillMazeArray(key[0], key[1], "key");
    }

    private void setOppositeDoorSwitch(int startDoor){
        switch (startDoor){
            case 0: oppositeDoor = 2;
                break;
            case 1: oppositeDoor = 3;
                break;
            case 2: oppositeDoor = 0;
                break;
            case 3: oppositeDoor = 1;
                break;
            default:
                System.out.println("Why am I here? oppositeDoor Default...");
                break;
        }
        doorCountBySide(oppositeDoor);
    }

    private void setValue(){
        setOppositeDoorSwitch(key[1]);
        if (remainingDoors.size() == 1){
            value[0] = remainingDoors.get(0);
        } else {
            for (Iterator<Integer> it = remainingDoors.iterator(); it.hasNext();) {
                Integer integer = it.next();
                List<Integer> doors = doorCountByRoom(integer);
                if (doors.size() == 1){
                    it.remove();
                }
            }
            value[0] = remainingDoors.get(rand.nextInt(remainingDoors.size()));
        }
        value[1] = oppositeDoor;
        fillMazeArray(value[0], value[1], "value");
    }
}
