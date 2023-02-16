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

import java.io.InputStream;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author mark
 */
public class CrtLabelPane extends StackPane {

    public static final double LABEL_WIDTH = 200;
    private static final String LABEL_IMAGE = "/ui/crt-label-1.png";
    private static final double FONT_H = 16;
    private static final Color FONT_COLOR = Color.BLACK;
    private static final String FONT = "/fonts/RockSalt-Regular.ttf";
    Font font = Font.loadFont(this.getClass().getResource(FONT).toExternalForm(),
            FONT_H
    );
    private final Text text = new Text();
    
    public CrtLabelPane() {
        InputStream is = getClass().getResourceAsStream(LABEL_IMAGE);
        // set background
        ImageView labelView = new ImageView(new Image(is));
        labelView.setPreserveRatio(true);
        labelView.setFitWidth(LABEL_WIDTH);
        labelView.setSmooth(true);
        labelView.setOpacity(0.7);

        text.setFill(FONT_COLOR);
        text.setFont(font);

        getChildren().addAll(labelView,text);
        setBackground(Background.EMPTY);
    }
    
    
    public void setTitle( String text ) {
        this.text.setText(text);        
    }
}
