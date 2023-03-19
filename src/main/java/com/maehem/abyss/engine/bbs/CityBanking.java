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
import com.maehem.abyss.engine.Player;
import com.maehem.abyss.engine.bbs.widgets.BBSGotoButton;
import com.maehem.abyss.engine.bbs.widgets.BBSHeader;
import com.maehem.abyss.engine.bbs.widgets.BBSMoneyTransferWidget;
import com.maehem.abyss.engine.bbs.widgets.BBSText;
import javafx.scene.layout.VBox;

/**
 *
 * @author mark
 */
public class CityBanking extends BBSTerminal {

    private final VBox content;

    public CityBanking(GameState gs) {
        super(gs);
        Player player = gs.getPlayer();
        
        setHeader(new BBSHeader(FONT, SiteHeader.BANK_CITY));
        
        content = new VBox();
        updateContent(gs);
        setBody(BBSTerminal.centeredNode(content));
        setFooter(new BBSText(FONT,
                  "Bank of the City"
        ));
    }

    @Override
    public void updateContent(GameState gs) {
        super.updateContent(gs); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody

        content.getChildren().clear();
        content.getChildren().add(new BBSText(FONT, 
                "   Name: " + gs.getPlayer().getName() +
                "     ID: " + gs.getPlayer().getAccountId() ));
        content.getChildren().add(new BBSText(FONT, " "));
        content.getChildren().add(new BBSMoneyTransferWidget(FONT, gs));
        content.getChildren().add(new BBSText(FONT, " "));
        content.getChildren().add( 
                new BBSGotoButton(FONT, "DONE", gs, PublicTerminalSystem.class)
        );
    }
    
    
    
}
