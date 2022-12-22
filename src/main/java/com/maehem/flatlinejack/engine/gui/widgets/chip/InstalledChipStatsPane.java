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
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Appears the installed chips in the Chips Installation screen where chips 
 * are selectively placed into the neck of the player.
 * 
 * @author mark
 */
public class InstalledChipStatsPane extends HBox {
    private static final int TEXT_W = 226;
    private static final int ICON_D = 64;
    
    public InstalledChipStatsPane( /* SkillChipThing chip */ ) {
        setBorder(new Border(new BorderStroke(
                Color.LIGHTCYAN, 
                BorderStrokeStyle.SOLID, 
                new CornerRadii(10),
                new BorderWidths(2))));
        //setPadding(new Insets(2));
        setFillHeight(true);
        
        // Title, Short Desc.  Image
        Text title = new Text("Title Text ABCD123");
        title.setFont(new Font(18));
        title.setFill(Color.GREEN);
        TextFlow textFlow = new TextFlow(title);
        textFlow.setPadding(new Insets(0,0,0,8));
        //getChildren().add(title);
        
//        Pane separator = new Pane();
//        separator.setPrefSize(10, 10);
//        HBox.setHgrow(separator, Priority.ALWAYS);
        Pane iconPane = new Pane();
        iconPane.setPrefSize(ICON_D , ICON_D);
        iconPane.setMaxSize(ICON_D, ICON_D);
        iconPane.setBackground(new Background(new BackgroundFill(
                Color.GRAY, 
                new CornerRadii(10), 
                Insets.EMPTY)));
        //HBox topArea = new HBox( title, separator, iconPane);
        //setTop(topArea);
        
        // Long Desc.
//        Text desc = new Text(
//            "Skill Chip description. This is a long description of what "
//                + "the skill chip does."
//        );
//        desc.setWrappingWidth(TEXT_W);
//        desc.setTextAlignment(TextAlignment.JUSTIFY);
//        
//        desc.setFill(Color.BISQUE);
        
//        // Buffs (1-4)
//        HBox buffBar = new HBox(
//                makeBuffPane("XYZC", "123", 81),
//                makeBuffPane("TRSD", "88", 61),
//                makeBuffPane("JIBN", "634", 41),
//                makeBuffPane("LEJC", "23", 11)
//        );
//        buffBar.setSpacing(3);
//        buffBar.setPadding(new Insets(8,0,0,0));
//        //setCenter(desc);
//        //setRight(iconPane);
//        setBottom(buffBar);
        
        GridPane buffPane = new GridPane();
        buffPane.addRow(0, 
                makeBuffPane("XYZC", "123", 10),
                makeBuffPane("TRSD", "88", 50)
        );
        buffPane.addRow(1, 
                makeBuffPane("HJJK", "275", 70),
                makeBuffPane("MUBN", "63", 91)
        );
        buffPane.setHgap(2);
        buffPane.setVgap(2);
        buffPane.setPadding(new Insets(0,2,2,8));
        //buffPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        //GridPane.setHgrow(buffPane, Priority.ALWAYS);
        //GridPane.setVgrow(buffPane, Priority.ALWAYS);
        
        VBox leftArea = new VBox(textFlow,buffPane);
        
        //HBox.setHgrow(buffPane, Priority.ALWAYS);
        HBox.setHgrow(leftArea, Priority.ALWAYS);
        getChildren().addAll(leftArea, iconPane);
    }
    
    private HBox makeBuffPane( String name, String val, int condition ) {
        HBox buff = new HBox();
        buff.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        buff.setPrefSize(80    , 60);
        
        buff.setFillHeight(true);
        //GridPane.setHgrow(buff, Priority.ALWAYS);
        //buff.setPrefSize(TEXT_W/4, 16);
        buff.setAlignment(Pos.CENTER);
        Color condColor;
        if ( condition > 80 ) {
            condColor = Color.LIGHTGREEN;
        } else if ( condition > 60 ) {
            condColor = Color.LIGHTYELLOW;
        } else if ( condition > 40 ) {
            condColor = Color.LIGHTSALMON;
        } else if ( condition > 10 ) {
            condColor = Color.LIGHTCORAL;
        } else {
            condColor = Color.DARKGREY.darker();
        }
        
        buff.setBackground(new Background(new BackgroundFill(
                condColor, 
                new CornerRadii(3), 
                new Insets(1)))
        );
        double fontSize = 14;
        String family = new Font(fontSize).getFamily();
        Label nameLbl = new Label(name + ": ");
        nameLbl.setFont(Font.font(family, FontWeight.BOLD, fontSize));
        Label valLbl = new Label( val);
        valLbl.setFont(Font.font(family, FontWeight.NORMAL, fontSize));
        buff.getChildren().addAll(nameLbl, valLbl);
        
        return buff;
    }
    
}
