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
package com.maehem.abyss.engine.gui.widgets;

import java.io.InputStream;
import javafx.geometry.Pos;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author mark
 */
public class GUIButtonsPane extends DecoBox {
    private final static double BUTTON_SIZE = 48;
    private final static double MONEY_WIDGET_SIZE = 30;

    private final String INV_ICON_PATH = "/icons/inventory-icon.png";
    private final String CHIP_ICON_PATH = "/icons/microchip-icon.png";
    private final String ROM_ICON_PATH = "/icons/chip-head.png";
    private final String TERM_ICON_PATH = "/icons/command-line.png";
    private final String MATRIX_ICON_PATH = "/icons/jack-icon.png";
    private final String SAVE_ICON_PATH = "/icons/save-icon.png";
    private final String DEBUG_WINDOW_ICON_PATH = "/icons/ladybug-icon.png";
    private final String POWER_ICON_PATH = "/icons/quit-icon.png";
    
    private final DSEG7Display money;
    private final Button inventoryButton;
    private final Button chipButton;
    private final Button romButton;
    private final Button terminalButton;
    private final Button matrixButton;
    private final Button saveButton;
    private final Button settingsButton;
    private final Button powerButton;
    
    public GUIButtonsPane() {
        setPrefWidth(300);
        
        money = new DSEG7Display(0, 0, 200, MONEY_WIDGET_SIZE, '$', "1234567890");
        FlowPane moneyPane = new FlowPane(money);
        moneyPane.setAlignment(Pos.CENTER);
        
        inventoryButton = new Button(BUTTON_SIZE, BUTTON_SIZE, getStream(INV_ICON_PATH));
        chipButton      = new Button(BUTTON_SIZE, BUTTON_SIZE, getStream(CHIP_ICON_PATH));
        romButton       = new Button(BUTTON_SIZE, BUTTON_SIZE, getStream(ROM_ICON_PATH));
        terminalButton  = new Button(BUTTON_SIZE, BUTTON_SIZE, getStream(TERM_ICON_PATH));
        matrixButton    = new Button(BUTTON_SIZE, BUTTON_SIZE, getStream(MATRIX_ICON_PATH));
        saveButton      = new Button(BUTTON_SIZE, BUTTON_SIZE, getStream(SAVE_ICON_PATH));
        settingsButton  = new Button(BUTTON_SIZE, BUTTON_SIZE, getStream(DEBUG_WINDOW_ICON_PATH));
        powerButton     = new Button(BUTTON_SIZE, BUTTON_SIZE, getStream(POWER_ICON_PATH));
        
        FlowPane buttons = new FlowPane(
                inventoryButton,  chipButton, romButton, 
                terminalButton, matrixButton,saveButton, 
                settingsButton, powerButton);
        buttons.setHgap(4.0);
        buttons.setVgap(4.0);
        buttons.setPrefWrapLength((BUTTON_SIZE+4.0)*4);
        buttons.setAlignment(Pos.CENTER_LEFT);
        HBox buttonBox = new HBox(buttons);
        buttonBox.setAlignment(Pos.CENTER);
        VBox content = new VBox(moneyPane,buttonBox);
        content.setSpacing(16);
        getChildren().add(content);
    }
    
    private InputStream getStream(String path) {
        return getClass().getResourceAsStream(path);
    }
    
    public void setMoney( String value ) {
        money.setText(value);
    }
    
    public Button getInventoryButton() {
        return inventoryButton;
    }
    
    public Button getChipButton() {
        return chipButton;
    }
    
    public Button getRomButton() {
        return romButton;
    }
    
    public Button getTerminalButton() {
        return terminalButton;
    }
    
    public Button getSaveButton() {
        return saveButton;
    }

    public Button getSettingsButton() {
        return settingsButton;
    }
    
    public Button getMatrixButton() {
        return  matrixButton;
    }
    
    public Button getPowerButton() {
        return powerButton;
    }
    
    public void setEnableSoloButton( Button b ) {
        greyAll(true);
        
        if ( b != null ) {
            b.setGreyout(false);
        }
    }
    
    public void greyAll( boolean grey) {
        inventoryButton.setGreyout(grey);
        chipButton.setGreyout(grey);
        romButton.setGreyout(grey);
        terminalButton.setGreyout(grey);
        matrixButton.setGreyout(grey);
        saveButton.setGreyout(grey);
        powerButton.setGreyout(grey);
        settingsButton.setGreyout(grey);
    }
    
    public void clearHighlights() {
        inventoryButton.showHighlight(false);
        chipButton.showHighlight(false);
        romButton.showHighlight(false);
        terminalButton.showHighlight(false);
        matrixButton.showHighlight(false);
        saveButton.showHighlight(false);
        powerButton.showHighlight(false);
        settingsButton.showHighlight(false);
        
    }
}
