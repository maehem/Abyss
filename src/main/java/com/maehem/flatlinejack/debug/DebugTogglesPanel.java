/*
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.

*/
package com.maehem.flatlinejack.debug;

import com.maehem.flatlinejack.Engine;
import com.maehem.flatlinejack.engine.GameState;
import com.maehem.flatlinejack.engine.GameStateListener;
import com.maehem.flatlinejack.engine.gui.bbs.BBSTerminal;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 *
 * @author maehem
 */
public class DebugTogglesPanel extends GridPane implements GameStateListener {

    private static final Logger LOGGER = Logger.getLogger(DebugTogglesPanel.class.getName());
    public static final double STUB_HEIGHT = 24;

    //private final Data data;
    //private final Debug debug;
    
    private final ToggleButton showCoords;
//    private final ToggleButton ghostStructures;


    public DebugTogglesPanel(GameState gs) { //Graphics gfx) {
        //this.data = gfx.game.data;
        //this.debug = gfx.debug;
        
        gs.addListenter(this);
        setHgap(4);
        setVgap(4);
        //setGridLinesVisible(true);
        
        setBorder(new Border(new BorderStroke(Color.GREY.brighter(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        setPadding(new Insets(4));
        
        showCoords = new ToggleButton("", createGlyph("/debug/glyphs/xy-visible.png"));
        showCoords.setTooltip(new Tooltip("Show Tile Coordinates"));
        //showCoords.setSelected(debug.showTileCoordinates);
        showCoords.selectedProperty().addListener((ov, prev, current) -> {
            gs.showWalkPerimeter = current;
        });
        add(showCoords, 0, 0);
        
//        ghostStructures = new ToggleButton("", Toolbar.createGlyph("/glyphs/ghost-buildings.png"));
//        ghostStructures.setTooltip(new Tooltip("Lower Building Opacity"));
//        ghostStructures.setSelected(debug.lowerBuildingOpacity);
//        ghostStructures.selectedProperty().addListener((ov, prev, current) -> {
//            debug.lowerBuildingOpacity = current;
//        });
//        add(ghostStructures, 0, 1);
        

    }
    
    public static StackPane createGlyph(String path) {
//        try {
            //URL glyphResource = DebugTogglesPanel.class.getResource(path);
            InputStream stream = Engine.class.getResourceAsStream(path);
            if ( stream == null ) {
                LOGGER.log(Level.WARNING, "Could not find glyph at path:" + path);
            }
            ImageView glyphImage = new ImageView(new Image(stream));
            glyphImage.setPreserveRatio(true);
            glyphImage.setFitHeight(STUB_HEIGHT);
            return new StackPane(glyphImage);
//        } catch (IOException ex) {
//            LOGGER.log(Level.SEVERE, null, ex);
//            return new StackPane();
//        }
    }

    @Override
    public void gameStateVignetteChanged(GameState gs) {}

    @Override
    public void gameStatePropertyChanged(GameState gs, String propKey) {}

    @Override
    public void gameStateShowInventory(GameState gs, boolean state) {}

    @Override
    public void gameStateShowChips(GameState gs, boolean state) {}

    @Override
    public void gameStateShowDebug(GameState gs, boolean state) {}

    @Override
    public void gameStateTerminalChanged(GameState gs, BBSTerminal term) {}

    @Override
    public void gameStateShowTerminal(GameState aThis, boolean showTerminal) {}

}
