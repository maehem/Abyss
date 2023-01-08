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

import static com.maehem.flatlinejack.engine.gui.bbs.BBSTerminal.FONT;

import com.maehem.flatlinejack.engine.GameState;
import com.maehem.flatlinejack.engine.Player;
import com.maehem.flatlinejack.engine.gui.bbs.BBSGotoButton;
import com.maehem.flatlinejack.engine.gui.bbs.BBSHeader;
import com.maehem.flatlinejack.engine.gui.bbs.BBSMoneyTransferWidget;
import com.maehem.flatlinejack.engine.gui.bbs.BBSTerminal;
import com.maehem.flatlinejack.engine.gui.bbs.BBSText;
import javafx.scene.layout.VBox;

/**
 *
 * @author mark
 */
public class CityBanking extends BBSTerminal {

    public CityBanking(GameState gs) {
        super(gs);
        Player player = gs.getPlayer();
        
        setHeader(new BBSHeader(FONT, SiteHeader.BANK_MARZ));
        
        VBox content = new VBox();
        content.getChildren().add(new BBSText(FONT, 
                "   Name: " + Player.PLAYER_NAME_LONG 
                +"    ID: " + Player.PLAYER_ID ));
        content.getChildren().add(new BBSText(FONT, " "));
        content.getChildren().add(new BBSMoneyTransferWidget(FONT, gs));
        content.getChildren().add(new BBSText(FONT, " "));
        content.getChildren().add( 
                new BBSGotoButton(FONT, "DONE", gs, PublicTerminalSystem.class)
        );
        
        setBody(BBSTerminal.centeredNode(content));
        setFooter(new BBSText(FONT,
                  "Bank of the Mojave Area Residential Zone"
        ));
    }
    
}
