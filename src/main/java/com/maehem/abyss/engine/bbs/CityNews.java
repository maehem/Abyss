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
package com.maehem.abyss.engine.bbs;

import static com.maehem.abyss.engine.bbs.BBSTerminal.FONT;

import com.maehem.abyss.engine.GameState;
import com.maehem.abyss.engine.NewsStory;
import com.maehem.abyss.engine.bbs.widgets.BBSGotoButton;
import com.maehem.abyss.engine.bbs.widgets.BBSHeader;
import com.maehem.abyss.engine.bbs.widgets.BBSNewsMenuItem;
import com.maehem.abyss.engine.bbs.widgets.BBSSimpleMenu;
import com.maehem.abyss.engine.bbs.widgets.BBSSimpleMenuItem;
import com.maehem.abyss.engine.bbs.widgets.BBSText;
import java.util.ArrayList;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author mark
 */
public class CityNews extends BBSTerminal {
    private static final int NUM_ITEMS = 9;
    private static final String PREV_LABEL = "PREVIOUS";
    private static final String NEXT_LABEL = "MORE";
    private static final String DONE_LABEL = "DONE";
    
    private int currentIndex = 0;
    ArrayList<BBSText> menuItems = new ArrayList<>();
    
    public CityNews(GameState gs) {
        super(gs);
        setHeader(new BBSHeader(FONT, SiteHeader.PAP_NEWS));
                
        updateContent(gs);
        
        setFooter(new BBSText(FONT,
                  "Public Access Point News               "
                + "            News, News and more News!!!"
        ));
    }
    
    @Override
    public final void updateContent(GameState gs) {
        int index=currentIndex;
        menuItems.clear();
        menuItems.add( new BBSSimpleMenuItem(FONT,"   DATE     SUBJECT" ));
        ArrayList<NewsStory> newsItems = new ArrayList<>();
        for ( NewsStory ni : gs.getNews() ) {
            if ( ni.canShow() ) {
                newsItems.add(ni);
            }
        }
        int j=0;
        while ( j < NUM_ITEMS ) {
            try {
                NewsStory ns = newsItems.get(index+j);
                menuItems.add(new BBSNewsMenuItem(FONT, ns, gs, this ));
                j++;
            } catch (IndexOutOfBoundsException ex ) {
                break;
            }
        }
        boolean hasMore = false;
        if ( j == NUM_ITEMS ) {
            try {
                newsItems.get(index+j);
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
        BBSGotoButton doneButton = new BBSGotoButton(FONT, DONE_LABEL, gs, PublicTerminalSystem.class);
        HBox navButtons = new HBox(prevNode, doneButton, nextNode );
        navButtons.setSpacing(20);
        
        VBox content = new VBox();
        content.getChildren().add( new BBSSimpleMenu(FONT, menuItems));
        content.getChildren().add(new BBSText(FONT, " "));
        content.getChildren().add( BBSTerminal.centeredNode(navButtons) );
                
        setBody(BBSTerminal.centeredNode(content));        
    }
    
}
