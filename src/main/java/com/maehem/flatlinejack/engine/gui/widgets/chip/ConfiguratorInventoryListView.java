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
package com.maehem.flatlinejack.engine.gui.widgets.chip;

import javafx.beans.value.ChangeListener;
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

    public ConfiguratorInventoryListView(double w) {
        setPrefSize(w, w * 0.36);
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


        VBox inventoryItems = new VBox();
        inventoryItems.setFillWidth(true);
        inventoryItems.setSpacing(4);
        inventoryItems.setBackground(new Background( new BackgroundFill(
                new Color(0.1, 0.1, 0.1, 1.0),
                CornerRadii.EMPTY,
                Insets.EMPTY
        )));
        

        ConfiguratorInventoryItem i1 = new ConfiguratorInventoryItem();
        ConfiguratorInventoryItem i2 = new ConfiguratorInventoryItem();
        ConfiguratorInventoryItem i3 = new ConfiguratorInventoryItem();
        ConfiguratorInventoryItem i4 = new ConfiguratorInventoryItem();
        ConfiguratorInventoryItem i5 = new ConfiguratorInventoryItem();
        ConfiguratorInventoryItem i6 = new ConfiguratorInventoryItem();
        ConfiguratorInventoryItem i7 = new ConfiguratorInventoryItem();
        ConfiguratorInventoryItem i8 = new ConfiguratorInventoryItem();
        ConfiguratorInventoryItem i9 = new ConfiguratorInventoryItem();

        inventoryItems.getChildren().addAll(i1, i2, i3, i4, i5, i6, i7, i8, i9);

        ScrollPane sp = new ScrollPane(inventoryItems);
//        sp.setBackground(new Background(new BackgroundFill(
//                Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY
//        )));
        sp.setBackground(Background.EMPTY);
        
        // Cause the items list to resize to the ScrollPane content width.
        sp.viewportBoundsProperty().addListener((
                ObservableValue<? extends Bounds> observableValue, 
                Bounds oldBounds, Bounds newBounds
        ) -> {
            inventoryItems.setMaxWidth(newBounds.getWidth());
            inventoryItems.setMinWidth(newBounds.getWidth());
        });
        

        // TODO: Style scroll bar: requires CSS
        
        
        getChildren().addAll(textArea, sp);
   }

}
