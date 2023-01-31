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
package com.maehem.flatlinejack.engine.gui;

import com.maehem.flatlinejack.engine.GameState;
import com.maehem.flatlinejack.engine.GameStateListener;
import com.maehem.flatlinejack.engine.gui.bbs.BBSTerminal;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 *
 * @author mark
 */
public class RomConstructPane extends GUIPane implements GameStateListener {
    
    private final BorderPane layoutPane = new BorderPane();
    private final Text title = new Text("ROM Construct - Abyss");
    private final Text t = new Text("Hey buddy!\nLet's look around a little more.");
    
    private final TextFlow flow = new TextFlow(t);

    public RomConstructPane(GameState gs, double width) {
        gs.addListenter(this);
        
        title.setFont(new Font(50));
        ImageView titleIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/chip-head.png")));
        titleIcon.setFitHeight(60);
        titleIcon.setPreserveRatio(true);
        Pane spacer = new Pane();
        spacer.setPrefSize(40, 40);
        HBox titleArea = new HBox(titleIcon,  spacer, title);
        titleArea.setPadding(new Insets(6, 20, 10, 10));
        Image image = new Image(getClass().getResourceAsStream("/ui/rom-1.png"));
        ImageView romPortriat = new ImageView(image);
        romPortriat.setFitHeight(170);
        romPortriat.setPreserveRatio(true);
        
        setWidth(width);
        t.setFont(new Font(30));
        flow.setPadding(new Insets(12));
        
        layoutPane.setTop(titleArea);
        layoutPane.setCenter(flow);
        layoutPane.setRight(romPortriat);
        getChildren().add(layoutPane);
    }

    @Override
    public void gameStateVignetteChanged(GameState gs) {
    }

    @Override
    public void gameStatePropertyChanged(GameState gs, String propKey) {
    }

    @Override
    public void gameStateShowDebug(GameState gs, boolean state) {
    }

    @Override
    public void gameStateTerminalChanged(GameState gs, BBSTerminal term) {}

    @Override
    public void gameStateDisplayChanged(GameState aThis, GameState.Display d) {}

}