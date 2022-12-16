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

import static com.maehem.flatlinejack.Engine.log;
import com.maehem.flatlinejack.engine.GameState;
import com.maehem.flatlinejack.engine.GameStateListener;
import com.maehem.flatlinejack.engine.Player;
import com.maehem.flatlinejack.engine.gui.widgets.DecoBox;
import com.maehem.flatlinejack.engine.gui.widgets.GUIButtonsPane;
import com.maehem.flatlinejack.engine.gui.widgets.OLEDStatusScreen;
import java.util.logging.Level;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

/**
 *
 * @author mark
 */
public class GameControlsPane extends GUIPane implements GameStateListener {
    private final StackPane decoBox = new DecoBox();
    private final OLEDStatusScreen status = new OLEDStatusScreen();
    private final GUIButtonsPane buttons = new GUIButtonsPane();
    
    public GameControlsPane( GameState gs, double width ) {
        gs.addListenter(this);
        
        setPrefWidth(width);
        setPadding(new Insets(8));
        
        
        buttons.setMoney("00000000");
        getChildren().addAll(decoBox, buttons, status);
        
        // Respond to button clicks
        buttons.getInventoryButton().setOnMouseClicked((MouseEvent t) -> {
            log.log(Level.INFO, "User clicked Inventory button.");
            gs.toggleInventoryShowing();
        });
        buttons.getChipButton().setOnMouseClicked((t) -> {
            log.log(Level.INFO, "User clicked Chip button.");
        });
        buttons.getKnowledgeButton().setOnMouseClicked((t) -> {
            log.log(Level.INFO, "User clicked Knowledge button.");
        });
        buttons.getPowerButton().setOnMouseClicked((t) -> {
            log.log(Level.INFO, "User clicked Power button.");
        });
    }

    
    @Override
    public void gameStateVignetteChanged(GameState gs) {}

    @Override
    public void gameStatePropertyChanged(GameState gs, String propKey) {
        switch( propKey ) {
            case Player.PLAYER_MONEY:
                buttons.setMoney(gs.getProperty(propKey));
        }
    }

    @Override
    public void gameStateShowInventory(GameState gs, boolean state) {}
}
