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
package com.maehem.abyss.engine.gui;

import static com.maehem.abyss.Engine.LOGGER;

import com.maehem.abyss.engine.GameState;
import static com.maehem.abyss.engine.GameState.Display.CHIPS;
import com.maehem.abyss.engine.GameStateListener;
import com.maehem.abyss.engine.Player;
import com.maehem.abyss.engine.bbs.BBSTerminal;
import com.maehem.abyss.engine.gui.widgets.Button;
import com.maehem.abyss.engine.gui.widgets.DecoBox;
import com.maehem.abyss.engine.gui.widgets.GUIButtonsPane;
import com.maehem.abyss.engine.gui.widgets.OLEDStatusScreen;
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
    private final OLEDStatusScreen status; //
    private final GUIButtonsPane buttons = new GUIButtonsPane();
    
    public GameControlsPane( GameState gs, double width ) {
        gs.addListenter(this);
        
        status = new OLEDStatusScreen(gs);
        
        setPrefWidth(width);
        setPadding(new Insets(6));
        
        buttons.setMoney("00000000");
        buttons.getTerminalButton().showHighlight(false);
        getChildren().addAll(decoBox, buttons, status);
        
        // Respond to button clicks
        buttons.getInventoryButton().setOnMouseClicked((MouseEvent t) -> {
            LOGGER.log(Level.FINER, "User clicked Inventory button.");
            gs.toggleShowing(GameState.Display.INVENTORY);
        });
        buttons.getChipButton().setOnMouseClicked((t) -> {
            LOGGER.log(Level.FINER, "User clicked Chip button.");
            gs.toggleShowing(GameState.Display.CHIPS);
        });
        buttons.getRomButton().setOnMouseClicked((t) -> {
            LOGGER.log(Level.FINER, "User clicked ROM button.");
        });
        buttons.getTerminalButton().setOnMouseClicked((t) -> {
            LOGGER.log(Level.FINER, "User clicked Terminal button.");
            gs.toggleShowing(GameState.Display.TERMINAL);
        });
        buttons.getMatrixButton().setOnMouseClicked((t) -> {
            LOGGER.log(Level.FINER, "User clicked Matrix button.");
            gs.toggleShowing(GameState.Display.MATRIX);
        });
        buttons.getSaveButton().setOnMouseClicked((t) -> {
            LOGGER.log(Level.FINER, "User clicked Save button.");
            gs.quickSave();
        });
        buttons.getSettingsButton().setOnMouseClicked((t) -> {
            LOGGER.log(Level.FINER, "User clicked Debug button.");
            gs.toggleDebugShowing();
        });
        buttons.getPowerButton().setOnMouseClicked((t) -> {
            LOGGER.log(Level.FINER, "User clicked Power button.");
        });
    }

    @Override
    public void gameStateVignetteChanged(GameState gs) {
    }

    @Override
    public void gameStatePropertyChanged(GameState gs, String propKey) {
        switch( propKey ) {
            case Player.MONEY_KEY -> buttons.setMoney(gs.getProperty(propKey));
        }
    }

    @Override
    public void gameStateShowDebug(GameState gs, boolean state) {}

    @Override
    public void gameStateTerminalChanged(GameState gs, BBSTerminal term) {}
    
    /**
     * Highlight some buttons when their content is displayed.
     * 
     * @param gs the game state
     * @param d thing that is displayed currently
     */
    @Override
    public void gameStateDisplayChanged(GameState gs, GameState.Display d) {
        // Configure button enabled/disbled states.
        buttons.clearHighlights();
        buttons.greyAll(false);
        
        Button b = null;
        switch (d) {
            case CHIPS:
                b = buttons.getChipButton();
                buttons.setEnableSoloButton(b);
                b.showHighlight(true);
                break;
            case INVENTORY:
                b = buttons.getInventoryButton();
                buttons.setEnableSoloButton(b);
                b.showHighlight(true);
                break;
            case MATRIX:
                b = buttons.getMatrixButton();
                buttons.setEnableSoloButton(b);
                b.showHighlight(true);
                break;
            case TERMINAL:
                b = buttons.getTerminalButton();
                buttons.setEnableSoloButton(b);
                b.showHighlight(true);
                break;

            case SPLASH:
            case VIGNETTE:
                buttons.getChipButton().setDisable(false);
                buttons.getInventoryButton().setDisable(false);
                buttons.getRomButton().setDisable(false);
                buttons.getTerminalButton().setDisable(false);
                buttons.getMatrixButton().setDisable(false);
                buttons.getSaveButton().setDisable(false);
                buttons.getSettingsButton().setDisable(false);
                break;
        }
    }

    @Override
    public void gameStateMatrixSiteChanged(GameState gs, int newAddr) {}
    
}
