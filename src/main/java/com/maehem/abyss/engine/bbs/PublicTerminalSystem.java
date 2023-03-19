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

import com.maehem.abyss.engine.GameState;
import com.maehem.abyss.engine.bbs.widgets.BBSGotoButton;
import com.maehem.abyss.engine.bbs.widgets.BBSHeader;
import com.maehem.abyss.engine.bbs.widgets.BBSSimpleMenu;
import com.maehem.abyss.engine.bbs.widgets.BBSSimpleMenuItem;
import static com.maehem.abyss.engine.bbs.BBSTerminal.FONT;
import com.maehem.abyss.engine.bbs.widgets.BBSText;
import java.util.ArrayList;
import javafx.scene.layout.VBox;

/**
 *
 * @author mark
 */
public class PublicTerminalSystem extends BBSTerminal {
    private static final String EXIT_LABEL = "Exit Terminal";
    private static final String BANKING_LABEL = "\u25ba Banking Access";
    private static final String NEWS_LABEL = "\u25ba News";
    private static final String BULLETIN_LABEL = "\u25ba Bulletin Board";
    private static final String HELP_LABEL = "\u25ba Help System";
    
    private final BBSTerminal banking;
    private final BBSTerminal news;
    private final BBSTerminal bulletin;
    private final BBSTerminal help;
    
    public PublicTerminalSystem(GameState gs) {
        super(gs);
        banking = new CityBanking(gs);
        news = new CityNews(gs);
        bulletin = new BBSBulletinBoard(gs,this);
        help = new HelpSystem(gs, this);
        
        setHeader(new BBSHeader(FONT, SiteHeader.PAP));
        
        ArrayList<BBSText> menuItems = new ArrayList<>();
        menuItems.add( new BBSSimpleMenuItem(FONT, BANKING_LABEL, gs, banking));
        menuItems.add( new BBSSimpleMenuItem(FONT, NEWS_LABEL, gs, news));
        menuItems.add( new BBSSimpleMenuItem(FONT, BULLETIN_LABEL, gs, bulletin));
        menuItems.add( new BBSSimpleMenuItem(FONT, HELP_LABEL, gs, help));
        menuItems.add( new BBSSimpleMenuItem(FONT, ""));
        menuItems.add( new BBSSimpleMenuItem(FONT, ""));
        menuItems.add( new BBSSimpleMenuItem(FONT, ""));
        BBSSimpleMenu menu = new BBSSimpleMenu(FONT, menuItems);

        // TODO: Make into a button.
        //BBSSimpleMenuItem exitItem = new BBSSimpleMenuItem(FONT, "Exit Terminal" );
        
        BBSGotoButton exitButton = new BBSGotoButton(FONT, EXIT_LABEL);
        
        exitButton.setOnMouseClicked((t) -> {
            gs.setCurrentTerminal(this);
        });
        
        VBox content = new VBox(menu, centeredNode(exitButton));
        
        setBody(BBSTerminal.centeredNode(content));
        setFooter(new BBSText(FONT,
                  "Public Access Point                    "
                + "       Illegal use will be proscecuted."
        ));
        
    }
    
    public BBSTerminal getBanking() {
        return banking;
    }
    
    public BBSTerminal getNews() {
        return news;
    }
    
    public BBSTerminal getBulletin() {
        return bulletin;
    }
    
    public BBSTerminal getHelp() {
        return help;
    }
    
}
