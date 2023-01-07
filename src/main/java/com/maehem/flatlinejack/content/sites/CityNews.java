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
public class CityNews extends BBSTerminal {

    public CityNews(GameState gs) {
        super(gs);
        setHeader(new BBSHeader(FONT, SiteHeader.PAP_NEWS));
        
        ArrayList<BBSSimpleMenuItem> menuItems = new ArrayList<>();
        menuItems.add( new BBSSimpleMenuItem(FONT,"   DATE     SUBJECT" ));
        menuItems.add(new BBSSimpleMenuItem(FONT, '1',"02/23/2054  Daily News Summary", gs, CityNews.class));
        menuItems.add(new BBSSimpleMenuItem(FONT, '2',"02/23/2054  Local Organ Bank Reopens After Mixup", gs, CityNews.class));
        menuItems.add(new BBSSimpleMenuItem(FONT, '3',"02/22/2054  Local Man Escapes Death In Freak Cyberspace Accident", gs, CityNews.class));
        menuItems.add(new BBSSimpleMenuItem(FONT, '4',"02/21/2054  Popular Bar Closed Due To Food Poisoning", gs, CityNews.class));
        
        BBSSimpleMenu menu = new BBSSimpleMenu(FONT, menuItems);
        
        setBody(BBSTerminal.centeredNode(menu));
        setFooter(new BBSText(FONT,
                  "Public Access Point News               "
                + "            News, News and more News!!!"
        ));
    }
    
}
