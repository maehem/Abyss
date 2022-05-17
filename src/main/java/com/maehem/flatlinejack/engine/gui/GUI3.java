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
package com.maehem.flatlinejack.engine.gui;

import com.maehem.flatlinejack.Engine;
import com.maehem.flatlinejack.engine.gui.widgets.GUIButtonsPane;
import com.maehem.flatlinejack.engine.gui.widgets.OLEDStatusScreen;
import javafx.geometry.Insets;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 *
 * @author mark
 */
public class GUI3 extends HBox {
    private final StackPane decoBox = new StackPane();
    private final OLEDStatusScreen status = new OLEDStatusScreen();
    private final GUIButtonsPane buttons = new GUIButtonsPane();
    
    public GUI3( Engine engine) {
        final StackPane decoInternal = new StackPane();
        decoInternal.setPrefWidth(20);
        setPadding(new Insets(4));
        decoBox.getChildren().add(decoInternal);
        decoInternal.setBorder(new Border(new BorderStroke(
                Color.DARKGREY, 
                BorderStrokeStyle.SOLID, 
                CornerRadii.EMPTY, 
                new BorderWidths(2)
        )));
        setBorder(new Border(new BorderStroke(
                Color.BLACK, 
                BorderStrokeStyle.SOLID, 
                new CornerRadii(10.0), 
                new BorderWidths(10.0)
        )));
        decoBox.setPadding(new Insets(10));
        
        
        getChildren().addAll(decoBox, buttons, status);
    }
    
    public void refresh() {
        
    }
}
