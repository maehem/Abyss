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
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Gauge that shows health, constitution, etc.
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class Gauge extends Pane {  // TODO make this a stackpane?
    //private final Rectangle valueBar = new Rectangle();

    private double value = 0;
    private double maxValue;
    //private final double increment;
    //private final Circle valueCircle = new Circle();
    private final ProgressBar pb = new ProgressBar();
    private final ProgressIndicator pi = new ProgressIndicator(0);

    public Gauge(double x, double y, double w, double h, double offsetX, double offsetY, InputStream iconStream, double value, double maxValue) {
        this(x, y, w, h, offsetX, offsetY, value, maxValue);

        // Icon Image
        ImageView icon = new ImageView();
        icon.setImage(new Image(iconStream));
        icon.setPreserveRatio(true);
        icon.setFitWidth(h);

        pb.setLayoutX(icon.getFitWidth());
        getChildren().add(icon);

    }

    public Gauge(double x, double y, double w, double h, double offsetX, double offsetY, Character symbolChar, double value, double maxValue) {
        this(x, y, w, h, offsetX, offsetY, value, maxValue);

        // Symbol
        Text symbol = new Text(0, 0 + h * 0.85, symbolChar.toString());

        symbol.setFont(new Font(h));
        symbol.setFill(Color.DARKSLATEGRAY);
        getChildren().add(symbol);

    }

    /**
     * Gauge with summary circle (for popup menu)
     *
     * @param x
     * @param y
     * @param w
     * @param h
     * @param offsetX
     * @param offsetY
     * @param value
     * @param maxValue
     */
    private Gauge(double x, double y, double w, double h, double offsetX, double offsetY, double value, double maxValue) {
        this(w, h, value, maxValue);

        setLayoutX(x);
        setLayoutY(y);

        Line lh = new Line(w, h / 2, w + offsetX, h / 2);
        lh.setStroke(Color.DARKSLATEGRAY);
        lh.setStrokeWidth(1.0);
        Line lv = new Line(w + offsetX, h / 2, w + offsetX, h / 2 + offsetY);
        lv.setStroke(Color.DARKSLATEGRAY);
        lv.setStrokeWidth(1.0);

        getChildren().addAll(lh, lv);

//        //valueCircle = new Circle(w+offsetX, h/2+offsetY, 10);
//        valueCircle.setCenterX(w+offsetX);
//        valueCircle.setCenterY(h/2+offsetY);
//        valueCircle.setRadius(10);
//        valueCircle.setStroke(Color.DARKSLATEGRAY);
//        valueCircle.setStrokeWidth(2.0);
//        valueCircle.setFill(Color.LIMEGREEN);
//        getChildren().add(valueCircle);
        getChildren().add(pi);
        pi.setScaleX(0.7);
        pi.setScaleY(0.7);
        pi.setLayoutY(offsetY);
        pi.setLayoutX(w + offsetX);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren(); //To change body of generated methods, choose Tools | Templates.

        // Final width is not known until its layed out. So we bump
        // the widget over after layout is done.
        pi.setTranslateX(-pi.getWidth() / 2);
        pi.setTranslateY(-pi.getHeight() / 10);
    }

    /**
     * Basic Gauge -- no added summary circle detail
     *
     * @param w
     * @param h
     * @param value
     * @param maxValue
     */
    public Gauge(double w, double h, double value, double maxValue) {
        this.maxValue = maxValue;

//        double strokeWidth = 2.0;
//        // Background Box
//        Rectangle background = new Rectangle(h, 0, w-h, h);
//        background.setStrokeWidth(strokeWidth);
//        background.setStroke(Color.DARKSLATEGRAY);
//        background.setFill(Color.LIGHTSLATEGRAY);        
//        getChildren().add(background);
//        
//        increment = (w-h-(2*strokeWidth))/maxValue;
//        
//        //Rectangle valueBar = new Rectangle(h+strokeWidth, strokeWidth, value*increment, h-(2*strokeWidth));
//        valueBar.setX(h+strokeWidth);
//        valueBar.setY(strokeWidth);
//        //valueBar.setWidth(0.1);
//        valueBar.setHeight(h-(2*strokeWidth));
        //valueBar.setStrokeWidth(2.0);
        //valueBar.setStroke(Color.GRAY);
        //valueBar.setFill(Color.RED);        
        //getChildren().add(valueBar);
        //pb = new ProgressBar(value/maxValue);
        pb.setStyle("-fx-accent: gray");
        getChildren().add(pb);
        setValue(value);
    }

    public Gauge(String label, double w, double h, double value, double maxValue) {
        this(w, h, value, maxValue);

        Text labelText = new Text(label);
        labelText.setFont(new Font(16));
        getChildren().add(labelText);
        pb.setLayoutX(labelText.getBoundsInLocal().getWidth());
        //labelText.setLayoutX(-labelText.getBoundsInLocal().getWidth());
        labelText.setLayoutY(h * 0.85);

    }

    public void setValue(double val) {
//        valueBar.setWidth(val*increment);
        this.value=val<0?0:val;
       
        if ( val <= 0 ) {
            pb.setProgress(-1);
        
            // +1 makes sure indicator stops at 99% to prevent "done" glyph(checkmark)
            pi.setProgress(0); 
        } else {
            pb.setProgress(value / maxValue);
        
            // +1 makes sure indicator stops at 99% to prevent "done" glyph(checkmark)
            pi.setProgress(value / (maxValue+1)); 
        }
        
        Text text = (Text) pi.lookup(".percentage");
        if ( text != null ) {
            text.setText("");
        }

        if (pb.getProgress() <= 0.2) {
            pb.setStyle("-fx-accent: red");
            pi.setStyle("-fx-accent: red");
        } else if (pb.getProgress() <= 0.5) {
            pb.setStyle("-fx-accent: gold");
            pi.setStyle("-fx-accent: gold");
        } else {
            pb.setStyle("-fx-accent: lime");
            pi.setStyle("-fx-accent: lime");
        }

//        if ( val <= 0.15*maxValue ) {
//            valueBar.setFill(Color.RED);
//            valueCircle.setFill(Color.RED);
//        } else if ( val <= 0.40*maxValue ) {
//            valueBar.setFill(Color.YELLOW);
//            valueCircle.setFill(Color.YELLOW);
//        } else {
//            valueBar.setFill(Color.GREEN);
//            valueCircle.setFill(Color.GREEN);
//        }        
    }
}
