package com.stukeenan.gametest2.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

/**
 * Created by Stu on 3/11/2017.
 */
public class LoadPlayerHistory {

    public static ObservableList<Player> playerHistory() {
        ObservableList<Player> pHistory = FXCollections.observableArrayList();
        ArrayList<ArrayList<String>> playerHistory = Load.deserializePlayer();
        if (!playerHistory.isEmpty()) {
            playerHistory.forEach(e -> {
                Player p = new Player(e.get(0), e.get(1), e.get(2));
                pHistory.add(p);
            });
        }
        return pHistory;
    }
}
