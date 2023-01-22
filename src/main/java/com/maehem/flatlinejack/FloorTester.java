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
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author mark
 */
public class FloorTester extends Application {

    private static final double SIZE = 400.0;

    //private Stage window;
    StackPane centerPane = new StackPane();
    private final BorderPane root = new BorderPane(centerPane);
    private final Scene scene = new Scene(root);
    private final Button generateButton = new Button("Generate");

    private static final Color TRACE_COLOR = Color.BLUE;
    private static final double TRACE_WIDTH = 15.0;
    private static final Color VIA_PAD_COLOR = Color.BLUEVIOLET;
    private static final double VIA_PAD_RADIUS = 15.0;
    private static final Color VIA_HOLE_COLOR = Color.MAGENTA;
    private static final double VIA_HOLE_RADIUS = 5.0;

    @Override
    public void start(Stage window) {
        //this.window = window;

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

        ToggleButton[] topToggle = addToggle(top, false);
        ToggleButton[] rightToggle = addToggle(right, true);
        ToggleButton[] bottomToggle = addToggle(bottom, false);
        ToggleButton[] leftToggle = addToggle(left, true);

        Router router = new Router(7, SIZE,
                TRACE_COLOR, TRACE_WIDTH,
                VIA_PAD_COLOR, VIA_PAD_RADIUS,
                VIA_HOLE_COLOR, VIA_HOLE_RADIUS
        );

        root.setCenter(router);

        generateButton.setOnAction((t) -> {
            LOGGER.log(INFO, "Generate...");
            router.setTop(getBits(topToggle));
            router.setRight(getBits(rightToggle));
            router.setBottom(getBits(bottomToggle));
            router.setLeft(getBits(leftToggle));

            router.generate();

            // Uncomment to pop a new window with the generated image.
//            Stage stage = new Stage();
//            //Fill stage with content
//            Scene imgScene = new Scene(new Group(new ImageView(router.snap())));
//            stage.setScene(imgScene);
//            stage.show();
            
            
        });

        window.setScene(this.scene);
        window.setResizable(false);
        window.setOnCloseRequest(e -> Platform.exit());
        window.show();
    }

    private ToggleButton[] addToggle(Group dest, boolean vertical) {

        String labels[] = {"0", "1", "2", "3", "4", "5", "6"};
        ToggleButton buttons[] = new ToggleButton[labels.length];

        double space = SIZE / (labels.length * 2.0 + 2.5);
        double btnSize = space;

        for (int i = 0; i < labels.length; i++) {
            buttons[i] = new ToggleButton(labels[i]);
            buttons[i].setPrefSize(btnSize, btnSize);
        }

        if (vertical) {
            VBox box = new VBox(space); //, spacer);
            box.setFillWidth(true);
            box.setPadding(new Insets(1.5 * btnSize, 0, 1.5 * btnSize, 0));
            box.getChildren().addAll(buttons);
            dest.getChildren().add(box);
        } else {
            HBox box = new HBox(space * 1.05);//, spacer);
            box.setPadding(new Insets(0, 2.5 * btnSize, 0, 2.5 * btnSize));
            box.setFillHeight(true);
            box.getChildren().addAll(buttons);
            dest.getChildren().add(box);
        }
        return buttons;
    }

    private int getBits(ToggleButton[] buttons) {
        int result = 0;
        for (int i = buttons.length - 1; i >= 0; i--) {
            result = result << 1;
            result |= buttons[i].isSelected() ? 0x1 : 0x0;
        }

        return result;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
