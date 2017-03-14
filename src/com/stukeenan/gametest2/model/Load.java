package com.stukeenan.gametest2.model;

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
        File file = new File("F:\\Documents\\IdeaProjects\\game_test_2\\resources\\saveData\\timeTable.ser");
        if (!file.exists()){
            System.out.println("no save data found");
        } else {

            try (ObjectInputStream ois
                         = new ObjectInputStream(new FileInputStream(file))) {
                Object inObj = ois.readObject();
                if (inObj.getClass() == ArrayList.class) {
                    System.err.println("IS LIST");
                    savedPlayers = (ArrayList<ArrayList<String>>) inObj;
                }


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return savedPlayers;
    }

}
