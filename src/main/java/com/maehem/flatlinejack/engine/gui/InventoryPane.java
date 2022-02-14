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

import com.maehem.flatlinejack.engine.Player;
import com.maehem.flatlinejack.engine.Thing;
import com.maehem.flatlinejack.engine.gui.widgets.ThingDetailPane;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
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
public class InventoryPane extends BorderPane {


    private static final int N_COLS = 3;
    private static final int MARGIN = 12;
    public static final int CELL_SIZE = 64;
    private final ThingDetailPane detailPane;
    private final Player player;
    
    
    public InventoryPane(Player p) {
        this.player = p;
        
        detailPane = new ThingDetailPane(p);
        
        setBackground(new Background(new BackgroundFill(Color.SLATEGRAY, new CornerRadii(4), Insets.EMPTY)));
        this.setPadding(new Insets(16));
        
        
        // Make this a floating window that the user can pin?
        //  When not pinned, it slides out from the side?
        
        // Inventory Slots
        GridPane slots = new GridPane();
        slots.setVgap(MARGIN);
        slots.setHgap(MARGIN);
        
        
        int col = 0;
        for( Thing t: player.getAInventory() ) {
            Node slotButton = Thing.getSlotButton(t);
            slots.add(slotButton, col%N_COLS, col/N_COLS );
            slotButton.setOnMouseClicked((event) -> {
                detailPane.showThing(t);
            });
            
            
//            if ( t != null ) {
//                slots.add(t.getSlotButton(), col%N_COLS, col/N_COLS);
//            } else {
//                Button button = new Button(" ");
//                button.setMinSize(CELL_SIZE, CELL_SIZE);
//                slots.add(button, col%N_COLS, col/N_COLS);
//            }
            col++;
        }
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
    }
    
    
}
