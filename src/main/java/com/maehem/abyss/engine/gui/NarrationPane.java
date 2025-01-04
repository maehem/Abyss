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
package com.maehem.abyss.engine.gui;

import java.util.ArrayList;
import java.util.Collections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author Mark J Koch [@maehem on GitHub]
 */
public class NarrationPane extends GUIPane {

    private final Font font = Font.loadFont(
            getClass().getResourceAsStream("/fonts/Orbitron-Regular.ttf"), 30.0);
    private final Text titleText = new Text("Title");
    private final CrtTextPane2 crtPane;
    private final static double CORNER_RADIUS = 30.0;

    public NarrationPane(double width) {
        setPrefWidth(width);
        titleText.setFont(font);
        titleText.setFill(new Color(0.2, 0.2, 0.2, 0.7));
        HBox titleBox = new HBox(titleText);
        titleBox.setAlignment(Pos.CENTER);

        crtPane = new CrtTextPane2(width);

        // Add a rounded corner effect to the crt.
        HBox crtBezel = new HBox(crtPane);
        crtBezel.setPadding(new Insets(CORNER_RADIUS / 3.333));
        crtBezel.setBackground(new Background(new BackgroundFill(CrtTextPane2.SCREEN_BG_COLOR, new CornerRadii(CORNER_RADIUS), Insets.EMPTY)));

        VBox content = new VBox(titleBox, crtBezel);
        VBox.setMargin(crtBezel, new Insets(0, 8, 8, 8));
        getChildren().add(content);
    }

    public void setTitle(String title) {
        titleText.setText(title);
    }

    public final void setText(String text) {
        crtPane.setText(text);
    }

    /**
     * Called each Loop() cycle. Appends all messages except ones intended for
     * next Vignette.
     *
     * @param msgs
     */
    public final void appendCurrentMessages(ArrayList<String> msgs) {
        Collections.unmodifiableList(msgs).forEach((msg) -> {
            if (!msg.startsWith("_")) {
                crtPane.appendLine(msg);
                msgs.remove(msg);
            }
        });
    }

    /**
     * Called at Vignette init. Appends any delayed messages.
     *
     * @param msgs
     */
    public final void appendAllMessages(ArrayList<String> msgs) {
        msgs.forEach((msg) -> {
            if (msg.startsWith("_")) {
                crtPane.appendLine(msg.substring(1));
            } else {
                crtPane.appendLine(msg);
            }
        });

        msgs.clear();
    }

}
