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
package com.maehem.flatlinejack.engine.gui;

import com.maehem.flatlinejack.Engine;
import com.maehem.flatlinejack.engine.Player;
import com.maehem.flatlinejack.engine.gui.widgets.Button;
import com.maehem.flatlinejack.engine.gui.widgets.DSEG7Display;
import com.maehem.flatlinejack.engine.gui.widgets.Gauge;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class GUI extends Group {

    private final Engine engine;
    private double menuPopY = 0;
    private final static double MENU_TAB_SHOW = 40;
    private final static double MENU_HEIGHT = 190;
    private final static double MENU_POP_Y_MAX = MENU_HEIGHT-MENU_TAB_SHOW-8;
    private final static double MENU_POP_Y_INCR = 12;
    private final static double BUTTON_SIZE = 48;
    

    private DSEG7Display money;
    private Gauge healthGauge;
    private Gauge constitutionGauge;
    private Button saveButton;
    private Button inventoryButton;
    private Button chipButton;
    private Button quitButton;
    private Button knowledgeButton;

    public GUI(Engine engine) {
        this.engine = engine;

        initMenuContent();
        //initMenuPopper();
    }

    private void initMenuContent() {
        // Place this GUI box off the bottom of the screen
        // with just a few pixels visible.  Mousing over
        // it will pop it like a tab.
        //setLayoutY(720-MENU_TAB_SHOW);

        // Menu base
        Rectangle rectangle = new Rectangle( engine.getScene().getWidth()/2.0, MENU_HEIGHT);
        rectangle.setStrokeWidth(3.0);
        rectangle.setFill(Color.SLATEGREY);
        getChildren().add(rectangle);

        initGauges();
        
        initButtons();

    }

    /**
     * Animations for popping menu up and down when user moves mouse over it.
     *
     */
    private void initMenuPopper() {
        setOnMouseEntered((event) -> {
            AnimationTimer timer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (menuPopY <= MENU_POP_Y_MAX) {
                        menuPopY += MENU_POP_Y_INCR;
                        setTranslateY(-menuPopY);  // Negative as we are sliding upward.
                    } else {
                        this.stop();
                    }
                }
            };
            timer.start();
        });

        setOnMouseExited((event) -> {
            AnimationTimer timer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (menuPopY > 0) {
                        menuPopY -= MENU_POP_Y_INCR;
                        setTranslateY(-menuPopY);  // Negative as we are sliding upward.
                    } else {
                        this.stop();
                    }
                }
            };
            timer.start();
        });
    }

    public void refresh() {
        money.setText(String.valueOf(engine.getPlayer().getMoney()));
        healthGauge.setValue(engine.getPlayer().getHealth());
        constitutionGauge.setValue(engine.getPlayer().getConstitution());
    }

    private void initGauges() {
        money = new DSEG7Display(
                180, 10,
                130, 26,
                GUI.class.getResourceAsStream("/icons/money-icon.png")
        );
        money.setText(String.valueOf(engine.getPlayer().getMoney()));
        getChildren().add(money);

        healthGauge = new Gauge(
                180, 40,
                130, 26,
                16, -30,
                GUI.class.getResourceAsStream("/icons/heart-icon.png"),
                engine.getPlayer().getHealth(),
                Player.PLAYER_HEALTH_MAX
        );
        getChildren().add(healthGauge);

        constitutionGauge = new Gauge(
                180, 70,
                130, 26,
                42, -60,
                GUI.class.getResourceAsStream("/icons/shield-icon.png"),
                engine.getPlayer().getConstitution(),
                Player.PLAYER_CONSTITUTION_MAX
        );
        getChildren().add(constitutionGauge);
        
    }

    private void initButtons() {        
        inventoryButton = new Button(BUTTON_SIZE, BUTTON_SIZE, GUI.class.getResourceAsStream("/icons/inventory-icon.png"));
        chipButton = new Button(BUTTON_SIZE, BUTTON_SIZE, GUI.class.getResourceAsStream("/icons/microchip-icon.png"));
        knowledgeButton = new Button(BUTTON_SIZE, BUTTON_SIZE, GUI.class.getResourceAsStream("/icons/knowledge-icon.png"));
        saveButton = new Button(BUTTON_SIZE, BUTTON_SIZE, GUI.class.getResourceAsStream("/icons/save-icon.png"));
        quitButton = new Button(BUTTON_SIZE, BUTTON_SIZE, GUI.class.getResourceAsStream("/icons/quit-icon.png"));

        FlowPane buttonPane = new FlowPane(16, 0, inventoryButton, knowledgeButton, chipButton, saveButton, quitButton );
        buttonPane.setLayoutX(8);
        buttonPane.setLayoutY(getLayoutBounds().getHeight()-BUTTON_SIZE-8);
        
        getChildren().add(buttonPane);
        
        saveButton.setOnMouseClicked((event) -> {
            engine.doSave();
        });
        
        quitButton.setOnMouseClicked((event) -> {
            engine.doExit();
        });
        
        inventoryButton.setOnMouseClicked((event) -> {
            engine.getInventoryPane().setVisible(true);
        });
        
    }
}
