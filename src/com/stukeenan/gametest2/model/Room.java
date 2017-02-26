package com.stukeenan.gametest2.model;

import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by Stu on 9/6/2016.
 */
public class Room {
    private ArrayList boxCoord = new ArrayList();
    private long boxCount;
    private Door door = new Door();
    private double dX;
    private double dY;
    public static int mazeLength = 3; //NUMBER OF ROOMS
    private Random random = new Random();
    private List<Pane> roomList = new ArrayList<>();
    private double[] roomSize = new double[2];
    private double smWidth;
    private double smHeight;
    public Map<String, List<Node>> solidObjectMap = new HashMap<>();


    public Room(){
    }

    private void calculateRoomDividers() {
        dX = random.nextInt(5) + 1;
        dY = random.nextInt(5) + 1;
        boxCount = Math.round(((dX * dY) / 2) * Math.random());
        smWidth = (roomSize[0] - 130) / dX;
        smHeight = (roomSize[1] - 130) / dY;
    }

    private void generateWidth() {
        double x = 600 * Math.random();
        if (x < 250) {
            generateWidth();
        } else
            roomSize[0] = x;
    }

    private void generateHeight() {
        double y = 600 * Math.random();
        if (y < 250) {
            generateHeight();
        }else
            roomSize[1] = y;
    }

    private Point point(){
        Point point = new Point();
        int x = random.nextInt((int) dX) + 1;
        int y = random.nextInt((int) dY) + 1;
        point.setLocation(x, y);
        return point;
    }

    private void generateBoxPoints(){
        Point boxPoint = point();
        try {
            if (!boxCoord.contains(boxPoint)) {
                boxCoord.add(boxPoint);
                System.out.println("x,Y: " + boxPoint.getX() + "," + boxPoint.getY());
            } else {
                generateBoxPoints();
            }
        } catch (StackOverflowError e){
            System.out.printf("StackOverflowError: " + e.getMessage() +
                    "%ndx: " + dX + "%ndy: " + dY);
        }
    }

    private void createBoxes(GridPane room, double width, double height) {
        for (int l = 0; l < boxCount ; l++) {
            generateBoxPoints();
        }
        int count = 0;
        for (int i = 0; i < boxCount; i++) {
            count++;
            Rectangle box = new Rectangle(width, height);
            box.setId("box" + count);
            box.getStyleClass().add("box");
            room.add(box, ((Point)boxCoord.get(i)).x, ((Point)boxCoord.get(i)).y);
        }
    }

    public List<Pane> generateRooms(){
        int roomCount = 0;
        for (int i = 0; i < mazeLength ; i++) {
            List<Node> solidObjectList = new ArrayList<>();
            generateWidth();
            generateHeight();
            calculateRoomDividers();
            GridPane room = new GridPane();
//            room.setGridLinesVisible(true);
            setColumnConstraints(room);
            setRowRestraints(room);
            room.setPrefSize(roomSize[0], roomSize[1]);
            room.setMaxSize(600, 600);
            room.setId("room" + roomCount);
            room.getStyleClass().add("room");
            setRoomMovable(room);
            createBoxes(room, smWidth, smHeight);
            if (!room.getChildren().isEmpty()){
                solidObjectList.addAll(room.getChildren());
            }
            door.createDoor(room, dX, dY);
            roomList.add(room);
            solidObjectList.add(room);
            solidObjectMap.put("room" + roomCount, solidObjectList);
            System.out.println(room.getId() + " dx: " + dX + " dy: " + dY);
            boxCoord.clear();
            roomCount++;
        }
        return roomList;
    }

    private void setColumnConstraints(GridPane pane){
        ColumnConstraints sides = new ColumnConstraints(70);
        ColumnConstraints centerColumns = new ColumnConstraints(smWidth);
        pane.getColumnConstraints().add(0, sides);
        for (int j = 1; j <= (int)dX; j++) {
            pane.getColumnConstraints().add(j, centerColumns);
        }
        pane.getColumnConstraints().add((int)dX + 1, sides);
    }

    private void setRowRestraints(GridPane pane){
        RowConstraints topBottom = new RowConstraints(70);
        RowConstraints middleRows = new RowConstraints(smHeight);
        pane.getRowConstraints().add(0, topBottom);
        for (int k = 1; k <= (int)dY; k++) {
            pane.getRowConstraints().add(k, middleRows);
        }
        pane.getRowConstraints().add((int)dY + 1, topBottom);

    }

    private void setRoomMovable(GridPane pane){
        final double[] deltaX = new double[1];
        final double[] deltaY = new double[1];
        pane.setOnMousePressed(event -> {
            deltaX[0] = pane.getLayoutX() - event.getSceneX();
            deltaY[0] = pane.getLayoutY() - event.getSceneY();

        });
        pane.setOnMouseDragged(event -> {
            pane.setLayoutX(event.getSceneX() + deltaX[0]);
            pane.setLayoutY(event.getSceneY() + deltaY[0]);
        });
    }





}
