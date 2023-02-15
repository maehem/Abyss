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
import com.maehem.flatlinejack.engine.view.ViewPane;
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
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class InventoryPane extends ViewPane implements GameStateListener {

    private static final Display DISPLAY = Display.INVENTORY;
    private static final BorderWidths ITEM_BORDER_WIDTH =new BorderWidths(4.0);

    private static final Border SEL_BORDER = new Border(new BorderStroke(
            new Color(0.9, 0.3, 0.9, 1.0),
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, ITEM_BORDER_WIDTH
    ));
    private static final Border UNSEL_BORDER = new Border(new BorderStroke(
            new Color(0.1, 0.1, 0.1, 0.5),
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, ITEM_BORDER_WIDTH
    ));
//    private static final String BTN_SELECTED_COLOR = "#444466";
//    private static final String BTN_NORMAL_COLOR   = "#666666";
    private static final int N_COLS = 7;
    private static final int MARGIN = 12;
    private static final double SLOT_GAP = 20.0;
    public static final int CELL_SIZE = 96;
    private final BorderPane contentArea = new BorderPane();
    private final ThingDetailPane detailPane;
    private final Player player;
    private final GridPane slots = new GridPane();
    private final GameState gameState;

    public InventoryPane(GameState gs) {
        this.gameState = gs;
        this.player = gs.getPlayer();

        contentArea.setPrefSize(WIDTH, HEIGHT);

        gs.addListenter(this);

        detailPane = new ThingDetailPane(player);

        setBackground(new Background(new BackgroundFill(Color.SLATEGRAY, new CornerRadii(4), Insets.EMPTY)));
        this.setPadding(new Insets(8));

        // Make this a floating window that the user can pin?
        //  When not pinned, it slides out from the side?
        // Inventory Slots
        slots.setVgap(SLOT_GAP);
        slots.setHgap(SLOT_GAP);

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
        text.setFont(Font.font(ViewPane.HEIGHT * 0.05));
        text.setFill(new Color(0.0, 0.0, 0.0, 0.5));
        FlowPane topPane = new FlowPane(Orientation.HORIZONTAL, text);
        topPane.setAlignment(Pos.CENTER);

        contentArea.setTop(topPane);
        contentArea.setCenter(slots);
        contentArea.setLeft(new Rectangle(MARGIN, MARGIN, Color.TRANSPARENT));
        contentArea.setRight(detailPane);

        //detailPane.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        // Right - Item Detail View
        BorderPane.setMargin(detailPane, new Insets(MARGIN, MARGIN, MARGIN, 0));
        BorderPane.setMargin(slots, new Insets(2*MARGIN, 0, MARGIN, 0));

        Button doneButton = new Button("Done");
        doneButton.setFont(Font.font(ViewPane.HEIGHT * 0.03));
        doneButton.setOnMouseClicked((event) -> {
            gs.toggleShowing(DISPLAY);
        });

        FlowPane bottomPane = new FlowPane(Orientation.HORIZONTAL, doneButton);
        bottomPane.setAlignment(Pos.CENTER);
        FlowPane.setMargin(doneButton, new Insets(8));
        contentArea.setBottom(bottomPane);

        getChildren().add(contentArea);

        setVisible(false);
    }

    /**
     * Re-style the button to indicate selected, but also un-style the other
     * buttons.
     *
     * @param b
     */
    private void highlightItem(Button b) {
        for (Node n : slots.getChildrenUnmodifiable()) {
            if (n instanceof Button) {
                if ((Button) n == b) {
                    //n.setStyle("-fx-base: " + BTN_SELECTED_COLOR + ";");
                    b.setBorder(SEL_BORDER);
                } else {
                    //n.setStyle("-fx-base: " + BTN_NORMAL_COLOR + ";");
                    ((Button)n).setBorder(UNSEL_BORDER);
                }
            }
        }
    }

    public static Button createSlotButton(Thing t) {
        String iconPath = t.getIconPath();
        Button b;
        if (iconPath != null) {
            InputStream imgStream = InventoryPane.class.getResourceAsStream(t.getIconPath());
            ImageView iv = new ImageView(new Image(imgStream));
            iv.setFitHeight(CELL_SIZE);
            iv.setPreserveRatio(true);
            b = new Button(null, iv);
        } else {
            b = new Button();
        }

        b.setAccessibleHelp(t.getName());
        b.setTooltip(new Tooltip(t.getName()));
        b.setPrefSize(CELL_SIZE, CELL_SIZE);
        b.setMinSize(CELL_SIZE, CELL_SIZE);
        //b.setStyle("-fx-base: " + BTN_NORMAL_COLOR + ";");
        b.setBackground(new Background(new BackgroundFill(t.getColor().desaturate(), CornerRadii.EMPTY, Insets.EMPTY)));
        return b;
    }

    public void updateItemGrid() {
        slots.getChildren().clear();
        int col = 0;
        for (Thing t : player.getAInventory()) {
            //Button slotButton = Thing.getSlotButton(t);
            Button slotButton = InventoryPane.createSlotButton(t);
            slots.add(slotButton, col % N_COLS, col / N_COLS);            
            slotButton.setOnMouseClicked((event) -> {
                detailPane.showThing(t);
                highlightItem(slotButton);
            });

            col++;
        }
        highlightItem(null); // Set default item border.

    }

    @Override
    public void gameStateVignetteChanged(GameState gs) {
    }

    @Override
    public void gameStatePropertyChanged(GameState gs, String propKey) {
        updateItemGrid();
    }

    @Override
    public void gameStateShowDebug(GameState gs, boolean state) {
    }

    @Override
    public void gameStateDisplayChanged(GameState aThis, Display d) {
        setVisible(d == DISPLAY);
        if (d == DISPLAY) {
            updateItemGrid();
        }
    }

    @Override
    public void gameStateTerminalChanged(GameState gs, BBSTerminal term) {
    }

    @Override
    public void gameStateMatrixSiteChanged(GameState gs, int newAddr) {
    }

}
