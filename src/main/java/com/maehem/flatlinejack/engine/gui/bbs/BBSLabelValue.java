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
package com.maehem.flatlinejack.engine.gui.bbs;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

/**
 *
 * @author mark
 */
public class BBSLabelValue extends HBox {
    private final BBSText valText;
    
    public BBSLabelValue( Font f, String label, String val, double boxWidth ) {
        HBox lblText = new HBox(new BBSText(f, label + ":"));
        lblText.setPadding(new Insets(8,12,8,12));
        lblText.setAlignment(Pos.CENTER);

        setAlignment(Pos.BASELINE_CENTER);
        valText = new BBSText(f, val);
        HBox valTextBox = new HBox(valText);
        valTextBox.setMinWidth(boxWidth);
        valTextBox.setPrefWidth(boxWidth);
        valTextBox.setPadding(new Insets(8,12,8,12));
        valTextBox.setAlignment(Pos.CENTER);
        valTextBox.setBorder(new Border(new BorderStroke(
                BBSText.FILL_COLOR, 
                BorderStrokeStyle.SOLID, 
                CornerRadii.EMPTY, 
                new BorderWidths(3)
        )));
        getChildren().addAll(lblText, valTextBox);
    }
    
    public void setValue( String val ) {
        valText.setText(val);
    }
    
}
