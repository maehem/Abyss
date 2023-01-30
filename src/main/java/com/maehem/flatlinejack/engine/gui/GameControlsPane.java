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

import static com.maehem.flatlinejack.Engine.LOGGER;

import com.maehem.flatlinejack.engine.GameState;
import static com.maehem.flatlinejack.engine.GameState.Display.CHIPS;
import com.maehem.flatlinejack.engine.GameStateListener;
import com.maehem.flatlinejack.engine.Player;
import com.maehem.flatlinejack.engine.gui.bbs.BBSTerminal;
import com.maehem.flatlinejack.engine.gui.widgets.Button;
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
        setPadding(new Insets(6));
        
        buttons.setMoney("00000000");
        buttons.getTerminalButton().showHighlight(false);
        getChildren().addAll(decoBox, buttons, status);
        
        // Respond to button clicks
        buttons.getInventoryButton().setOnMouseClicked((MouseEvent t) -> {
            LOGGER.log(Level.INFO, "User clicked Inventory button.");
            gs.toggleShowing(GameState.Display.INVENTORY);
        });
        buttons.getChipButton().setOnMouseClicked((t) -> {
            LOGGER.log(Level.INFO, "User clicked Chip button.");
            gs.toggleShowing(GameState.Display.CHIPS);
        });
        buttons.getRomButton().setOnMouseClicked((t) -> {
            LOGGER.log(Level.INFO, "User clicked ROM button.");
        });
        buttons.getTerminalButton().setOnMouseClicked((t) -> {
            LOGGER.log(Level.INFO, "User clicked Terminal button.");
            gs.toggleShowing(GameState.Display.TERMINAL);
        });
        buttons.getMatrixButton().setOnMouseClicked((t) -> {
            LOGGER.log(Level.INFO, "User clicked Matrix button.");
            gs.toggleShowing(GameState.Display.MATRIX);
        });
        buttons.getSaveButton().setOnMouseClicked((t) -> {
            LOGGER.log(Level.INFO, "User clicked Save button.");
            gs.quickSave();
        });
        buttons.getSettingsButton().setOnMouseClicked((t) -> {
            LOGGER.log(Level.INFO, "User clicked Debug button.");
            gs.toggleDebugShowing();
        });
        buttons.getPowerButton().setOnMouseClicked((t) -> {
            LOGGER.log(Level.INFO, "User clicked Power button.");
        });
    }

    
    @Override
    public void gameStateVignetteChanged(GameState gs) {
        //buttons.setMoney(String.valueOf(gs.getPlayer().getMoney()));
    }

    @Override
    public void gameStatePropertyChanged(GameState gs, String propKey) {
        switch( propKey ) {
            case Player.MONEY_KEY:
                buttons.setMoney(gs.getProperty(propKey));
        }
    }

//    @Override
//    public void gameStateShowInventory(GameState gs, boolean state) {
//        clearButtonHighlights();
//        buttons.getInventoryButton().showHighlight(
//                gs.inventoryShowing()
//        );
//    }

//    @Override
//    public void gameStateShowChips(GameState gs, boolean state) {
//        clearButtonHighlights();
//        buttons.getChipButton().showHighlight(
//                gs.chipsShowing()
//        );
//        //TODO: Highlight the chips button.
//    }

    @Override
    public void gameStateShowDebug(GameState gs, boolean state) {}

    @Override
    public void gameStateTerminalChanged(GameState gs, BBSTerminal term) {}
    
//    private void clearButtonHighlights() {
//        buttons.getInventoryButton().showHighlight(false);
//        buttons.getChipButton().showHighlight(false);
//        buttons.getRomButton().showHighlight(false);
//        buttons.getTerminalButton().showHighlight(false);
//        buttons.getSaveButton().showHighlight(false);
//        buttons.getPowerButton().showHighlight(false);
//    }

//    @Override
//    public void gameStateShowTerminal(GameState gs, boolean showTerminal) {
//        buttons.getTerminalButton().showHighlight( showTerminal );
//    }

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
    
}
