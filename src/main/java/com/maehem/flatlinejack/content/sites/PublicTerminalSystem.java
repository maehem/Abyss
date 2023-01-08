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
import com.maehem.flatlinejack.engine.gui.bbs.BBSHeader;
import com.maehem.flatlinejack.engine.gui.bbs.BBSSimpleMenu;
import com.maehem.flatlinejack.engine.gui.bbs.BBSSimpleMenuItem;
import com.maehem.flatlinejack.engine.gui.bbs.BBSTerminal;
import static com.maehem.flatlinejack.engine.gui.bbs.BBSTerminal.FONT;
import com.maehem.flatlinejack.engine.gui.bbs.BBSText;
import java.util.ArrayList;

/**
 *
 * @author mark
 */
public class PublicTerminalSystem extends BBSTerminal {
    
    public PublicTerminalSystem(GameState gs) {
        super(gs);
        setHeader(new BBSHeader(FONT, SiteHeader.PAP));
        
        ArrayList<BBSText> menuItems = new ArrayList<>();
        menuItems.add( new BBSSimpleMenuItem(FONT, '1',"Banking Access", gs, CityBanking.class));
        menuItems.add( new BBSSimpleMenuItem(FONT, '2',"News", gs, CityNews.class));
        menuItems.add( new BBSSimpleMenuItem(FONT, '3',"Public Sites", gs, CityNews.class));
        menuItems.add( new BBSSimpleMenuItem(FONT, '4',"Help System", gs, CityNews.class));
        menuItems.add( new BBSSimpleMenuItem(FONT, 'X',"Exit Terminal", gs, PublicTerminalSystem.class));
        
        BBSSimpleMenu menu = new BBSSimpleMenu(FONT, menuItems);
        
        setBody(BBSTerminal.centeredNode(menu));
        setFooter(new BBSText(FONT,
                  "Public Access Point                    "
                + "       Illegal use will be proscecuted."
        ));
        
    }
    
}
