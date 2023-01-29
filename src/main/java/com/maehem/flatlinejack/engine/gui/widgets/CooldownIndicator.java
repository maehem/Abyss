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

import static com.maehem.flatlinejack.Engine.LOGGER;
import java.util.logging.Level;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;

/**
 * Animated indicator showing cooldown. A round pie-like circle.
 *
 * @author mark
 */
public class CooldownIndicator extends HBox {

    private final double maxValue;
    private final Arc arc;

    public CooldownIndicator(double height, double value, double maXValue) {
        this.maxValue = maXValue;
//Setting the properties of the arc 
        float rad = (float)height/2.0f;
        float piD = rad-1.0f;
        arc = new Arc(rad, rad, piD, piD, 90.0f, 0.0);
        arc.setType(ArcType.ROUND);
        arc.setFill(Color.LIGHTGRAY);
        //arc.setLength(-120.0f);

        Circle ring = new Circle(rad, rad, rad);
        //Arc ring = new Arc(RAD, RAD, RAD, RAD, 0, 360);
        ring.setFill(Color.DARKGREY.darker());
        ring.setStroke(Color.BLACK);
        ring.setStrokeWidth(1.0);
        //ring.setType(ArcType.ROUND);
        
        Pane pane = new Pane(ring, arc);
        pane.setPrefSize(height, height);
        pane.setMaxSize(height, height);
        getChildren().add(pane);
        
        setValue(value);
    }

    public void setValue(double value) {
        float arcL;
        if ( value > maxValue ) {
            arcL = 360.0f;
        } else if ( value < 0 ) {
            arcL = 0.0f;
        } else {
            arcL = 360.0f * (float)(value/maxValue);
        }
        arc.setLength(-arcL);

        if ( value <= 0.85*maxValue ) {
            arc.setFill(Color.LIGHTGRAY);
        } else if ( value <= 0.99*maxValue ) {
            arc.setFill(Color.YELLOW);
        } else {
            arc.setFill(Color.LIMEGREEN);
        }        
    }
}
