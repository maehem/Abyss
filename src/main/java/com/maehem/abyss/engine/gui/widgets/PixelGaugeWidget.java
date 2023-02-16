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
package com.maehem.abyss.engine.gui.widgets;

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
    private final Text labelText = new Text("XXXX:");
    private final Text capL = new Text("[");
    private final Text capR = new Text("]");
    private final Text fillValText = new Text("#######");
    private final Text blankValText = new Text("---");
    private final Text valueNumText = new Text("999");

    public PixelGaugeWidget(String label, double height, Color c1, Color c2) {
        super(height);
        labelText.setText(label + " ");
        setAlignment(Pos.CENTER);
        setSpacing(2);
        labelText.setFill(c1.darker());
        capL.setFill(c2);
        fillValText.setFill(c2);
        blankValText.setFill(c1);
        capR.setFill(c2);
        valueNumText.setFill(c1.darker());

        font = Font.loadFont(
                //PixelGaugeWidget.class.getResourceAsStream("/fonts/DotMatrix-Regular.ttf"), height);
                PixelGaugeWidget.class.getResourceAsStream("/fonts/VT323-Regular.ttf"), height);
        
        labelText.setFont(font);
        fillValText.setFont(font);
        blankValText.setFont(font);
        capL.setFont(font);
        capR.setFont(font);
        valueNumText.setFont(font);

        getChildren().addAll(labelText, 
                capL, fillValText, blankValText, capR, 
                valueNumText
        );

        setScaleX(1.3);
    }
    
    /**
     * Set value 0-999
     * 
     * @param val 
     */
    public void setValue( int val ) {
        int vv = val/100;
        StringBuilder sbFill = new StringBuilder();
        StringBuilder sbBlank = new StringBuilder();
        for ( int i=0; i<10; i++ ) {
            if ( val > 0 && vv >= i ) {
                sbFill.append("#");
            } else {
                sbBlank.append("»");
            }
        }
        valueNumText.setText(String.format("%03d", val));
        
        fillValText.setText(sbFill.toString());
        blankValText.setText(sbBlank.toString());
    }

}
