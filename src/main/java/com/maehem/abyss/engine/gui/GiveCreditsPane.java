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
import com.maehem.abyss.engine.Player;
import com.maehem.abyss.engine.view.ViewPane;
import java.util.logging.Level;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author mark
 */
public class GiveCreditsPane extends BorderPane {

    private final double TEXT_SIZE = 30.0;
    
    private final GameState gameState;
    TextField giveAmountField = new TextField();
    //private String successKey;
    //private String successValue;
    private final Text titleText;
    private EventHandler successAction;
    
    public GiveCreditsPane( GameState gs ) {
        this.gameState = gs;
        this.titleText = paneText("");
        titleText.setFont(Font.font(TEXT_SIZE * 1.1));
        HBox titleArea = new HBox(titleText);
        titleArea.setPadding(new Insets(TEXT_SIZE*0.2));
        setTop(titleArea);
        
        GridPane gp = new GridPane();
        
        gp.add(paneText("Credits:"), 0, 0, 1, 1);
        gp.add(paneText(String.valueOf(gs.getPlayer().getMoney())), 1, 0, 1, 1);
        gp.add(paneText("Give:"), 0, 1, 1, 1);
        gp.add(giveAmountField, 1, 1, 1, 1);
        giveAmountField.setFont(Font.font(TEXT_SIZE));
        
        setCenter(gp);
        
        Button okButton = new Button("Give");
        okButton.setFont(Font.font(TEXT_SIZE));
        Button cancelButton = new Button("Cancel");
        cancelButton.setFont(Font.font(TEXT_SIZE));
        
        HBox buttons = new HBox(okButton, cancelButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setSpacing(10);
        
        setBottom(buttons);
        
        BorderPane.setMargin(gp, new Insets(30));
        BorderPane.setMargin(buttons, new Insets(30));
        setBackground(new Background(new BackgroundFill(Color.DARKGREY, new CornerRadii(20), Insets.EMPTY)));
        setEffect(new DropShadow());
        setPrefSize(ViewPane.WIDTH*0.5, ViewPane.HEIGHT*0.5);
        setLayoutX(ViewPane.WIDTH*0.25);
        setLayoutY(ViewPane.HEIGHT*0.25);
        
        // TODO bind value field proper value to okButton enabled.
        
        okButton.setOnAction((t) -> {
            LOGGER.log(Level.INFO, "Give amount: " + giveAmountField.getText());
            try {
                if (successAction == null) {
                    LOGGER.warning("GiveCreditsPane: Success action is null!");
                } else {

                    Integer amount = Integer.valueOf(giveAmountField.getText());
                    gameState.getPlayer().addMoney(-amount);
                    successAction.handle(t);
                    //gameState.setProperty(successKey, successValue);

                    successAction = null;
                    titleText.setText("");
                    setVisible(false);
                }
            } catch (NumberFormatException ex) {
                // Nothing happens.
            }
        });

        cancelButton.setOnAction((t) -> {
            successAction = null;
            titleText.setText("");
            setVisible(false);
        });

    }
    
    /**
     * 
     * @param amount to deduct from player account.
     * @param key to set in GameState upon success
     * @param val value for key to setin GameState upon success.
     */
    public void show( int amount, String titleString, EventHandler successAction ) {
        giveAmountField.setText(String.valueOf(amount));
        titleText.setText(titleString);
        this.successAction = successAction;
        //successKey = key;
        //successValue = val;
        
        setVisible(true);
    }
    
    private Text paneText( String text ) {
        Text pt = new Text(text);
        pt.setFont(Font.font(TEXT_SIZE));
        
        return pt;
    }
}
