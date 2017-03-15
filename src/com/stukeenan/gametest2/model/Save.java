package com.stukeenan.gametest2.model;

import javafx.collections.ObservableList;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Stu on 3/11/2017.
 */
public class Save {

    public void serializeTimeTable(ObservableList<Player> players) {
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        int counter = 0;
        for (Player p : players){
            ArrayList<String> player = new ArrayList<>(3);
            player.add(0, p.getName());
            player.add(1, p.getTime());
            player.add(2, p.getMazeLength());
            data.add(counter, player);
            counter++;
        }

        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream("resources/saveData/timeTable.ser"))) {

            oos.writeObject(data);
            System.out.println("saved");

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
