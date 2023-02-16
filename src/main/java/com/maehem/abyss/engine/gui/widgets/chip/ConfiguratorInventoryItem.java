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

import com.maehem.abyss.engine.GameState;
import com.maehem.abyss.engine.SkillChipThing;
import java.io.InputStream;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/**
 *
 * @author mark
 */
public class ConfiguratorInventoryItem extends HBox {

    private final double GLYPH_DIM = 40;

    private final Border DARK_BORDER = new Border(new BorderStroke(
            Color.BLACK,
            BorderStrokeStyle.SOLID,
            new CornerRadii(4),
            new BorderWidths(2)
    ));
    private final Border SELECTED_BORDER = new Border(new BorderStroke(
            Color.RED,
            BorderStrokeStyle.SOLID,
            new CornerRadii(4),
            new BorderWidths(2)
    ));

    public ConfiguratorInventoryItem(GameState gameState, SkillChipThing t) {
        setBackground(new Background(new BackgroundFill(
                Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY
        )));
        setSpacing(10);
        setSelected(false);

        StackPane slotId = new StackPane();
        slotId.setPrefSize(GLYPH_DIM, GLYPH_DIM);
        slotId.setMinSize(GLYPH_DIM, GLYPH_DIM);
        slotId.setMaxSize(GLYPH_DIM, GLYPH_DIM);
        slotId.setBackground(new Background(new BackgroundFill(
                new Color(0.25, 0.25, 0.25, 1.0),
                CornerRadii.EMPTY,
                Insets.EMPTY)));
        slotId.setBorder(new Border(new BorderStroke(
                new Color(0.16, 0.16, 0.16, 1.0),
                BorderStrokeStyle.SOLID,
                new CornerRadii(0, 5, 5, 0, false),
                new BorderWidths(0, 6, 0, 0)
        )));
        Text slotN = new Text("0");
        slotN.setFill(new Color(0.76, 0.76, 0.76, 1.0));
        slotN.setFont(new Font(GLYPH_DIM * 0.6));
        slotId.getChildren().add(slotN);

        Text titleText = new Text(t.getName());
        titleText.setFill(Color.SPRINGGREEN);
        titleText.setFont(new Font(GLYPH_DIM * 0.6));
        TextFlow titleFlow = new TextFlow(titleText);
        titleFlow.setTextAlignment(TextAlignment.CENTER);

        String iconPath = t.getIconPath();
        InputStream imgStream;
        if (iconPath.startsWith("/content")) {
            // Use class loader from content JAR.
            imgStream = gameState.getContentLoader().getStream(iconPath);
        } else {
            // Use class loader from engine JAR.
            imgStream = getClass().getResourceAsStream(iconPath);
        }
        Image icon = new Image(imgStream);
        ImageView itemIconView = new ImageView(icon);
        itemIconView.setFitHeight(GLYPH_DIM * 0.94);
        itemIconView.setPreserveRatio(true);
        StackPane itemIcon = new StackPane(itemIconView);
        itemIcon.setPrefSize(GLYPH_DIM, GLYPH_DIM);
        itemIcon.setMinSize(GLYPH_DIM, GLYPH_DIM);
        itemIcon.setMaxSize(GLYPH_DIM, GLYPH_DIM);
        itemIcon.setBackground(new Background(new BackgroundFill(
                t.getColor(),
                new CornerRadii(5),
                Insets.EMPTY)));

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        getChildren().addAll(slotId, titleFlow, spacer, itemIcon);
    }

    public final void setSelected(boolean selected) {
        if (selected) {
            setBorder(SELECTED_BORDER);
        } else {
            setBorder(DARK_BORDER);
        }
    }

}
