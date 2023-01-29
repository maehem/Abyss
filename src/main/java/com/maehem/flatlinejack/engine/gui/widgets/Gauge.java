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

import java.io.InputStream;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Gauge that shows health, constitution, etc.
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class Gauge extends HBox {

    public enum ValueLabel {
        NONE, OVERLAY_CENTERED, END
    }
    
    private final ProgressBar pb = new ProgressBar();
    private final Text valueText = new Text();

    private double value;
    private double maxValue;

    /**
     * Basic Gauge -- no extras
     *
     * @param w
     * @param h
     * @param value
     * @param maxValue
     */
    public Gauge(double w, double h, double value, double maxValue, ValueLabel pos) {
        this.maxValue = maxValue;
        pb.setStyle("-fx-accent: gray");

        setValue(value);
        setSpacing(h * 0.2);
        setAlignment(Pos.CENTER_LEFT);

        valueText.setFont(new Font(h * 0.7));

        switch (pos) {
            case NONE:
                getChildren().add(pb);
                break;
            case END:
                getChildren().addAll(pb, valueText);
                break;
            case OVERLAY_CENTERED:
                StackPane pbPane = new StackPane(pb, valueText);
                getChildren().add(pbPane);
                valueText.setFill(new Color(0.5, 0.5, 0.5, 0.8));
                break;
        }
    }

    /**
     * Gauge, bare-bones, with no current value text shown.
     *
     * @param w
     * @param h
     * @param value
     * @param maxValue
     */
    public Gauge(double w, double h, double value, double maxValue) {
        this(w, h, value, maxValue, ValueLabel.NONE);
    }

    /**
     * Basic Gauge with String Label
     *
     * @param label
     * @param w
     * @param h
     * @param value
     * @param maxValue
     */
    public Gauge(String label, double w, double h, double value, double maxValue, ValueLabel valLabel) {
        this(w, h, value, maxValue, valLabel);

        Text labelText = new Text(label);
        labelText.setFont(new Font(0.8 * h));
        getChildren().add(0, labelText);
    }

    /**
     * Gauge with icon from InputStream
     *
     * @param iconStream
     * @param w
     * @param h
     * @param value
     * @param maxValue
     */
    public Gauge(InputStream iconStream, double w, double h, double value, double maxValue, ValueLabel valLabel) {
        this(w, h, value, maxValue, valLabel);

        //pb.setLayoutX(h);
        pb.setPrefSize(w, h);

        // Icon Image
        ImageView icon = new ImageView();
        icon.setImage(new Image(iconStream));
        icon.setPreserveRatio(true);
        icon.setFitHeight(0.8 * h);

        getChildren().add(0, icon);
    }

    public void setValue(double val) {
        this.value = val < 0 ? 0 : val;
        valueText.setText(String.valueOf(this.value));

        if (val <= 0) {
            pb.setProgress(-1);  // -1 will animate bar "scanning mode"
        } else {
            pb.setProgress(value / maxValue);
        }

        if (pb.getProgress() <= 0.2) {
            pb.setStyle("-fx-text-box-border: #555555;"
                    + " -fx-control-inner-background: #444444;"
                    + " -fx-accent: red");
        } else if (pb.getProgress() <= 0.5) {
            pb.setStyle("-fx-text-box-border: #555555;"
                    + " -fx-control-inner-background: #444444;"
                    + " -fx-accent: gold");
        } else {
            pb.setStyle("-fx-text-box-border: #555555;"
                    + " -fx-control-inner-background: #444444;"
                    + " -fx-accent: lime");
        }

    }
}
