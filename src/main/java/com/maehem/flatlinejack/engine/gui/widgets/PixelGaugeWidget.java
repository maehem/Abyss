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
package com.maehem.flatlinejack.engine.gui.widgets;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author mark
 */
public class PixelGaugeWidget extends HBox {

    private final Font font;
    private final Text labelText = new Text("XX:");
    private final Text valueText = new Text("[#######------]");

    public PixelGaugeWidget(double height) {
        super(height);
        setAlignment(Pos.CENTER);
        setSpacing(2);
        labelText.setFill(Color.LIGHTBLUE);
        valueText.setFill(Color.LIGHTBLUE);

        font = Font.loadFont(
                PixelGaugeWidget.class.getResourceAsStream("/fonts/DotMatrix-Regular.ttf"), height);
        
        labelText.setFont(font);
        valueText.setFont(font);

        getChildren().addAll(labelText, valueText);

    }

}
