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
package com.maehem.flatlinejack.engine.gui.widgets;

import com.maehem.flatlinejack.engine.GameState;
import com.maehem.flatlinejack.engine.GameStateListener;
import com.maehem.flatlinejack.engine.Player;
import com.maehem.flatlinejack.engine.gui.bbs.BBSTerminal;
import static com.maehem.flatlinejack.engine.SkillChipThing.Buff.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 *
 * @author mark
 */
public class OLEDStatusScreen extends DecoBox implements GameStateListener {
    private static final Color OLED_COLOR1 = Color.LIGHTBLUE;
    private static final Color OLED_COLOR2 = Color.YELLOW;
    
    private final DateTimeWidget timeWidget = new DateTimeWidget(22, OLED_COLOR1);
    private final PixelGaugeWidget healthWidget = new PixelGaugeWidget("HLTH", 20, OLED_COLOR1, OLED_COLOR2);
    private final PixelGaugeWidget constWidget = new PixelGaugeWidget("CONS", 20, OLED_COLOR1, OLED_COLOR2);
    private final PixelGaugeWidget crypotWidget = new PixelGaugeWidget(CRYPTO.mnemonic(), 20, OLED_COLOR1, OLED_COLOR2);
    private final PixelGaugeWidget debugWidget = new PixelGaugeWidget(DEBUG.mnemonic(), 20, OLED_COLOR1, OLED_COLOR2);
    private final PixelGaugeWidget hackingWidget = new PixelGaugeWidget(HACKING.mnemonic(), 20, OLED_COLOR1, OLED_COLOR2);
    private final PixelGaugeWidget interrogateWidget = new PixelGaugeWidget(INTERROGATE.mnemonic(), 20, OLED_COLOR1, OLED_COLOR2);
    private final PixelGaugeWidget musicWidget = new PixelGaugeWidget(MUSIC.mnemonic(), 20, OLED_COLOR1, OLED_COLOR2);
    private final PixelGaugeWidget negotiateWidget = new PixelGaugeWidget(NEGOTIATE.mnemonic(), 20, OLED_COLOR1, OLED_COLOR2);
    private final PixelGaugeWidget recoveryWidget = new PixelGaugeWidget(RECOVERY.mnemonic(), 20, OLED_COLOR1, OLED_COLOR2);
    private final PixelGaugeWidget repairWidget = new PixelGaugeWidget(REPAIR.mnemonic(), 20, OLED_COLOR1, OLED_COLOR2);
    private final PixelGaugeWidget softwareWidget = new PixelGaugeWidget(SOFTWARE.mnemonic(), 20, OLED_COLOR1, OLED_COLOR2);
    
    public OLEDStatusScreen( GameState gs ) {
        gs.addListenter(this);
        
        setPrefWidth(280);
        setAlignment(Pos.CENTER);
        setBackground(new Background(new BackgroundFill(new Color(0.1,0.1,0.1,1.0), CornerRadii.EMPTY, Insets.EMPTY)));
        
        VBox content = new VBox(
                timeWidget,healthWidget,constWidget,
                crypotWidget, debugWidget, hackingWidget,
                interrogateWidget, musicWidget, negotiateWidget,
                recoveryWidget,repairWidget, softwareWidget
        );
        //VBox.setMargin(content, new Insets(5));
        content.setSpacing(4);
        getChildren().add(content);
        
        refresh(gs);
    }

    private void refresh( GameState gs ) {
        Player p = gs.getPlayer();
        timeWidget.refresh(gs);
        healthWidget.setValue(p.getHealth() );
        constWidget.setValue(p.getConstitution() );
        crypotWidget.setValue(p.getSkill(CRYPTO));
        debugWidget.setValue(p.getSkill(DEBUG));
        hackingWidget.setValue(p.getSkill(HACKING));
        interrogateWidget.setValue(p.getSkill(INTERROGATE));
        musicWidget.setValue(p.getSkill(MUSIC));
        negotiateWidget.setValue(p.getSkill(NEGOTIATE));
        recoveryWidget.setValue(p.getSkill(RECOVERY));
        repairWidget.setValue(p.getSkill(REPAIR));
        softwareWidget.setValue(p.getSkill(SOFTWARE));
    }
    
    
    @Override
    public void gameStateVignetteChanged(GameState gs) {
    }

    @Override
    public void gameStatePropertyChanged(GameState gs, String propKey) {
        // Update
        refresh(gs);
    }

    @Override
    public void gameStateShowDebug(GameState gs, boolean state) {
    }

    @Override
    public void gameStateTerminalChanged(GameState gs, BBSTerminal term) {
    }

    @Override
    public void gameStateDisplayChanged(GameState gs, GameState.Display d) {
    }

    @Override
    public void gameStateMatrixSiteChanged(GameState gs, int newAddr) {
    }
    
}
