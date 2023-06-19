/*
    Licensed to the Apache Software Foundation (ASF) under one or more 
    contributor license agreements.  See the NOTICE file distributed with this
    work for additional information regarding copyright ownership.  The ASF 
    licenses this file to you under the Apache License, Version 2.0 
    (the "License"); you may not use this file except in compliance with the 
    License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software 
    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the 
    License for the specific language governing permissions and limitations 
    under the License.
 */
package com.maehem.abyss;

import com.maehem.abyss.engine.PoseSheet;
import com.maehem.abyss.engine.gui.widgets.Gauge;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author mark
 */
public class PoseSheetTester extends Application {

    private static final double SIZE = 400.0;
    private static final Font BUTTON_FONT = Font.font(34);

private final ObservableList<String> directions = FXCollections.observableArrayList(
               "Left", "Right", "Toward", "Away");

    private final Pane checkerPane = new Pane();
    private final PoseSheet poseSheet = new PoseSheet((int) SIZE);
    private final StackPane centerPane = new StackPane(checkerPane, poseSheet);
    private final BorderPane root = new BorderPane(centerPane);
    private final Scene scene = new Scene(root);
    private final MenuBar menuBar = new MenuBar();
    private final FileChooser fileChooser = new FileChooser();
    private final Label rowsLabel = new Label(" Rows:");
    private final Spinner rowsSpinner = new Spinner(1, 4, 4);
    private final Label colsLabel = new Label(" Cols:");
    private final Spinner colsSpinner = new Spinner(1, 12, 12);
    private final Spinner dirSpinner = new Spinner(directions);
    

    private Stage stage;
    Timeline timeline;

    @Override
    public void start(Stage window) {
        this.stage = window;
        HBox menuBox = new HBox(menuBar,
                rowsLabel, rowsSpinner, 
                colsLabel ,colsSpinner
        );
        root.setTop(menuBox);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setSpacing(5);
        
        
        initFileMenu();
        initBackground();
        
        initPlayControls();

        window.setScene(this.scene);
        window.setResizable(false);
        window.setOnCloseRequest(e -> Platform.exit());
        window.show();
    }

    private void initBackground() {
        
        for (int y=0; y<12; y++) {
            for (int x=y%2; x<8; x+=2) {
                Rectangle r = new Rectangle(SIZE/8, SIZE/8, Color.LIGHTGRAY);
                r.setLayoutX(x*SIZE/8);
                r.setLayoutY(y*SIZE/8);
                checkerPane.getChildren().add(r);
            }
        }

    }
    
    private void initPlayControls() {
        Button stepBackButton = new Button("\u23ee");
        stepBackButton.setFont(BUTTON_FONT);
        stepBackButton.setOnAction((t) -> {
            poseSheet.previousPose();
        });
        
        Button playButton = new Button("\u27A1");
        playButton.setFont(BUTTON_FONT);
        playButton.setOnAction((t) -> {
            timeline = new Timeline(new KeyFrame(
                    Duration.millis(50),
                    ae -> {
                        poseSheet.nextPose();
                        root.requestLayout();
                    }));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
            playButton.setDisable(true);
        });
        
        Button stepFwdButton = new Button("\u23ED");
        stepFwdButton.setFont(BUTTON_FONT);
        stepFwdButton.setOnAction((t) -> {
            poseSheet.nextPose();
        });
        
        Button stopButton = new Button("\u2B1B");
        stopButton.setFont(BUTTON_FONT);
        stopButton.setOnAction((t) -> {
            if ( timeline != null ) {
                timeline.stop();
                timeline = null;
                playButton.setDisable(false);
            }
        });
        
        HBox controls = new HBox(
                stepBackButton,
                playButton,
                stopButton, 
                stepFwdButton,
                dirSpinner
        );
        controls.setAlignment(Pos.CENTER);
        root.setBottom(controls);
    }
    
    
    private void initFileMenu() {
        Menu fileMenu = new Menu("File");
        menuBar.getMenus().addAll(fileMenu);

        MenuItem loadItem = new MenuItem("Load...");
        fileMenu.getItems().add(loadItem);

        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("PNG Files", "*.png")
        );
        loadItem.setOnAction((t) -> {
            // File Chooser
            File pngFile = fileChooser.showOpenDialog(stage);
            if ( pngFile != null ) {
                try {
                    poseSheet.setSkin(new FileInputStream(pngFile), 4, 12);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(PoseSheetTester.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
