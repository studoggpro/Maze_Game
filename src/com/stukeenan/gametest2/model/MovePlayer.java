package com.stukeenan.gametest2.model;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

import java.util.Map;

/**
 * Created by Stu on 1/9/2017.
 */
public class MovePlayer {

    public static void MovePlayer(Node player) {
        Bounds playerBounds = player.getBoundsInParent();
        if (player.getParent() instanceof GridPane){
            GridPane oldParent = ((GridPane) player.getParent());
            for (Node child : oldParent.getChildren()) {
                if (child.getId() != null
                    && child.getId().matches("\\S*Door\\S*")
                    && child.getBoundsInParent().contains(playerBounds)) {
                    System.out.println("FOUND in DOOR: " + child.getId());
                    oldParent.getChildren().remove(player);
                    oldParent.clearConstraints(player);
                    if (child.getId().contains(String.valueOf(Location.TOP)) ||
                            child.getId().contains(String.valueOf(Location.LEFT))) {
                        changeParent(player, Door.mazeMap.get(child));
                        System.out.println("Destination: " + Door.mazeMap.get(child).getId());
                    } else {
                        if (child.getId().contains(String.valueOf(Location.BOTTOM)) ||
                                child.getId().contains(String.valueOf(Location.RIGHT))) {
                            Node destination = Door.mazeMap.entrySet().stream()
                                    .filter(e -> child.equals(e.getValue()))
                                    .map(Map.Entry::getKey)
                                    .findAny().get();
                            changeParent(player, destination);
                            System.out.println("Destination: " + destination.getId());
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
