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

import com.maehem.abyss.engine.bbs.BBSTerminal;
import com.maehem.abyss.engine.BulletinMessage;
import com.maehem.abyss.engine.GameState;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author mark
 */
public class BBSBulletinReader extends BBSReader {
    private static final String REPLY_BTN_TXT = "REPLY";
    private static final String BACK_BTN_TXT = "BACK";
    
    public BBSBulletinReader(GameState gs, BulletinMessage bm, BBSTerminal returnTo) {
        super(gs);
                
        setHeader(new BBSText(FONT, 
                  "                         "
                + "B U L L E T I N    B O A R D"
        ));
        BBSTextFlow text = new BBSTextFlow();
        text.getChildren().add(new BBSText(FONT, "\n"));
        text.getChildren().add(new BBSText(FONT, "   Date: " + bm.getDate() + "\n"));
        text.getChildren().add(new BBSText(FONT, "     To: " + bm.getTo() + "\n"));
        text.getChildren().add(new BBSText(FONT, "   From: " + bm.getFrom() + "\n"));
        text.getChildren().add(new BBSText(FONT, "Subject: " + bm.getTitle() + "\n"));
        text.getChildren().add(new BBSText(FONT, "\n"));
        text.getChildren().add(new BBSText(FONT, bm.getBody()));
        text.setTextAlignment(TextAlignment.JUSTIFY);
        
        VBox content = new VBox(text);
        if ( bm.canReply() && !bm.hasReplied() ) {
            // Show a reply button
            BBSGotoButton replyButton = new BBSGotoButton(FONT, REPLY_BTN_TXT );
            replyButton.setOnMouseClicked((t) -> {
                // Go to reply screen and reurn to here.
                gs.setCurrentTerminal(new  BBSBulletinReply(gs, bm, this));
            });
            content.getChildren().add(new BBSText(FONT, "\n"));
            content.getChildren().add(centeredNode(replyButton));
            
        }
        setBody(content);        
        setFooter(new BBSGotoButton(FONT, BACK_BTN_TXT, gs, returnTo));
    }
    
}
