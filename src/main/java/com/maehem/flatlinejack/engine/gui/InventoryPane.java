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
import com.maehem.flatlinejack.engine.GameState.Display;
import com.maehem.flatlinejack.engine.GameStateListener;
import com.maehem.flatlinejack.engine.Player;
import com.maehem.flatlinejack.engine.Thing;
import com.maehem.flatlinejack.engine.gui.bbs.BBSTerminal;
import com.maehem.flatlinejack.engine.gui.widgets.ThingDetailPane;
import java.io.InputStream;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class InventoryPane extends BorderPane implements GameStateListener {

    private static final Display display = Display.INVENTORY;
    
    private static final String BTN_SELECTED_COLOR = "#444466";
    private static final String BTN_NORMAL_COLOR   = "#666666";
    private static final int N_COLS = 3;
    private static final int MARGIN = 12;
    public static final int CELL_SIZE = 64;
    private final ThingDetailPane detailPane;
    private final Player player;
    private final GridPane slots = new GridPane();
    private final GameState gameState;
    
    public InventoryPane(GameState gs) {
        this.gameState = gs;
        this.player = gs.getPlayer();
        
        gs.addListenter(this);
        
        detailPane = new ThingDetailPane(player);
        
        setBackground(new Background(new BackgroundFill(Color.SLATEGRAY, new CornerRadii(4), Insets.EMPTY)));
        this.setPadding(new Insets(16));
        
        
        // Make this a floating window that the user can pin?
        //  When not pinned, it slides out from the side?
        
        // Inventory Slots
        slots.setVgap(MARGIN);
        slots.setHgap(MARGIN);
        
        updateItemGrid();
//        for( Thing t: player.getAInventory() ) {
//            //Button slotButton = Thing.getSlotButton(t);
//            Button slotButton = InventoryPane.createSlotButton(t);
//            slots.add(slotButton, col%N_COLS, col/N_COLS );
//            slotButton.setOnMouseClicked((event) -> {
//                detailPane.showThing(t);
//                highlightItem(slotButton);
//            });
//            
//            
//            col++;
//        }
        Text text = new Text("Inventory");
        FlowPane topPane = new FlowPane(Orientation.HORIZONTAL, text);
        topPane.setAlignment(Pos.CENTER);
        setTop(topPane);
        
        setCenter(slots);
        
        setLeft(  new Rectangle(MARGIN, MARGIN, Color.TRANSPARENT));
        
        // Right - Item Detail View
        BorderPane.setMargin(detailPane, new Insets(MARGIN));
        BorderPane.setMargin(slots, new Insets(MARGIN));
        setRight( detailPane);
        
        Button doneButton = new Button("Done");
        doneButton.setOnMouseClicked((event) -> {
            this.setVisible(false);
        });
        
        FlowPane bottomPane = new FlowPane(Orientation.HORIZONTAL, doneButton);
        bottomPane.setAlignment(Pos.CENTER);
        FlowPane.setMargin(doneButton, new Insets(8));
        setBottom(bottomPane);
        
        setVisible(false);
    }
    
    /**
     * Re-style the button to indicate selected, but also un-style the other
     * buttons.
     * 
     * @param b 
     */
    private void highlightItem( Button b ) {
        for( Node n : slots.getChildrenUnmodifiable() ) {
            if ( n instanceof Button ) {
                if ( (Button)n == b ) {
                    n.setStyle("-fx-base: " + BTN_SELECTED_COLOR + ";");
                } else {
                    n.setStyle("-fx-base: " + BTN_NORMAL_COLOR + ";");
                }
            }
        }
    }
    
    public static Button createSlotButton(Thing t) {
        String iconPath = t.getIconPath();
        Button b;
        if (iconPath != null ) {
            InputStream imgStream = InventoryPane.class.getResourceAsStream(t.getIconPath());
            ImageView iv = new ImageView(new Image(imgStream));
            iv.setFitHeight(CELL_SIZE);
            iv.setPreserveRatio(true);
            b = new Button(null,iv);
        } else {
            b = new Button();
        }

        b.setAccessibleHelp(t.getName());
        b.setTooltip(new Tooltip(t.getName()));
        b.setPrefSize(CELL_SIZE, CELL_SIZE);
        b.setMinSize(CELL_SIZE, CELL_SIZE);
        b.setStyle("-fx-base: " + BTN_NORMAL_COLOR + ";");
        return b;
    }

    public void updateItemGrid() {
        slots.getChildren().clear();
        int col = 0;
        for( Thing t: player.getAInventory() ) {
            //Button slotButton = Thing.getSlotButton(t);
            Button slotButton = InventoryPane.createSlotButton(t);
            slots.add(slotButton, col%N_COLS, col/N_COLS );
            slotButton.setOnMouseClicked((event) -> {
                detailPane.showThing(t);
                highlightItem(slotButton);
            });
            
            
            col++;
        }

    }

    @Override
    public void gameStateVignetteChanged(GameState gs) {}

    @Override
    public void gameStatePropertyChanged(GameState gs, String propKey) { }

    @Override
    public void gameStateShowDebug(GameState gs, boolean state) {}

    @Override
    public void gameStateDisplayChanged(GameState aThis, Display d) {
        setVisible(d == display);
        if ( d == display ) {
            updateItemGrid();
        }
    }

    @Override
    public void gameStateTerminalChanged(GameState gs, BBSTerminal term) { }
    
           
}
