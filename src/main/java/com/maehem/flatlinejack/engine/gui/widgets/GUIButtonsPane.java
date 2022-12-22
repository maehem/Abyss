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
package com.maehem.flatlinejack.engine.gui.widgets;

import java.io.InputStream;
import javafx.geometry.Pos;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author mark
 */
public class GUIButtonsPane extends DecoBox {
    private final static double BUTTON_SIZE = 48;

    private final String INV_ICON_PATH = "/icons/inventory-icon.png";
    private final String CHIP_ICON_PATH = "/icons/microchip-icon.png";
    private final String ROM_ICON_PATH = "/icons/knowledge-icon.png";
    private final String POWER_ICON_PATH = "/icons/quit-icon.png";
    
    private final DSEG7Display money;
    private final Button inventoryButton;
    private final Button chipButton;
    private final Button romButton;
    private final Button powerButton;
    
    public GUIButtonsPane() {
        setPrefWidth(300);
        
        money = new DSEG7Display(0, 0, 200, 30, '$', "1234567890");
        FlowPane moneyPane = new FlowPane(money);
        moneyPane.setAlignment(Pos.CENTER);
        
        inventoryButton = new Button(BUTTON_SIZE, BUTTON_SIZE, getStream(INV_ICON_PATH));
        chipButton      = new Button(BUTTON_SIZE, BUTTON_SIZE, getStream(CHIP_ICON_PATH));
        romButton       = new Button(BUTTON_SIZE, BUTTON_SIZE, getStream(ROM_ICON_PATH));
        powerButton     = new Button(BUTTON_SIZE, BUTTON_SIZE, getStream(POWER_ICON_PATH));
        
        FlowPane buttons = new FlowPane(inventoryButton,  chipButton, romButton, powerButton);
        buttons.setHgap(4.0);
        buttons.setAlignment(Pos.CENTER);
        
        VBox content = new VBox(moneyPane,buttons);
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
    
    public Button getKnowledgeButton() {
        return romButton;
    }
    
    public Button getPowerButton() {
        return powerButton;
    }
}
