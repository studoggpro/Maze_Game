package com.stukeenan.Maze_Game.model;

import javafx.animation.FadeTransition;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

import java.util.Map;

/**
 * Created by Stu on 1/9/2017.
 */
public class MovePlayer {
    private Node oldDoor;
    private Node mPlayer;
    private FadeTransition ft;
    private GridPane oldParent;

    private void changeParent(Node door) {
        if (door.getParent() instanceof GridPane) {
            GridPane newParent = (GridPane)door.getParent();
            int col = GridPane.getColumnIndex(door);
            int row = GridPane.getRowIndex(door);
            mPlayer.setTranslateX(0);
            mPlayer.setTranslateY(0);
            newParent.add(mPlayer, col, row);
            GridPane.setHalignment(mPlayer, GridPane.getHalignment(door));
            GridPane.setValignment(mPlayer, GridPane.getValignment(door));
            newParent.setVisible(true);
            newParent.toFront();
            fadeIn();
        } else {
            System.out.println("oldDoor parent not gridpane!!!");
        }
    }


    private void doorToNowhere(){
        System.out.println("Door to nowhere");
        fadeIn();
    }

    private void fadeIn(){
        ft = new FadeTransition(javafx.util.Duration.seconds(0.3), mPlayer);
        ft.setFromValue(0);
        ft.setToValue(1.0);
        ft.play();
    }

    public void fadeOut(){
        ft = new FadeTransition(javafx.util.Duration.seconds(0.3), mPlayer);
        ft.setFromValue(1.0);
        ft.setToValue(0);
        ft.setOnFinished(e -> removePlayer());
        ft.play();
    }

    public MovePlayer(Node player){
        this.mPlayer = player;
    }


    private void removePlayer() {
        if (Maze.mazeMap.keySet().contains(oldDoor)) {
            if (Maze.mazeMap.get(oldDoor) != null) {
                oldParent.getChildren().remove(mPlayer);
                GridPane.clearConstraints(mPlayer);
                changeParent(Maze.mazeMap.get(oldDoor));
                System.out.println("Destination: " + Maze.mazeMap.get(oldDoor).getId());
            } else {
                doorToNowhere();
            }
        } else {
            if (Maze.mazeMap.values().contains(oldDoor)) {
                Node destination;
                try {
                    destination = Maze.mazeMap.entrySet().stream()
                            .filter(e -> oldDoor.equals(e.getValue()))
                            .map(Map.Entry::getKey)
                            .findAny().orElse(null);
                    oldParent.getChildren().remove(mPlayer);
                    GridPane.clearConstraints(mPlayer);
                    changeParent(destination);
                    System.out.println("Destination: " + destination.getId());
                } catch (NullPointerException npe) {
                    doorToNowhere();
                }
            }
        }
    }

    public boolean validMovePlayer() {
        Bounds playerBounds = mPlayer.getBoundsInParent();
        if (mPlayer.getParent() instanceof GridPane) {
            oldParent = ((GridPane) mPlayer.getParent());
            for (Node child : oldParent.getChildren()) {
                if (child.getId() != null
                        && child.getId().matches("\\S*Door\\S*")
                        && child.getBoundsInParent().contains(playerBounds)) {
                    System.out.println("FOUND in DOOR: " + child.getId());
                    oldDoor = child;
                    return true;
                }
            }
        }
        return false;
    }
}
