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
import javafx.scene.text.TextAlignment;

/**
 *
 * @author mark
 */
public class DateTimeWidget extends HBox {

    private final Font  font;
    private final Text  text = new Text();

    public DateTimeWidget( double height ) {
        setAlignment(Pos.CENTER);
        font = Font.loadFont(
            PixelGaugeWidget.class.getResourceAsStream("/fonts/DotMatrix-Bold.ttf"), height);
        text.setFont(font);
        //text.setTextAlignment(TextAlignment.CENTER);
        text.setText("2044/11/24  22:22");
        text.setFill(Color.LIGHTBLUE);
        getChildren().add(text);
    }
    
}
