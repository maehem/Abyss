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
 * Widget that shows a text value. Can have Nothing, String or Icon as label.
 *
 * @author Mark J Koch [@maehem on GitHub]
 */
public class Indicator extends HBox {

    private final Text valueText = new Text("ABCD");

    /**
     * Basic Indicator -- no extras. Use setValue() to change displayed value.
     *
     * @param h height of widget. Affects font size.
     */
    public Indicator(double h) {
        setSpacing(h * 0.1);
        setAlignment(Pos.CENTER_LEFT);

        valueText.setFont(new Font(h*0.8));

        getChildren().add(valueText);
    }

    /**
     * Basic Gauge with String value
     *
     * @param h
     * @param value
     */
    public Indicator(double h, String value) {
        this(h);
        setValueText(value);
    }

    /**
     * Indicator with String Label
     *
     * @param label
     * @param h
     * @param value
     */
    public Indicator(String label, double h, String value) {
        this(h, value);

        Text labelText = new Text(label);
        labelText.setFont(new Font(0.8 * h));
        getChildren().add(0, labelText);

    }

    /**
     * Indicator with icon from InputStream
     *
     * @param iconStream
     * @param h
     * @param value
     */
    public Indicator(InputStream iconStream, double h, String value) {
        this( h, value);

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
    public final void setValueText(String val) {
        this.valueText.setText(val);
    }
}
