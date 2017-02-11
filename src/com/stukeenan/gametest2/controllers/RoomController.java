package com.stukeenan.gametest2.controllers;

import com.stukeenan.gametest2.model.Door;
import com.stukeenan.gametest2.model.Location;
import com.stukeenan.gametest2.model.MovePlayer;
import com.stukeenan.gametest2.model.Room;
import javafx.animation.AnimationTimer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Stu on 9/6/2016.
 */
public class RoomController {

    private final double rectangleSpeed = 150; // pixels per second

    private final DoubleProperty rectangleVelocity = new SimpleDoubleProperty();
    private final LongProperty lastUpdateTime = new SimpleLongProperty();
    private Bounds[] playerHitBox = new Bounds[4];
    private KeyCode playerDirection;
    private Map<String, List<Bounds>> boundaryMap = new HashMap<>();
    private Room rooms = new Room();
    private final AnimationTimer playerMove = new AnimationTimer() {
        @Override
        public void handle(long timestamp) {
            if (lastUpdateTime.get() > 0) {
                createPlayerHitBox(player);
                final double elapsedSeconds = (timestamp - lastUpdateTime.get()) / 1_000_000_000.0;
                final double deltaX = elapsedSeconds * rectangleVelocity.get();
                final double oldX = player.getTranslateX();
                final double newX = oldX + deltaX;
                final double oldY = player.getTranslateY();
                final double newY = oldY + deltaX;
                if ((playerDirection == KeyCode.LEFT || playerDirection == KeyCode.RIGHT) && moveAcceptable()) {
                    player.setTranslateX(newX);
                } else if ((playerDirection == KeyCode.UP || playerDirection == KeyCode.DOWN) && moveAcceptable()) {
                    player.setTranslateY(newY);
                }
            }
            lastUpdateTime.set(timestamp);
        }
    };

    @FXML
    protected Pane endMenu;

    @FXML
    protected Pane mainMenu;

    @FXML
    protected Pane playSpace;

    @FXML
    public Rectangle player;

    @FXML
    public void initialize() {
        int col = 0;
        int row = 0;
        for (Node rm : rooms.generateRooms()){
            if (rm.getId().matches("room1")){
                col = ((GridPane) rm).getColumnIndex(Door.doorMap.get("BOTTOMDoorroom1"));
                row = ((GridPane) rm).getRowIndex(Door.doorMap.get("BOTTOMDoorroom1"));
                ((GridPane) rm).add(player, col, row);
                ((GridPane) rm).setHalignment(player, HPos.CENTER);
                ((GridPane) rm).setValignment(player, VPos.BOTTOM);
                playSpace.getChildren().add(rm);
                rm.setVisible(false);
            } else {
                playSpace.getChildren().add(rm);
                rm.setVisible(false);

            }
        }
        player.getParent().toFront();
        System.out.println(playSpace.getBoundsInParent().toString());
        System.out.println(playSpace.getChildren());
        System.out.println("FXML loaded");
        System.out.println("col: " + col + " row: " + row);
        Door.mazeMap.entrySet().forEach(e -> System.out.println(e.getKey().getId() + " <-> " + e.getValue().getId()));
    }

    public RoomController(){
        System.out.println("loaded");
    }

    public void keyPressed(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT ||
                event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {
            playerDirection = event.getCode();
            if ((playerDirection == KeyCode.RIGHT || playerDirection == KeyCode.DOWN)) {
                rectangleVelocity.set(rectangleSpeed);
            } else if ((playerDirection == KeyCode.LEFT || playerDirection == KeyCode.UP)) {
                rectangleVelocity.set(-rectangleSpeed);
            }
            playerMove.start();
        }
        if (event.getCode() == KeyCode.ESCAPE) {
           restart(event);
        }
        if (event.getCode() == KeyCode.X){
            if (Door.exitDoor.getBoundsInParent().contains(player.getBoundsInParent())){
                player.setVisible(false);
                endMenu.setVisible(true);
            } else {
                MovePlayer.MovePlayer(player);
            }
        }
    }


    public void keyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT
                || event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {
            rectangleVelocity.set(0);
        }
    }

    public void  createCollisionBox() {
        for (String key : rooms.solidObjectMap.keySet()) {
            List<Bounds> topBoundaryList = new ArrayList<>();
            List<Bounds> bottomBoundaryList = new ArrayList<>();
            List<Bounds> leftBoundaryList = new ArrayList<>();
            List<Bounds> rightBoundaryList = new ArrayList<>();
            for (Node obstacle : rooms.solidObjectMap.get(key)) {
                Bounds topBoundary = new BoundingBox(obstacle.getBoundsInParent().getMinX() + 3,
                        obstacle.getBoundsInParent().getMinY(),
                        obstacle.getBoundsInParent().getWidth() - 6,
                        5.0);
                topBoundaryList.add(topBoundary);
                boundaryMap.put(key + String.valueOf(Location.TOP), topBoundaryList);
                Bounds bottomBoundary = new BoundingBox(obstacle.getBoundsInParent().getMinX() + 3,
                        obstacle.getBoundsInParent().getMinY() + obstacle.getBoundsInParent().getHeight() - 2.0,
                        obstacle.getBoundsInParent().getWidth() - 6,
                        3.0);
                bottomBoundaryList.add(bottomBoundary);
                boundaryMap.put(key + String.valueOf(Location.BOTTOM), bottomBoundaryList);
                Bounds leftBoundary = new BoundingBox(obstacle.getBoundsInParent().getMinX(),
                        obstacle.getBoundsInParent().getMinY() + 3,
                        5.0,
                        obstacle.getBoundsInParent().getHeight() - 3);
                leftBoundaryList.add(leftBoundary);
                boundaryMap.put(key + String.valueOf(Location.LEFT), leftBoundaryList);
                Bounds rightBoundary = new BoundingBox(obstacle.getBoundsInParent().getMinX() + obstacle.getBoundsInParent().getWidth() - 3.0,
                        obstacle.getBoundsInParent().getMinY() + 3,
                        5.0,
                        obstacle.getBoundsInParent().getHeight() - 3);
                rightBoundaryList.add(rightBoundary);
                boundaryMap.put(key + String.valueOf(Location.RIGHT), rightBoundaryList);
            }
        }
    }

    private void createPlayerHitBox(Node player){
        Bounds hitBoxTop = new BoundingBox(player.getBoundsInParent().getMinX(),
                player.getBoundsInParent().getMinY() - 1.0,
                player.getBoundsInParent().getWidth(),
                1.0);
        playerHitBox[0] = hitBoxTop;
        Bounds hitBoxBottom = new BoundingBox(player.getBoundsInParent().getMinX(),
                player.getBoundsInParent().getMinY() + player.getBoundsInParent().getHeight() - 1.0,
                player.getBoundsInParent().getWidth(),
                1.0);
        playerHitBox[2] = hitBoxBottom;
        Bounds hitBoxLeft = new BoundingBox(player.getBoundsInParent().getMinX(),
                player.getBoundsInParent().getMinY(),
                1.0,
                player.getBoundsInParent().getHeight());
        playerHitBox[3] = hitBoxLeft;
        Bounds hitBoxRight = new BoundingBox(player.getBoundsInParent().getMinX() + player.getBoundsInParent().getWidth(),
                player.getBoundsInParent().getMinY(),
                1.0,
                player.getBoundsInParent().getHeight());
        playerHitBox[1] = hitBoxRight;
    }


    private boolean moveAcceptable() {
        if (player.getParent() != null) {
            String roomId = player.getParent().getId();
            for (Bounds bounds : boundaryMap.get(roomId + String.valueOf(Location.BOTTOM))) {
                if ((playerDirection == KeyCode.UP && playerHitBox[0].intersects(bounds)) ||
                        (playerDirection == KeyCode.DOWN && playerHitBox[2].intersects(bounds))) {
//                    System.out.println("hit bottom" + roomId);
                    return false;
                }
            }
            for (Bounds bounds : boundaryMap.get(roomId + String.valueOf(Location.TOP))) {
                if ((playerDirection == KeyCode.UP && playerHitBox[0].intersects(bounds)) ||
                        (playerDirection == KeyCode.DOWN && playerHitBox[2].intersects(bounds))) {
//                    System.out.println("hit top");
                    return false;
                }
            }
            for (Bounds bounds : boundaryMap.get(roomId + String.valueOf(Location.RIGHT))) {
                if ((playerDirection == KeyCode.RIGHT && playerHitBox[1].intersects(bounds)) ||
                        (playerDirection == KeyCode.LEFT && playerHitBox[3].intersects(bounds))) {
//                    System.out.println("hit right");
                    return false;
                }
            }
            for (Bounds bounds : boundaryMap.get(roomId + String.valueOf(Location.LEFT))) {
                if ((playerDirection == KeyCode.LEFT && playerHitBox[3].intersects(bounds)) ||
                        (playerDirection == KeyCode.RIGHT && playerHitBox[1].intersects(bounds))) {
//                    System.out.println("hit left");
                    return false;
                }
            }
        }
        return true;
    }

    public void startButtonPress(ActionEvent event) {
        mainMenu.setVisible(false);
        createCollisionBox();
        player.getParent().setVisible(true);
        System.out.println("solid list " + rooms.solidObjectMap);
        System.out.println("exit door: " + Door.exitDoor.getId());
    }

    public void restartButtonPressed(ActionEvent event) throws IOException {
        restart(event);
    }

    public void restart(Event event) throws IOException {
        Door.mazeMap.clear();
        Door.doorMap.clear();
        Parent gameStartParent = FXMLLoader.load(getClass().getResource("/fxml/RoomController.fxml"));
        Scene gameStartScene = new Scene(gameStartParent);
        Stage gameStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        gameStage.hide();
        gameStage.setScene(gameStartScene);
        gameStage.show();
    }
}
