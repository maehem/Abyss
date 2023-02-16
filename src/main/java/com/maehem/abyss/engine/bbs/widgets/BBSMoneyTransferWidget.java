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
package com.maehem.abyss.engine.bbs.widgets;

import com.maehem.abyss.engine.GameState;
import com.maehem.abyss.engine.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 *
 * @author mark
 */
public class BBSMoneyTransferWidget extends VBox {

    public BBSMoneyTransferWidget(Font f, GameState gs) {
        Player player = gs.getPlayer();
        setSpacing(3);
        String fillHex = BBSText.toHex(BBSText.FILL_COLOR);
        BBSLabelValue balance = new BBSLabelValue(f, "Account Balance", 
                String.valueOf(player.getBankMoney()), 276
        );
        BBSLabelValue pocket  = new BBSLabelValue(f, "    Pocket Chip", 
                String.valueOf(player.getMoney()), 276
        );
        
        StackPane upButton = new StackPane(new BBSText(f, "\u0018")); // Up arrow
        upButton.setPadding(new Insets(8,12,8,12));
        upButton.setBorder(new Border(new BorderStroke(
                BBSText.FILL_COLOR, 
                BorderStrokeStyle.SOLID, 
                CornerRadii.EMPTY, 
                new BorderWidths(3)
        )));
        
        StackPane dnButton = new StackPane(new BBSText(f, "\u0019")); // Down arrow
        dnButton.setPadding(new Insets(8,12,8,12));
        dnButton.setBorder(new Border(new BorderStroke(
                BBSText.FILL_COLOR, 
                BorderStrokeStyle.SOLID, 
                CornerRadii.EMPTY, 
                new BorderWidths(3)
        )));
        
                
        TextField txfrAmount = new TextField("0");
        txfrAmount.setFont(f);
        txfrAmount.setAlignment(Pos.CENTER);
        txfrAmount.setPrefColumnCount(10);
        txfrAmount.setBackground(Background.EMPTY);
        txfrAmount.setBorder(new Border(new BorderStroke(
                BBSText.FILL_COLOR, 
                BorderStrokeStyle.SOLID, 
                CornerRadii.EMPTY, 
                new BorderWidths(3)
        )));
        txfrAmount.setStyle("-fx-text-fill: " + fillHex + ";");
        txfrAmount.setPrefColumnCount(10);

        //getChildren().add(new BBSLabelValue(f, "       Transfer", "0000", 200));
        HBox lblText = new HBox(new BBSText(f, "           Transfer" + ":"));
        lblText.setPadding(new Insets(8,12,0,12));
        lblText.setAlignment(Pos.CENTER);
        
        HBox xferWidget = new HBox(lblText,dnButton, txfrAmount, upButton);
        xferWidget.setSpacing(3);
        
        getChildren().addAll(balance, xferWidget, pocket);

        upButton.setOnMouseClicked((t) -> {
            int amt = Integer.parseInt(txfrAmount.getText() );
            if ( amt > 0 && player.getMoney() >= amt ) {
                player.addMoney(-amt);
                player.addBankMoney(amt);
                pocket.setValue( String.valueOf(player.getMoney()) );
                balance.setValue( String.valueOf(player.getBankMoney()));
            }
        });
        dnButton.setOnMouseClicked((t) -> {
            int amt = Integer.parseInt(txfrAmount.getText() );
            if ( amt > 0 && player.getBankMoney() >= amt ) {
                player.addMoney(amt);
                player.addBankMoney(-amt);
                pocket.setValue( String.valueOf(player.getMoney()) );
                balance.setValue( String.valueOf(player.getBankMoney()));
            }
        });



    }
    
//    private static String toHex(Color c) {
//        int red = (int) (c.getRed() * 0xFF);
//        String rr = Integer.toHexString(red);
// 
//        int green = (int) (c.getGreen() * 0xFF);
//        String gg = Integer.toHexString(green);
//
//        int blue = (int) (c.getBlue() * 0xFF);
//        String bb = Integer.toHexString(blue);
//
//        return "#" + rr + gg + bb;
//    }
    
}
