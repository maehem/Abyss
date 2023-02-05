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
import com.maehem.flatlinejack.engine.view.ViewPane;
import com.maehem.flatlinejack.engine.gui.bbs.BBSTerminal;
import com.maehem.flatlinejack.engine.gui.widgets.chip.ChipDetailsPane;
import com.maehem.flatlinejack.engine.gui.widgets.chip.ConfiguratorInventoryListView;
import com.maehem.flatlinejack.engine.gui.widgets.chip.InstalledChipsGridPane;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 *
 * @author mark
 */
public class ChipsConfiguratorPane extends Pane implements GameStateListener {
    private static final GameState.Display display = GameState.Display.CHIPS;
    
    private static final double VIEW_W = 530;
    private static final double VIEW_X = 670;
    private static final String BG_IMAGE_FILE = "/ui/chips-configurator.png";
    
    private final InstalledChipsGridPane installedChips = new InstalledChipsGridPane( VIEW_W, 2*VIEW_W/3);
    // Chip detail
    private final ChipDetailsPane details = new ChipDetailsPane(VIEW_W);
    // Chips inventory (scrollpane)
    private final ConfiguratorInventoryListView inventory = new ConfiguratorInventoryListView(VIEW_W);
    private final GameState gameState;
    

    public ChipsConfiguratorPane( GameState gs ) {  //, int w, int h) {
        this.gameState = gs;
        gameState.addListenter(this);
        
        this.setPrefSize(ViewPane.WIDTH, ViewPane.HEIGHT);
        
        VBox content = new VBox(installedChips, details, inventory );
        content.setLayoutX(VIEW_X);
        content.setLayoutY(70);
        content.setSpacing(0);
        
        VBox.setMargin(installedChips, new Insets(0,0,0,20));
        VBox.setMargin(inventory, new Insets(0,0,0,20));
                
        // Image for neck.
        Image bgImage = new Image( getClass().getResourceAsStream(BG_IMAGE_FILE));
        setBackground(new Background(
                new BackgroundImage(bgImage, 
                        BackgroundRepeat.NO_REPEAT, 
                        BackgroundRepeat.NO_REPEAT, 
                        BackgroundPosition.CENTER, 
                        BackgroundSize.DEFAULT
                )));
        

        getChildren().add(content);
        
        setVisible(false);
    }

    @Override
    public void gameStateVignetteChanged(GameState gs) {}

    @Override
    public void gameStatePropertyChanged(GameState gs, String propKey) {}

    @Override
    public void gameStateShowDebug(GameState gs, boolean state) {}

    @Override
    public void gameStateDisplayChanged(GameState gs, GameState.Display d) {
        setVisible(d == display);
        if ( d == display ) {
            //updateItemGrid();
        }
    }

    @Override
    public void gameStateTerminalChanged(GameState gs, BBSTerminal term) {}

    @Override
    public void gameStateMatrixSiteChanged(GameState gs, int newAddr) {}
    
}
