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

import com.maehem.abyss.engine.view.ViewPane;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 *
 * @author mark
 */
public class GUIPane extends HBox {

    public GUIPane() {
        setBackground(new Background(new BackgroundFill(
                Color.DARKGREY, CornerRadii.EMPTY, Insets.EMPTY
        )));
        setBorder(new Border(new BorderStroke(
                Color.DARKGREY.darker(), 
                BorderStrokeStyle.SOLID, 
                new CornerRadii(10.0), 
                new BorderWidths(10.0)
        )));
        setSpacing(8);
        setPrefHeight(ViewPane.HEIGHT/3);
    }
    
}
