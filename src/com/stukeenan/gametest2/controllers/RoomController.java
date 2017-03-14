package com.stukeenan.gametest2.controllers;

import com.stukeenan.gametest2.model.*;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TableViewSkinBase;
import javafx.animation.AnimationTimer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Stu on 9/6/2016.
 */
public class RoomController implements Serializable {

    private static final Long serialVersionUID = 1L;

    private final double rectangleSpeed = 150; // pixels per second

    private final DoubleProperty rectangleVelocity = new SimpleDoubleProperty();
    private final LongProperty lastUpdateTime = new SimpleLongProperty();
    private Bounds[] playerHitBox = new Bounds[4];
    private Instant start;
    private Instant stop;
    private KeyCode playerDirection;
    private Map<String, List<Rectangle>> boundaryMap = new HashMap<>();
    private ObservableList<Player> players = FXCollections.observableArrayList();
    private Room rooms = new Room();
    private String playerTime;
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
    private Button addPlayerButton;

    @FXML
    private Button startButton;

    @FXML
    private CheckBox easyModeCheckBox;

    @FXML
    private Pane playSpace;

    @FXML
    private Rectangle mainMenuExitDoor;

    @FXML
    private Rectangle player;

    @FXML
    private TableColumn<Player, String> mazeLengthCol;

    @FXML
    private TableColumn<Player, String> nameCol;

    @FXML
    private TableColumn<Player, String> timeCol;

    @FXML
    private TableView<Player> timeTable;

    @FXML
    private Text yourTimeText;

    @FXML
    private TextField mazeLengthTextField;

    @FXML
    private TextField nameText;

    @FXML
    private VBox endMenu;

    @FXML
    private VBox mainMenu;

    @FXML
    private void initialize(){
        mazeLengthTextField.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue.length() > 2){
                mazeLengthTextField.replaceText(0, mazeLengthTextField.getLength(), oldValue);
            }
        }));

        loadPlayerHistory();
        initPlayerHistoryTable();

        //DISABLES TABLE COLUMN REORDERING
        timeTable.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            final TableHeaderRow header = ((TableViewSkinBase) newSkin).getTableHeaderRow();
            header.reorderingProperty().addListener((o, oldVal, newVal) -> header.setReordering(false));
        });
    }

    private void  createCollisionBox() {
        for (String key : rooms.solidObjectMap.keySet()) {
            List<Rectangle> topBoundaryList = new ArrayList<>();
            List<Rectangle> bottomBoundaryList = new ArrayList<>();
            List<Rectangle> leftBoundaryList = new ArrayList<>();
            List<Rectangle> rightBoundaryList = new ArrayList<>();
            for (Node obstacle : rooms.solidObjectMap.get(key)) {
                Rectangle topBoundary;
                Rectangle rightBoundary;
                Rectangle bottomBoundary;
                Rectangle leftBoundary;
                if (obstacle instanceof Rectangle) {
                    int colIn = GridPane.getColumnIndex(obstacle);
                    int rowIn = GridPane.getRowIndex(obstacle);
                    double height = obstacle.getBoundsInParent().getHeight();
                    double width = obstacle.getBoundsInParent().getWidth();
                    String style = obstacle.getStyle();
                    /*TOP BOUNDARY*/
                    topBoundary = new Rectangle(width, 3);
                    topBoundary.getStyleClass().add(style);
                    ((GridPane) obstacle.getParent()).add(topBoundary, colIn, rowIn);
                    GridPane.setHalignment(topBoundary, HPos.CENTER);
                    GridPane.setValignment(topBoundary, VPos.TOP);
                    topBoundaryList.add(topBoundary);

                    /*RIGHT BOUNDARY*/
                    rightBoundary = new Rectangle(3, height);
                    rightBoundary.getStyleClass().add(style);
                    ((GridPane) obstacle.getParent()).add(rightBoundary, colIn, rowIn);
                    GridPane.setHalignment(rightBoundary, HPos.RIGHT);
                    rightBoundaryList.add(rightBoundary);

                    /*BOTTOM BOUNDARY*/
                    bottomBoundary = new Rectangle(width, 3);
                    bottomBoundary.getStyleClass().add(style);
                    ((GridPane) obstacle.getParent()).add(bottomBoundary, colIn, rowIn);
                    GridPane.setHalignment(bottomBoundary, HPos.CENTER);
                    GridPane.setValignment(bottomBoundary, VPos.BOTTOM);
                    bottomBoundaryList.add(bottomBoundary);

                    /*LEFT BOUNDARY*/
                    leftBoundary = new Rectangle(3, height);
                    leftBoundary.getStyleClass().add(style);
                    ((GridPane) obstacle.getParent()).add(leftBoundary, colIn, rowIn);
                    GridPane.setHalignment(leftBoundary, HPos.LEFT);
                    leftBoundaryList.add(leftBoundary);

                }
                if (obstacle instanceof GridPane){
                    double height = ((GridPane) obstacle).getPrefHeight();
                    double width = ((GridPane) obstacle).getPrefWidth();
                    String style = "roomBoundary";

                    /*TOP BOUNDARY*/
                    topBoundary = new Rectangle(width, 1);
                    topBoundary.getStyleClass().add(style);
                    ((GridPane) obstacle).add(topBoundary, 0, 0,
                            GridPane.REMAINING, GridPane.REMAINING);
                    GridPane.setHalignment(topBoundary, HPos.LEFT);
                    GridPane.setValignment(topBoundary, VPos.TOP);
                    topBoundaryList.add(topBoundary);

                    /*RIGHT BOUNDARY */
                    rightBoundary = new Rectangle(1, height);
                    rightBoundary.getStyleClass().add(style);
                    ((GridPane) obstacle).add(rightBoundary,
                            ((GridPane) obstacle).getColumnConstraints().size() - 1, 0,
                            GridPane.REMAINING, GridPane.REMAINING);
                    GridPane.setHalignment(rightBoundary, HPos.RIGHT);
                    GridPane.setValignment(rightBoundary, VPos.TOP);
                    rightBoundaryList.add(rightBoundary);

                    /*BOTTOM BOUNDARY*/
                    bottomBoundary = new Rectangle(width, 1);
                    bottomBoundary.getStyleClass().add(style);
                    ((GridPane) obstacle).add(bottomBoundary, 0,
                            ((GridPane) obstacle).getRowConstraints().size() - 1,
                            GridPane.REMAINING, GridPane.REMAINING);
                    GridPane.setHalignment(bottomBoundary, HPos.LEFT);
                    GridPane.setValignment(bottomBoundary, VPos.BOTTOM);
                    bottomBoundaryList.add(bottomBoundary);

                    /*LEFT BOUNDARY*/
                    leftBoundary = new Rectangle(1, height);
                    leftBoundary.getStyleClass().add(style);
                    ((GridPane) obstacle).add(leftBoundary, 0, 0,
                            GridPane.REMAINING, GridPane.REMAINING);
                    GridPane.setHalignment(leftBoundary, HPos.LEFT);
                    GridPane.setValignment(leftBoundary, VPos.TOP);
                    leftBoundaryList.add(leftBoundary);
                }
            }
            boundaryMap.put(key + String.valueOf(Location.TOP), topBoundaryList);
            boundaryMap.put(key + String.valueOf(Location.RIGHT), rightBoundaryList);
            boundaryMap.put(key + String.valueOf(Location.BOTTOM), bottomBoundaryList);
            boundaryMap.put(key + String.valueOf(Location.LEFT), leftBoundaryList);
        }
    }

    private void createPlayerHitBox(Node player){
        Bounds hitBoxTop = new BoundingBox(player.getBoundsInParent().getMinX(),
                player.getBoundsInParent().getMinY() - 1.0,
                player.getBoundsInParent().getWidth(),
                1.0);
        playerHitBox[0] = hitBoxTop;
        Bounds hitBoxRight = new BoundingBox(player.getBoundsInParent().getMinX() + player.getBoundsInParent().getWidth(),
                player.getBoundsInParent().getMinY(),
                1.0,
                player.getBoundsInParent().getHeight());
        playerHitBox[1] = hitBoxRight;
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
    }

    public void easyMode(ActionEvent event) {
        if (easyModeCheckBox.isSelected()) {
            mainMenuExitDoor.getStyleClass().add("easyMode");
        } else {
            mainMenuExitDoor.getStyleClass().remove("easyMode");
        }
    }


    public void handleAddPlayer(ActionEvent event) throws FileNotFoundException {
        Save save = new Save();
        Player player = new Player(String.valueOf(nameText.getCharacters()),
                playerTime, String.valueOf(Room.mazeLength));
        players.add(player);
        nameText.clear();
        addPlayerButton.setDisable(true);
        timeCol.setSortable(true);
        timeTable.sort();
        timeCol.setSortable(false);
        if (players.size() > 5) {
            players.remove(5);
        }
        save.serializeTimeTable(players);
    }

    private void initPlayerHistoryTable(){
        timeTable.setEditable(true);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setSortable(false);
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        timeCol.setSortType(TableColumn.SortType.ASCENDING);
        timeTable.getSortOrder().add(0, timeCol);
        timeCol.setSortable(false);
        mazeLengthCol.setCellValueFactory(new PropertyValueFactory<>("mazeLength"));
        mazeLengthCol.setSortable(false);
    }


    public void keyPressed(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT ||
                event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {
            playerDirection = event.getCode();
            if ((playerDirection == KeyCode.RIGHT || playerDirection == KeyCode.DOWN)) {
                rectangleVelocity.set(rectangleSpeed);
            }
            if ((playerDirection == KeyCode.LEFT || playerDirection == KeyCode.UP)) {
                rectangleVelocity.set(-rectangleSpeed);
            }
            playerMove.start();
        }
        if (event.getCode() == KeyCode.ESCAPE) {
            restart(event);
        }
        if (event.getCode() == KeyCode.X){
            if (Maze.exitDoor.getBoundsInParent().contains(player.getBoundsInParent())){
                player.setVisible(false);
                endMenu.setVisible(true);
                stop = Instant.now();
                setYourTimeText();
            } else {
                MovePlayer.movePlayer(player);
            }
        }
    }

    public void keyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT
                || event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {
            rectangleVelocity.set(0);
        }
    }

    private void loadPlayerHistory(){
        players = LoadPlayerHistory.playerHistory();
        timeTable.setItems(players);

    }

    private boolean moveAcceptable() {
        if (player.getParent() != null) {
            String roomId = player.getParent().getId();
            for (Rectangle border : boundaryMap.get(roomId + String.valueOf(Location.BOTTOM))) {
                Bounds bounds = border.getBoundsInParent();
                if ((playerDirection == KeyCode.UP && playerHitBox[0].intersects(bounds)) ||
                        (playerDirection == KeyCode.DOWN && playerHitBox[2].intersects(bounds))) {
//                    System.out.println("hit bottom" + roomId);
                    return false;
                }
            }
            for (Rectangle border : boundaryMap.get(roomId + String.valueOf(Location.TOP))) {
                Bounds bounds = border.getBoundsInParent();
                if ((playerDirection == KeyCode.UP && playerHitBox[0].intersects(bounds)) ||
                        (playerDirection == KeyCode.DOWN && playerHitBox[2].intersects(bounds))) {
//                    System.out.println("hit top");
                    return false;
                }
            }
            for (Rectangle border : boundaryMap.get(roomId + String.valueOf(Location.RIGHT))) {
                Bounds bounds = border.getBoundsInParent();
                if ((playerDirection == KeyCode.RIGHT && playerHitBox[1].intersects(bounds)) ||
                        (playerDirection == KeyCode.LEFT && playerHitBox[3].intersects(bounds))) {
//                    System.out.println("hit right");
                    return false;
                }
            }
            for (Rectangle border : boundaryMap.get(roomId + String.valueOf(Location.LEFT))) {
                Bounds bounds = border.getBoundsInParent();
                if ((playerDirection == KeyCode.LEFT && playerHitBox[3].intersects(bounds)) ||
                        (playerDirection == KeyCode.RIGHT && playerHitBox[1].intersects(bounds))) {
//                    System.out.println("hit left");
                    return false;
                }
            }
        }
        return true;
    }

    private void restart(Event event) throws IOException {
//        Maze.mazeMap.clear();
        Door.doorMap.clear();
        Font.loadFont(getClass().getResource("/fonts/Kenney Future Square.ttf").toExternalForm(), 10);
        Parent gameStartParent = FXMLLoader.load(getClass().getResource("/fxml/RoomController.fxml"));
        Scene gameStartScene = new Scene(gameStartParent);
        gameStartScene.getStylesheets().add("/css/MazeGame.css");
        Stage gameStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        gameStage.hide();
        gameStage.setScene(gameStartScene);
        gameStage.show();
    }

    public void restartButtonPressed(ActionEvent event) throws IOException {
        restart(event);
        start = Instant.now();
    }

    private void setMazeLength() {
        String sequence = mazeLengthTextField.getText();
        if (sequence.length() > 2){
            String string = mazeLengthTextField.getText(0, 2);
            mazeLengthTextField.replaceText(0, 2, string);
        }
        if (sequence.isEmpty() || !sequence.matches("[0-9]+") || Integer.valueOf(sequence) < 3){
            Room.mazeLength = 3;
        }else {
            Room.mazeLength = Integer.valueOf(sequence);
        }
    }

    public void setYourTimeText(){
        Duration time = Duration.between(start, stop);
        int minutes = (int) (time.getSeconds() % (60 * 60) / 60);
        int seconds = (int) (time.getSeconds() % 60);
        int milliseconds = time.getNano() / 1000000;
        playerTime = (String.format("%02d:%02d:%02d", minutes, seconds, milliseconds));
        yourTimeText.setText(playerTime);
    }

    public void startButtonPressed(ActionEvent event) {
        setMazeLength();
        player.setVisible(true);
        rooms.generateRooms();
        int col;
        int row;
        for (Node rm : rooms.roomList){
            if (rm.getId().matches("room0")){
                col = GridPane.getColumnIndex(Door.doorMap.get("BOTTOMDoorroom0"));
                row = GridPane.getRowIndex(Door.doorMap.get("BOTTOMDoorroom0"));
                ((GridPane) rm).add(player, col, row);
                GridPane.setHalignment(player, HPos.CENTER);
                GridPane.setValignment(player, VPos.BOTTOM);
                playSpace.getChildren().add(rm);
                rm.setVisible(false);
            } else {
                playSpace.getChildren().add(rm);
                rm.setVisible(false);

            }
        }
        Maze maze = new Maze();
        maze.createMaze();
        player.getParent().toFront();
        mainMenu.setVisible(false);
        player.getParent().setVisible(true);
        System.out.println("exit door: " + Maze.exitDoor.getId());
        createCollisionBox();
        if (easyModeCheckBox.isSelected()){
            Maze.exitDoor.getStyleClass().add("easyMode");
        }
        start = Instant.now();

    }
}
