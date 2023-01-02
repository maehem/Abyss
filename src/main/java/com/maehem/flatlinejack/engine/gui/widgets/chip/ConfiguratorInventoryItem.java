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
package com.maehem.flatlinejack.engine.gui.widgets.chip;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;


/**
 *
 * @author mark
 */
public class ConfiguratorInventoryItem extends HBox {

    private final double GLYPH_DIM = 30;
    
    public ConfiguratorInventoryItem() {
        setBackground(new Background(new BackgroundFill(
                Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY
        )));
        //setPadding(new Insets(1));
        setBorder(new Border(new BorderStroke(
                Color.RED, 
                BorderStrokeStyle.SOLID, 
                new CornerRadii(4), 
                new BorderWidths(2)
        )));
        setSpacing(10);
        
        StackPane slotId = new StackPane();
        slotId.setPrefSize(GLYPH_DIM, GLYPH_DIM);
        slotId.setMinSize(GLYPH_DIM,GLYPH_DIM);
        slotId.setMaxSize(GLYPH_DIM,GLYPH_DIM);
        slotId.setBackground(new Background(new BackgroundFill(
                new Color(0.25,0.25,0.25,1.0), 
                CornerRadii.EMPTY, 
                Insets.EMPTY)));
        slotId.setBorder(new Border(new BorderStroke(
                new Color(0.16,0.16,0.16,1.0), 
                BorderStrokeStyle.SOLID, 
                new CornerRadii(0,5,5,0,false), 
                new BorderWidths(0,6,0,0)
        )));
        Text slotN = new Text("0");
        slotN.setFill(new Color(0.76,0.76,0.76,1.0));
        slotN.setFont(new Font(20));
        slotId.getChildren().add(slotN);
        
        Text titleText = new Text("Title Text ABCD");
        titleText.setFill(Color.GREEN);
        titleText.setFont(new Font(24));
        TextFlow titleFlow = new TextFlow(titleText);
        
        Pane itemIcon = new Pane();
        itemIcon.setPrefSize(GLYPH_DIM, GLYPH_DIM);
        itemIcon.setMinSize(GLYPH_DIM, GLYPH_DIM);
        itemIcon.setMaxSize(GLYPH_DIM, GLYPH_DIM);
        itemIcon.setBackground(new Background(new BackgroundFill(
                Color.DARKOLIVEGREEN, 
                new CornerRadii(5), 
                Insets.EMPTY)));
        itemIcon.setBorder(new Border(new BorderStroke(
                new Color(0.16,0.16,0.16,1.0), 
                BorderStrokeStyle.SOLID, 
                new CornerRadii(0,5,5,0,false), 
                new BorderWidths(0,0,0,8)
        )));
        
//        setMargin(slotId, new Insets(4));
//        setMargin(itemIcon, new Insets(4));
        
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        getChildren().addAll(slotId, titleFlow, spacer, itemIcon);
    }
    
}