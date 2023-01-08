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
package com.maehem.flatlinejack.content.sites;

import com.maehem.flatlinejack.engine.GameState;
import com.maehem.flatlinejack.engine.NewsStory;
import com.maehem.flatlinejack.engine.gui.bbs.BBSGotoButton;
import com.maehem.flatlinejack.engine.gui.bbs.BBSHeader;
import com.maehem.flatlinejack.engine.gui.bbs.BBSNewsMenuItem;
import com.maehem.flatlinejack.engine.gui.bbs.BBSSimpleMenu;
import com.maehem.flatlinejack.engine.gui.bbs.BBSSimpleMenuItem;
import com.maehem.flatlinejack.engine.gui.bbs.BBSTerminal;
import static com.maehem.flatlinejack.engine.gui.bbs.BBSTerminal.FONT;
import com.maehem.flatlinejack.engine.gui.bbs.BBSText;
import java.util.ArrayList;
import javafx.scene.layout.VBox;

/**
 *
 * @author mark
 */
public class CityNews extends BBSTerminal {

    public CityNews(GameState gs) {
        super(gs);
        setHeader(new BBSHeader(FONT, SiteHeader.PAP_NEWS));
                
        ArrayList<BBSText> menuItems = new ArrayList<>();
        int i=1;
        menuItems.add( new BBSSimpleMenuItem(FONT,"   DATE     SUBJECT" ));
        for ( NewsStory ns : gs.getNews() ) {
            if (ns.canShow() ) {
                menuItems.add(new BBSNewsMenuItem(FONT, (char) ('0'+i), 
                        ns.getDate() + "  " + ns.getHeadline(),
                        ns.getUid(), gs
                ));
                
                i++;
            }
            
            // nine messages max, generate next button
        }
        
        VBox content = new VBox();
        content.getChildren().add( new BBSSimpleMenu(FONT, menuItems));
        content.getChildren().add(new BBSText(FONT, " "));
        content.getChildren().add( 
                new BBSGotoButton(FONT, "DONE", gs, PublicTerminalSystem.class)
        );
                
        setBody(BBSTerminal.centeredNode(content));
        setFooter(new BBSText(FONT,
                  "Public Access Point News               "
                + "            News, News and more News!!!"
        ));
    }
    
}
