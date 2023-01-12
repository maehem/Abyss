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

import static com.maehem.flatlinejack.engine.gui.bbs.BBSTerminal.FONT;

import com.maehem.flatlinejack.content.sites.SiteHeader;
import com.maehem.flatlinejack.engine.BulletinMessage;
import com.maehem.flatlinejack.engine.GameState;
import java.util.ArrayList;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author mark
 */
public class BBSBulletinBoard extends BBSTerminal {
    private static final int NUM_ITEMS = 9;
    private static final String PREV_LABEL = "\u25c4\u25c4 PREVIOUS";
    private static final String NEXT_LABEL = "MORE \u25ba\u25ba";
    private static final String DONE_LABEL = "DONE";
    
    private int currentIndex = 0;
    ArrayList<BBSText> menuItems = new ArrayList<>();
    
    private final GameState gameState;
    private final BBSTerminal parent;
    
    public BBSBulletinBoard(GameState gs, BBSTerminal parent) {
        super(gs);
        this.gameState = gs;
        this.parent = parent;
        
        setHeader(new BBSHeader(FONT, SiteHeader.BULLETIN_BD));
        updateContent(gs);
        setFooter(new BBSText(FONT,
                  "Flatline Jack                    "
                + "                   Bulletin Board"
        ));
    }
    
    @Override
    public final void updateContent(GameState gs) {
        int index=currentIndex;
        menuItems.clear();
        menuItems.add( new BBSSimpleMenuItem(FONT,"   DATE                   FROM   SUBJECT" ));
        ArrayList<BulletinMessage> messageItems = new ArrayList<>();
        for ( BulletinMessage bm : gs.getMessages()) {
            if ( bm.canShow() ) {
                messageItems.add(bm);
            }
        }
        int j=0;
        while ( j < NUM_ITEMS ) {
            try {
                BulletinMessage ns = messageItems.get(index+j);
                menuItems.add(new BBSBulletinMenuItem(FONT, ns, gs, this ));
                j++;
            } catch (IndexOutOfBoundsException ex ) {
                break;
            }
        }
        boolean hasMore = false;
        if ( j == NUM_ITEMS ) {
            try {
                messageItems.get(index+j);
                hasMore = true;
            } catch (IndexOutOfBoundsException ex ) {
            }
        }

        BBSGotoButton prevNode = new BBSGotoButton(FONT, PREV_LABEL);
        if ( index >= NUM_ITEMS ) {
            prevNode.setOnMouseClicked((t) -> {
                currentIndex -= NUM_ITEMS;
                if ( currentIndex < 0 ) {
                    currentIndex = 0;
                }
                updateContent(gs);
            });
        } else {
            prevNode.setEnabled(false);
        }
        BBSGotoButton nextNode = new BBSGotoButton(FONT, NEXT_LABEL);
        if ( hasMore  ) {
            nextNode.setOnMouseClicked((t) -> {
                currentIndex += NUM_ITEMS;
                if ( currentIndex > gs.getNews().size()-1 ) {
                    currentIndex = gs.getNews().size()-1;
                }
                updateContent(gs);
            });
        } else {
            nextNode.setEnabled(false);
        }
        BBSGotoButton doneButton = new BBSGotoButton(FONT, DONE_LABEL, gs, parent);
        HBox navButtons = new HBox(prevNode, doneButton, nextNode );
        navButtons.setSpacing(20);
        
        VBox content = new VBox();
        content.getChildren().add( new BBSSimpleMenu(FONT, menuItems));
        content.getChildren().add(new BBSText(FONT, " "));
        content.getChildren().add( BBSTerminal.centeredNode(navButtons) );
                
        setBody(BBSTerminal.centeredNode(content));        
    }

}
