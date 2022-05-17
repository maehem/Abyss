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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;

/**
 *
 * @author mark
 */
public class GUIButtonsPane extends FlowPane {
    private final static double BUTTON_SIZE = 48;

    private final String INV_ICON_PATH = "/icons/inventory-icon.png";
    private final String CHIP_ICON_PATH = "/icons/microchip-icon.png";
    private final String KNOW_ICON_PATH = "/icons/knowledge-icon.png";
    private final String DISK_ICON_PATH = "/icons/quit-icon.png";
    
    private final Button inventoryButton;
    private final Button chipButton;
    private final Button knowledgeButton;
    private final Button diskButton;
    
    public GUIButtonsPane() {
        super( 2,0 );
        
        
        setAlignment(Pos.CENTER);
        setBorder(new Border(new BorderStroke(
                Color.GREY, 
                BorderStrokeStyle.SOLID, 
                new CornerRadii(4.0),
                new BorderWidths(1.0)
        )));
        setPadding(new Insets(4));
        
        DSEG7Display money = new DSEG7Display(0, 0, 200, 30, '$', "1234567890");
        inventoryButton = new Button(BUTTON_SIZE, BUTTON_SIZE, getStream(INV_ICON_PATH));
        chipButton      = new Button(BUTTON_SIZE, BUTTON_SIZE, getStream(CHIP_ICON_PATH));
        knowledgeButton = new Button(BUTTON_SIZE, BUTTON_SIZE, getStream(KNOW_ICON_PATH));
        diskButton      = new Button(BUTTON_SIZE, BUTTON_SIZE, getStream(DISK_ICON_PATH));
        FlowPane buttons = new FlowPane(inventoryButton,  chipButton, knowledgeButton, diskButton);
        buttons.setHgap(2.0);
        buttons.setAlignment(Pos.CENTER);
        
        getChildren().addAll(money,buttons);
    }
    
    private InputStream getStream(String path) {
        return getClass().getResourceAsStream(path);
    }
    
    
}
