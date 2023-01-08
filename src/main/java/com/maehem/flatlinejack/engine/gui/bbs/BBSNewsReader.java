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

import com.maehem.flatlinejack.content.sites.CityNews;
import com.maehem.flatlinejack.engine.GameState;
import com.maehem.flatlinejack.engine.NewsStory;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author mark
 */
public class BBSNewsReader extends BBSReader {
    
    public BBSNewsReader(GameState gs, String uid) {
        super(gs);
        
        NewsStory ns = gs.getNewsStory(uid);
        
        setHeader(new BBSText(FONT, ns.getDate() + "      " + ns.getHeadline() ));
        BBSTextFlow text = new BBSTextFlow();
        text.getChildren().add(new BBSText(FONT, "\n\n"));
        text.getChildren().add(new BBSText(FONT, ns.getBody()));
        text.setTextAlignment(TextAlignment.JUSTIFY);
        setBody(text);
        
        setFooter(new BBSGotoButton(FONT, "BACK", gs, CityNews.class));
    }
    
}
