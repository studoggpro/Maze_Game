package com.stukeenan.Maze_Game.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Created by Stu on 3/11/2017.
 */
public class Load {
    public static ArrayList<ArrayList<String>> deserializePlayer() {

        ArrayList<ArrayList<String>> savedPlayers = new ArrayList<>();
        File file = new File("saveData/timeTable.ser");
        if (file.exists()){
            try (ObjectInputStream ois
                         = new ObjectInputStream(new FileInputStream(file))) {
                Object inObj = ois.readObject();
                if (inObj.getClass() == ArrayList.class) {
                    System.err.println("IS LIST");
                    savedPlayers = (ArrayList<ArrayList<String>>) inObj;
                    ois.close();
                }


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return savedPlayers;
    }

}
