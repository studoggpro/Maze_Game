package com.stukeenan.gametest2.model;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

import java.util.Map;

/**
 * Created by Stu on 1/9/2017.
 */
public class MovePlayer {

    public static void movePlayer(Node player) {
        Bounds playerBounds = player.getBoundsInParent();
        if (player.getParent() instanceof GridPane){
            GridPane oldParent = ((GridPane) player.getParent());
            for (Node child : oldParent.getChildren()) {
                if (child.getId() != null
                        && child.getId().matches("\\S*Door\\S*")
                        && child.getBoundsInParent().contains(playerBounds)) {
                    System.out.println("FOUND in DOOR: " + child.getId());
                    if (Maze.mazeMap.keySet().contains(child)) {
                        if (Maze.mazeMap.get(child) != null) {
                            oldParent.getChildren().remove(player);
                            oldParent.clearConstraints(player);
                            changeParent(player, Maze.mazeMap.get(child));
                            System.out.println("Destination: " + Maze.mazeMap.get(child).getId());
                        } else {
                            System.out.println("Door to nowhere");
                        }
                    } else {
                        if (Maze.mazeMap.values().contains(child)) {
                            Node destination;
                            try {
                                destination = Maze.mazeMap.entrySet().stream()
                                        .filter(e -> child.equals(e.getValue()))
                                        .map(Map.Entry::getKey)
                                        .findAny().orElse(null);
                                oldParent.getChildren().remove(player);
                                oldParent.clearConstraints(player);
                                changeParent(player, destination);
                                System.out.println("Destination: " + destination.getId());
                            } catch (NullPointerException npe) {
                                System.out.println("Door to nowhere");
                            }
                        }
                    }
                    break;
                }
            }
        }
    }


    private static void changeParent(Node player, Node door) {
        if (door.getParent() instanceof GridPane) {
            GridPane newParent = (GridPane)door.getParent();
            int col = newParent.getColumnIndex(door);
            int row = newParent.getRowIndex(door);
            player.setTranslateX(0);
            player.setTranslateY(0);
            newParent.add(player, col, row);
            newParent.setHalignment(player, newParent.getHalignment(door));
            newParent.setValignment(player, newParent.getValignment(door));
            newParent.setVisible(true);
            newParent.toFront();
        } else {
            System.out.println("door parent not gridpane!!!");
        }
    }
}
