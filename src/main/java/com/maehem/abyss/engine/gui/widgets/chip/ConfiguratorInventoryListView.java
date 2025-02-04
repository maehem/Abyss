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
package com.maehem.abyss.engine.gui.widgets.chip;

import com.maehem.abyss.engine.Character;
import com.maehem.abyss.engine.GameState;
import com.maehem.abyss.engine.SkillChipThing;
import com.maehem.abyss.engine.Thing;
import com.maehem.abyss.engine.gui.ChipsConfigurationListener;
import java.util.ArrayList;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author mark
 */
public class ConfiguratorInventoryListView extends VBox {

    private final Character character;
    private final VBox inventoryItems = new VBox();

//    private final Border DARK_BORDER = new Border(new BorderStroke(
//            Color.BLACK,
//            BorderStrokeStyle.SOLID,
//            new CornerRadii(4),
//            new BorderWidths(2)
//    ));
//    private final Border SELECTED_BORDER = new Border(new BorderStroke(
//            Color.RED,
//            BorderStrokeStyle.SOLID,
//            new CornerRadii(4),
//            new BorderWidths(2)
//    ));

    private final ChipsConfigurationListener selectionListener;
    private final GameState gameState;

    public ConfiguratorInventoryListView(GameState gs, double w, Character c, ChipsConfigurationListener listener) {
        setPrefSize(w, w * 0.42);
        this.character = c;
        this.selectionListener = listener;
        this.gameState = gs;

        setBackground(new Background(new BackgroundFill(
                new Color(0.1, 0.1, 0.1, 1.0),
                new CornerRadii(20),
                Insets.EMPTY
        )));
        setPadding(new Insets(10));

        Text heading = new Text("Chip Inventory");
        heading.setFill(Color.WHITE);
        heading.setFont(new Font(20));
        heading.setTextAlignment(TextAlignment.CENTER);
        HBox textArea = new HBox(heading);
        textArea.setAlignment(Pos.CENTER);
        textArea.setPadding(new Insets(0, 0, 6, 0));

        VBox.getVgrow(inventoryItems);
        //inventoryItems.setFillWidth(true);
        inventoryItems.setSpacing(4);
        //inventoryItems.setAlignment(Pos.TOP_CENTER);
        //inventoryItems.setMinHeight(300);
        //inventoryItems.setBackground(Background.EMPTY);
        inventoryItems.setBackground(new Background(new BackgroundFill(
                new Color(0.2, 0.2, 0.2, 1.0),
                CornerRadii.EMPTY,
                Insets.EMPTY
        )));
        refresh(null);

        ScrollPane sp = new ScrollPane(inventoryItems);
        sp.setFitToHeight(true);
        sp.setFitToWidth(true);
        sp.setMinHeight(getPrefHeight()
                - textArea.getBoundsInLocal().getHeight()
                - getPadding().getTop()
                - getPadding().getBottom()
        );
        sp.setBackground(new Background(new BackgroundFill(
                new Color(0.1, 0.1, 0.1, 1.0),
                CornerRadii.EMPTY,
                Insets.EMPTY
        )));

        // Cause the items list to resize to the ScrollPane content width.
        sp.viewportBoundsProperty().addListener((
                ObservableValue<? extends Bounds> observableValue,
                Bounds oldBounds, Bounds newBounds) -> {
            inventoryItems.setMaxWidth(newBounds.getWidth());
            inventoryItems.setMinWidth(newBounds.getWidth());
        });

        // TODO: Style scroll bar: requires CSS
        getChildren().addAll(textArea, sp);
    }

    public final void refresh( SkillChipThing selectedThing ) {
        inventoryItems.getChildren().clear();
        ArrayList<Thing> items = character.getInventory();

        items.forEach((t) -> {
            if (t instanceof SkillChipThing skillChipThing) {
                ConfiguratorInventoryItem item = new ConfiguratorInventoryItem(gameState, skillChipThing);
                if ( selectedThing != null && selectedThing == t ) {
                    item.setSelected(true);
                }
                //item.setBorder(DARK_BORDER);
                inventoryItems.getChildren().addAll(item);

                // Change border when clicked
                item.setOnMouseClicked((tt) -> {
                    // Clear border highlights
                    inventoryItems.getChildren().forEach((ii) -> {
                        if (ii instanceof ConfiguratorInventoryItem) {
                            ((ConfiguratorInventoryItem) ii).setSelected(false);
                        }
                    });
                    item.setSelected(true);
                    selectionListener.thingSelected(skillChipThing);
                });
            }
        });
        if (inventoryItems.getChildren().isEmpty()) {
            Text t = new Text("You have no Skill Chips.");
            t.setFont(Font.font(getPrefWidth() * 0.05));
            t.setFill(new Color(1.0, 1.0, 1.0, 0.5));
            inventoryItems.getChildren().add(t);
            inventoryItems.setAlignment(Pos.CENTER);
        }
//        ConfiguratorInventoryItem i1 = new ConfiguratorInventoryItem();
//        ConfiguratorInventoryItem i2 = new ConfiguratorInventoryItem();
//        ConfiguratorInventoryItem i3 = new ConfiguratorInventoryItem();
//        ConfiguratorInventoryItem i4 = new ConfiguratorInventoryItem();
//        ConfiguratorInventoryItem i5 = new ConfiguratorInventoryItem();
//        ConfiguratorInventoryItem i6 = new ConfiguratorInventoryItem();
//        ConfiguratorInventoryItem i7 = new ConfiguratorInventoryItem();
//        ConfiguratorInventoryItem i8 = new ConfiguratorInventoryItem();
//        ConfiguratorInventoryItem i9 = new ConfiguratorInventoryItem();
//
//        inventoryItems.getChildren().addAll(i1, i2, i3, i4, i5, i6, i7, i8, i9);
        inventoryItems.requestLayout();
    }

}
