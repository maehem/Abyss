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

import java.io.InputStream;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Widget that shows a number value. Can have String or Icon as label. Can set
 * decimal places.
 *
 * @author Mark J Koch [@maehem on GitHub]
 */
public class NumberIndicator extends HBox {

    private double value = 0;
    private final int decimals;
    private final Text valueText = new Text();

    /**
     * Basic Gauge -- no extras
     *
     * @param h height of widget. Affects font size.
     * @param value initial value to display.
     */
    public NumberIndicator(double h, double value, int decimals) {
        this.decimals = decimals;

        setValue(value);
        setSpacing(h * 0.1);
        setAlignment(Pos.CENTER_LEFT);

        valueText.setFont(new Font(h*0.66));

        getChildren().add(valueText);

    }

    /**
     * Basic Gauge with String Label
     *
     * @param label
     * @param h
     * @param value
     * @param decimals to show after dot
     */
    public NumberIndicator(String label, double h, double value, int decimals) {
        this(h, value, decimals);

        Text labelText = new Text(label);
        labelText.setFont(new Font(0.8 * h));
        getChildren().add(0, labelText);

    }

    /**
     * Gauge with icon from InputStream
     *
     * @param iconStream
     * @param h
     * @param value
     * @param decimals
     */
    public NumberIndicator(InputStream iconStream, double h, double value, int decimals) {
        this( h, value, decimals);

        // Icon Image
        ImageView icon = new ImageView();
        icon.setImage(new Image(iconStream));
        icon.setPreserveRatio(true);
        icon.setFitHeight(0.8 * h);

        getChildren().add(0, icon);
    }

    /**
     * Update the displayed value.
     *
     * @param val value to show
     */
    public final void setValue(double val) {
        this.value = val < 0 ? 0 : val;
        if ( decimals <= 0 ) {
            valueText.setText(String.format("%d", (int)(this.value)));
        } else {
            valueText.setText(String.format("%." + decimals + "f", this.value));
        }
    }
}
