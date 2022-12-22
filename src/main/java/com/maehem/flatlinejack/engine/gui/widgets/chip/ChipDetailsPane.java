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
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/**
 *
 * @author mark
 */
public class ChipDetailsPane extends HBox {

    private static final String BG_IMAGE_FILE = "/ui/chip-detail.png";
    private static final int ICON_SIZE = 70;
    private final double width;

    public ChipDetailsPane(double w) {
        this.width = w;
        setPadding(new Insets(18, 80, 32, 30));
        
        // Image for neck.
        Image bgImage = new Image( getClass().getResourceAsStream(BG_IMAGE_FILE));
        //setPrefSize(w, w*0.4);
        setPrefSize(w, bgImage.getHeight());
        setBackground(new Background( new BackgroundImage(
                bgImage, 
                BackgroundRepeat.NO_REPEAT, 
                BackgroundRepeat.NO_REPEAT, 
                BackgroundPosition.CENTER, 
                BackgroundSize.DEFAULT
        )));
        
        //setBorder(new Border(new BorderStroke(Color.YELLOWGREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(4))));
        
//        Pane spacer = new Pane();
//        spacer.setMinWidth(100);
//        spacer.setMaxWidth(100);
        // Spacer for bg image detail
        getChildren().addAll(chipDetails() );//, spacer);
    }
    
    public final BorderPane chipDetails() {
        BorderPane thePane = new BorderPane();
        
        //thePane.setMargin(slotId, new Insets(4));

        //setMaxWidth(Double.MAX_VALUE);
        thePane.setBackground(new Background(new BackgroundFill(
                new Color(0.15,0.15,0.15,1.0), 
                new CornerRadii(12), Insets.EMPTY
        )));
        //thePane.setPadding(new Insets(4));
        thePane.setBorder(new Border(new BorderStroke(
                Color.DARKGRAY.darker(), 
                BorderStrokeStyle.SOLID, 
                new CornerRadii(8), 
                new BorderWidths(1)
        )));
        StackPane slotId = new StackPane();
        slotId.setPrefSize(36, 36);
        slotId.setMinSize(36,36);
        slotId.setMaxSize(36,36);
        slotId.setBackground(new Background(new BackgroundFill(
                Color.BLACK.brighter(), 
                new CornerRadii(12, 0, 12, 0, false), 
                Insets.EMPTY)));
        slotId.setBorder(new Border(new BorderStroke(
                Color.DARKGREY.darker(), 
                BorderStrokeStyle.SOLID, 
                new CornerRadii(8, 0, 8, 0, false), 
                new BorderWidths(0,4,4,0)
        )));
        Text slotN = new Text("8");
        slotN.setFill(Color.GRAY);
        slotN.setFont(new Font(26));
        slotId.getChildren().add(slotN);
        
        Text titleText = new Text("Title Text ABCD");
        HBox.setMargin(titleText, new Insets(2,0,0,16));
        titleText.setFill(Color.LIMEGREEN);
        titleText.setFont(new Font(30));
        
        
        VBox centerArea = new VBox();
        centerArea.setPadding(new Insets(0,6,6,6));
        Text descText = new Text(
                "This is a long description of the item.  It has many "
                + "words describing its parameters and more stuff that "
                + "you need to know about the item.  I like ice cream!"
                + "This is more description.  Oh my!!!!"
        );
        descText.setFill(Color.GRAY);
        descText.setFont(new Font(15));
        
        TextFlow textFlow = new TextFlow(descText);
        textFlow.setTextAlignment(TextAlignment.JUSTIFY);
        textFlow.setMaxWidth(Double.MAX_VALUE);
        textFlow.setLineSpacing(-3.4);
        textFlow.setPadding(new Insets(4,0,0,8));
        //BorderPane.setMargin(textFlow, new Insets(20,0,0,8));
        
        //centerArea.getChildren().addAll(titleText, textFlow);
        centerArea.getChildren().add(textFlow);
        
        Pane itemIcon = new Pane();
        //itemIcon.setPadding(new Insets(16, 0, 0, 0));
        itemIcon.setPrefSize(ICON_SIZE, ICON_SIZE);
        itemIcon.setMinSize(ICON_SIZE, ICON_SIZE);
        itemIcon.setMaxSize(ICON_SIZE, ICON_SIZE);
        itemIcon.setBackground(new Background(new BackgroundFill(
                Color.DARKOLIVEGREEN, 
                new CornerRadii(5), 
                Insets.EMPTY)));
        itemIcon.setBorder(new Border(new BorderStroke(
                Color.DARKGREY.darker(), 
                BorderStrokeStyle.SOLID, 
                new CornerRadii(5,0,0,5,false), 
                new BorderWidths(6,0,6,8)
        )));
        //thePane.setMargin(itemIcon, new Insets(0,4,0,0));
        
//        Text itemCondition = new Text(" ABCD: 234    HJED:893    NIFS: 34    KOHD: 97");
//        itemCondition.setTextAlignment(TextAlignment.JUSTIFY);
//        itemCondition.setFont(new Font(18));
//        itemCondition.setFill(Color.CORAL);

        HBox buffBar = new HBox(
                makeBuffPane("XYZC", "123", 81),
                makeBuffPane("TRSD", "88", 61),
                makeBuffPane("JIBN", "634", 41),
                makeBuffPane("LEJC", "23", 11)
        );
        buffBar.setAlignment(Pos.CENTER);
        buffBar.setSpacing(6);
        buffBar.setPadding(new Insets(8,0,0,0));
        
        HBox topArea = new HBox(slotId, titleText);
        thePane.setTop(topArea);
        //thePane.setLeft(slotId);
        thePane.setCenter(centerArea);
        thePane.setRight(itemIcon);
        thePane.setBottom(buffBar);
        
        return thePane;
    }
    
    private HBox makeBuffPane( String name, String val, int condition ) {
        HBox buff = new HBox();

        buff.setPrefSize(width*0.19, 32);
        buff.setAlignment(Pos.CENTER);
        Color condColor;
        if ( condition > 80 ) {
            condColor = Color.GREY;
        } else if ( condition > 60 ) {
            condColor = Color.YELLOW;
        } else if ( condition > 40 ) {
            condColor = Color.ORANGE;
        } else if ( condition > 10 ) {
            condColor = Color.RED;
        } else {
            condColor = Color.DARKGREY.darker();
        }
        
        buff.setBackground(new Background(new BackgroundFill(
                condColor, 
                new CornerRadii(10,10,0,0,false), 
                new Insets(2,0,0,0)))
        );
        double fontSize = 16;
        String family = new Font(fontSize).getFamily();
        Label nameLbl = new Label(name + ": ");
        nameLbl.setFont(Font.font(family, FontWeight.BOLD, fontSize));
        Label valLbl = new Label( val);
        valLbl.setFont(Font.font(family, FontWeight.NORMAL, fontSize));
        buff.getChildren().addAll(nameLbl, valLbl);
        
        return buff;
    }
    
}
