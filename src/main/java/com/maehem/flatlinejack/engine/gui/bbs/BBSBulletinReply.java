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
package com.maehem.flatlinejack.engine.gui.bbs;

import com.maehem.flatlinejack.engine.BulletinMessage;
import com.maehem.flatlinejack.engine.GameState;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author mark
 */
public class BBSBulletinReply extends BBSReader {
    private static final String SEND_BTN_TXT = "SEND";
    private static final String CANCEL_BTN_TXT = "CANCEL";
    
    public BBSBulletinReply(GameState gs, BulletinMessage bm, BBSTerminal returnTo) {
        super(gs);
                
        setHeader(new BBSText(FONT, 
                  "                             "
                + "M E S S A G E   R E P L Y"
        ));
        BBSTextFlow text = new BBSTextFlow();
        text.getChildren().add(new BBSText(FONT, "\n"));
        text.getChildren().add(new BBSText(FONT, 
                  "Date: " + gs.getProperty(GameState.PROP_CURRENT_DATE)
                + "     To: " + bm.getFrom() + "\n\n"));
        text.getChildren().add(new BBSText(FONT, "Subject: RE: " + bm.getTitle()));
        //text.getChildren().add(new BBSText(FONT, "Subject: RE: " + bm.getTitle() + "\n"));
        text.getChildren().add(new BBSText(FONT, "\n"));
        
        // Reply field
        TextArea replyField = new TextArea();
        replyField.setBorder(new Border(new BorderStroke(
                BBSText.FILL_COLOR, BorderStrokeStyle.SOLID, 
                CornerRadii.EMPTY, new BorderWidths(3)
        )));
        replyField.setFont(FONT);
        replyField.setStyle( // Some text area things cannot be styled with direct Java calls.
                "-fx-control-inner-background: " + BBSText.toHex(BG_COLOR.darker()) + "; "
              + "-fx-highlight-fill: #00aa00; " 
              + "-fx-highlight-text-fill: #005500; "
              + "-fx-text-fill: " + BBSText.toHex(BBSText.FILL_COLOR) + "; "
        );
        replyField.setPrefHeight(200);
        text.getChildren().add(new BBSText(FONT, bm.getBody(), BBSText.Shade.DARKER));
        text.setTextAlignment(TextAlignment.JUSTIFY);
        
        setBody(new VBox(replyField, text ));

        BBSGotoButton sendButton = new BBSGotoButton(FONT, SEND_BTN_TXT );
        sendButton.setOnMouseClicked((t) -> {
            // Do it
        });
        BBSGotoButton cancelButton = new BBSGotoButton(FONT, CANCEL_BTN_TXT, gs, returnTo);
        
        HBox buttons = new HBox(sendButton, cancelButton );
        buttons.setSpacing(30);
        setFooter(centeredNode(buttons));
    }
    
}
