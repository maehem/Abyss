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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/**
 *
 * @author mark
 */
public class ChipDetailsNotUsed extends BorderPane {

    public ChipDetailsNotUsed() {
        //setMaxWidth(Double.MAX_VALUE);
        setBackground(new Background(new BackgroundFill(
                Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY
        )));
        //setPadding(new Insets(4));
//        setBorder(new Border(new BorderStroke(
//                Color.RED, 
//                BorderStrokeStyle.SOLID, 
//                new CornerRadii(2), 
//                new BorderWidths(8)
//        )));
        StackPane slotId = new StackPane();
        slotId.setPrefSize(36, 36);
        slotId.setMinSize(36,36);
        slotId.setMaxSize(36,36);
        slotId.setBackground(new Background(new BackgroundFill(
                Color.BLACK.brighter(), 
                CornerRadii.EMPTY, 
                Insets.EMPTY)));
        slotId.setBorder(new Border(new BorderStroke(
                Color.DARKGREY.darker(), 
                BorderStrokeStyle.SOLID, 
                CornerRadii.EMPTY, new BorderWidths(4)
        )));
        Text slotN = new Text("8");
        slotN.setFill(Color.GRAY);
        slotN.setFont(new Font(26));
        slotId.getChildren().add(slotN);
        
        VBox centerArea = new VBox();
        Text titleText = new Text("Title Text ABCD");
        titleText.setFill(Color.LIMEGREEN);
        titleText.setFont(new Font(22));
        Text descText = new Text(
                "This is a long description of the item.  It has many"
                + "words describing its parameters and more stuff that"
                + "you need to know about the item.  I like ice cream!"
        );
        descText.setFill(Color.LIGHTGRAY);
        //descText.setTextAlignment(TextAlignment.JUSTIFY);
        //descText.setWrappingWidth(320);
        
        TextFlow textFlow = new TextFlow(descText);
        textFlow.setTextAlignment(TextAlignment.JUSTIFY);
        textFlow.setMaxWidth(Double.MAX_VALUE);
        centerArea.getChildren().addAll(titleText, textFlow);
        
        Pane itemIcon = new Pane();
        itemIcon.setPrefSize(72, 72);
        itemIcon.setMinSize(72, 72);
        itemIcon.setMaxSize(72, 72);
        itemIcon.setBackground(new Background(new BackgroundFill(
                Color.DARKOLIVEGREEN, 
                new CornerRadii(5), 
                Insets.EMPTY)));
        itemIcon.setBorder(new Border(new BorderStroke(
                Color.DARKGREY.darker(), 
                BorderStrokeStyle.SOLID, 
                new CornerRadii(5), new BorderWidths(4)
        )));
        
        Text itemCondition = new Text("    ABCD: 234    HJED:893    NIFS: 34    KOHD: 97");
        itemCondition.setTextAlignment(TextAlignment.JUSTIFY);
        itemCondition.setFont(new Font(20));
        itemCondition.setFill(Color.CORAL);
        
        setMargin(slotId, new Insets(4));
        setMargin(itemIcon, new Insets(4));
        setLeft(slotId);
        setCenter(centerArea);
        setRight(itemIcon);
        setBottom(itemCondition);
    }
    
}
