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
package com.maehem.flatlinejack;

import static com.maehem.flatlinejack.Engine.LOGGER;

import com.maehem.flatlinejack.engine.matrix.Router;
import static java.util.logging.Level.INFO;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author mark
 */
public class FloorTester extends Application {

    private static final double SIZE = 400.0;
    
    private Stage window;
    StackPane centerPane = new StackPane();
    private final BorderPane root = new BorderPane(centerPane);
    private final Scene scene = new Scene(root);
    private final Button generateButton = new Button("Generate");

    @Override
    public void start(Stage window) {

        ToolBar toolBar = new ToolBar();
        toolBar.getItems().add(generateButton);
        Group top = new Group();
        VBox topArea = new VBox(toolBar, top);
       root.setTop(topArea);
        Group right = new Group();
       root.setRight(right);
        Group bottom = new Group();
       root.setBottom(bottom);
        Group left = new Group();
       root.setLeft(left);
        
        ToggleButton[]topToggle    = addToggle(   top, false);
        ToggleButton[]rightToggle  = addToggle( right, true);
        ToggleButton[]bottomToggle = addToggle(bottom, false);
        ToggleButton[]leftToggle   = addToggle(  left, true);
        
        Router router = new Router( 7, SIZE);
        
        root.setCenter(router);
        
        generateButton.setOnAction((t) -> {
            LOGGER.log(INFO, "Generate...");
            //LOGGER.log(INFO, "   Top ");
            router.setTop(getBits(topToggle));
            //LOGGER.log(INFO, " Right: ");
            router.setRight(getBits(rightToggle));
            //LOGGER.log(INFO, "Bottom: ");
            router.setBottom(getBits(bottomToggle));
            //LOGGER.log(INFO, "  Left: ");
            router.setLeft(getBits(leftToggle));
            
            router.generate();
        });
        
        root.requestLayout();
        
        this.window = window;
        window.setScene(this.scene);
        window.setResizable(false);
        window.setOnCloseRequest(e -> Platform.exit());
        window.show();
    }

    private ToggleButton[] addToggle(Group dest, boolean vertical) {
        //ToggleGroup group = new ToggleGroup();

        String labels[] = {"0", "1", "2", "3", "4", "5", "6"};
        ToggleButton buttons[] = new ToggleButton[labels.length];
        
        //double space = 2.0*SIZE/((labels.length*4.0));
        double space = SIZE/(labels.length*2.0+2.5);
        double btnSize = space;
        
        for (int i = 0; i < labels.length; i++) {
            buttons[i] = new ToggleButton(labels[i]);
            buttons[i].setPrefSize(btnSize, btnSize);
        }

        if (vertical) {
           VBox box = new VBox(space); //, spacer);
            box.setFillWidth(true);
            box.setPadding(new Insets(1.5*btnSize, 0, 1.5*btnSize, 0));
            box.getChildren().addAll(buttons);
            dest.getChildren().add(box);
        } else {
//            Pane spacer = new Pane();
//            spacer.setPrefSize(1.5*btnSize, btnSize);
            HBox box = new HBox(space*1.05 );//, spacer);
            box.setPadding(new Insets(0, 2.5*btnSize, 0, 2.5*btnSize));
            box.setFillHeight(true);
            box.getChildren().addAll(buttons);
            dest.getChildren().add(box);
        }
        return buttons;
    }

    private int getBits( ToggleButton[] buttons ) {
        int result = 0;
        for (int i=buttons.length-1; i>=0; i-- ) {
            result = result << 1;
            result |= buttons[i].isSelected()?0x1:0x0;
        }
        
//        LOGGER.log(INFO, "Bit result:  {0}", 
//                String.format(
//                        "%" + buttons.length + "s", 
//                    Integer.toBinaryString(result)
//        ).replace(" ", "0"));
        
        return result;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
