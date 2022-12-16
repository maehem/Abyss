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

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 *
 * @author mark
 */
public class OLEDStatusScreen extends DecoBox {
    private final DateTimeWidget timeWidget = new DateTimeWidget(22);
    private final PixelGaugeWidget healthWidget = new PixelGaugeWidget(20);
    private final PixelGaugeWidget constWidget = new PixelGaugeWidget(20);
    
    public OLEDStatusScreen() {
        setPrefWidth(280);
        setAlignment(Pos.CENTER);
        setBackground(new Background(new BackgroundFill(new Color(0.1,0.1,0.1,1.0), CornerRadii.EMPTY, Insets.EMPTY)));
        
        VBox content = new VBox(
                timeWidget,healthWidget,constWidget
        );
        content.setSpacing(8);
        getChildren().add(content);
        //layout();
    }
    
}
