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
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Seven segment digital display based on DSEG7 open source font,
 * more info about this wonderful font set: https://www.keshikan.net/fonts-e.html
 * Fonts have their own licensing terms.
 * 
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class DSEG7Display extends Group {
    //private String text="0000000";
    private static final String DEFAULT_TEXT = "-------";
    private static final String FONT = "/fonts/DSEG7Classic-BoldItalic.ttf";
    private final Text fgText = new Text(DEFAULT_TEXT);
    
    public DSEG7Display(double x, double y, double w, double h, InputStream iconStream) {
        this(x, y, w, h);
        
        // Icon Image
        ImageView icon = new ImageView();
        icon.setImage(new Image(iconStream));
        icon.setPreserveRatio(true);
        icon.setFitWidth(h);        
        getChildren().add(icon);
        
    }

    public DSEG7Display(double x, double y, double w, double h, Character symbolChar, String textVal) {
        this(x, y, w, h);
        
        // Symbol
        Text symbol = new Text(0, h * 0.85, symbolChar.toString());
        symbol.setFont(new Font(h));
        symbol.setFill(Color.DARKSLATEGRAY);
        getChildren().add(symbol);

        setText(textVal);
    }
    
    public DSEG7Display(double x, double y, double w, double h) {
        setLayoutX(x);
        setLayoutY(y);

        
        // Load the seven segment font
        Font calcFont = Font.loadFont(DSEG7Display.class.getResource(FONT).toExternalForm(),
                h*0.65
        );
        
        // Background Box
        Rectangle background = new Rectangle(h, 0, w - h, h);
        background.setStrokeWidth(2.0);
        background.setStroke(Color.DARKSLATEGRAY);
        background.setFill(Color.OLIVEDRAB);
        getChildren().add(background);

        // Box text
//        Text t = new Text(30, h, text);
//        t.setFont(calcFont);
//        t.setFill(Color.DARKSLATEGRAY);
//        VBox textBox = new VBox(t);
//        textBox.setPrefHeight(h);
//        textBox.setAlignment(Pos.CENTER_RIGHT);
//        textBox.setPrefWidth(w - 2.0);

        // Draw the ghost outlines of the OFF segments.
        Color offColors = Color.DARKSLATEGRAY.deriveColor(1, 1, 1, 0.2);
        //VBox calcBack = getCalcBox("8888888", w, h, offColors);
        Text bgText = new Text("8888888");
        bgText.setFont(calcFont);
        bgText.setFill(offColors);
        VBox calcBack = new VBox(bgText);
        calcBack.setAlignment(Pos.CENTER_RIGHT);
        calcBack.setPrefWidth(w - 2.0);
        calcBack.setPrefHeight(h);
       
        // Draw the on segments.
        Color onColors = Color.DARKSLATEGRAY;
        fgText.setFont(calcFont);
        fgText.setFill(onColors);
        VBox calcVal = new VBox(this.fgText);
        calcVal.setAlignment(Pos.CENTER_RIGHT);
        calcVal.setPrefWidth(w - 2.0);
        calcVal.setPrefHeight(h);
        
        getChildren().addAll(calcBack,calcVal);
    }
    

    /**
     * @return the text
     */
    public String getText() {
        return fgText.getText();
    }

    /**
     * @param textVal the text to set
     */
    public final void setText(String textVal) {
        this.fgText.setText(textVal);
    }

}
