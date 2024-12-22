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
package com.maehem.abyss.engine.babble;

import com.maehem.abyss.engine.*;
import com.maehem.abyss.engine.Character;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class VendWidget extends VBox {

    public final static String FONT_PATH = "/fonts/Modenine-2OPd.ttf";
    public final static double FONT_SIZE = 24;
    private final Font FONT = Font.loadFont(getClass().getResourceAsStream(FONT_PATH), FONT_SIZE);
    private final Font HEADER_FONT = Font.loadFont(getClass().getResourceAsStream(FONT_PATH), FONT_SIZE * 0.8);

    private final Player player;
    private final Character character;
    private EventHandler<ActionEvent> eventHandler;
    private final ArrayList<Thing> items;

    public VendWidget(Character character, Player player, ArrayList<Thing> vendItems, double height) {
        this.character = character;
        this.player = player;
        this.items = vendItems;

        setMaxHeight(height);
        setPrefHeight(height);

        setFillWidth(true);
        setSpacing(8);

        //setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));

        initHeading();
        initCenter();
        initBottom();

    }

    private void initHeading() {
        // TODO: i18n Bundle strings.
        Text title = topText("Price List");
        Text crLabel = topText("Credits: ");
        Text crVal = topText("$" + player.getMoney());
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox hBox = new HBox(title, spacer, crLabel, crVal);
        //BorderPane.setMargin(hBox, new Insets(FONT_SIZE));
        getChildren().add(hBox);

        Text descLabel = headerText(" Description");
        Text qtyLabel = headerText("QTY   ");
        Text costLabel = headerText("Cost  ");
        Region hSpacer = new Region();
        HBox.setHgrow(hSpacer, Priority.ALWAYS);
        HBox hdrBox = new HBox(descLabel, hSpacer, qtyLabel, costLabel);
        //BorderPane.setMargin(hBox, new Insets(FONT_SIZE));
        getChildren().add(hdrBox);
    }

    private Text topText(String s) {
        Text text = new Text(s);
        text.setFont(FONT);

        return text;
    }

    private Text headerText(String s) {
        Text text = new Text(s);
        text.setFont(HEADER_FONT);

        return text;
    }

    private void initCenter() {
        VBox itemsBox = new VBox();
        itemsBox.setFillWidth(true);
        itemsBox.setSpacing(FONT_SIZE / 8);
        itemsBox.setPadding(new Insets(12));
        //itemsBox.setBorder(new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));

        ScrollPane sp = new ScrollPane(new BorderPane(itemsBox));
        sp.setFitToHeight(true);
        sp.setFitToWidth(true);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        VBox.setVgrow(sp, Priority.ALWAYS);

        items.forEach((t) -> {
            itemsBox.getChildren().add(vendItem(t));
        });

        getChildren().add(sp);
    }

    private Node vendItem(Thing thing) {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Text priceText = itemText(String.format("%4d", thing.getValue()));
        Text qtyText;
        if (thing.getVendQuantity() < 0) {
            qtyText = itemText("  ");
        } else {
            qtyText = itemText(String.format("%2d", thing.getVendQuantity()));
        }
        HBox lineBox = new HBox(itemText(thing.getName()), spacer, qtyText, priceText);
        lineBox.setSpacing(FONT_SIZE);
        return lineBox;
    }

    private Text itemText(String s) {
        Text text = new Text(s);
        text.setFont(FONT);

        return text;
    }

    private void initBottom() {

        Button okButton = new Button("DONE");
        okButton.setStyle("-fx-font-size: 26;");

        HBox box = new HBox(okButton);
        box.setAlignment(Pos.CENTER);

        getChildren().add(box);

        okButton.setOnAction((t) -> {
            if (eventHandler != null) {
                eventHandler.handle(t);
            }
        });
    }

    public final void setOnAction(EventHandler<ActionEvent> eh) {
        this.eventHandler = eh;
    }
}
