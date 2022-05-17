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

import javafx.scene.layout.VBox;

/**
 *
 * @author mark
 */
public class OLEDStatusScreen extends VBox {
    private final DateTimeWidget timeWidget = new DateTimeWidget();
    private final PixelGaugeWidget healthWidget = new PixelGaugeWidget();
    private final PixelGaugeWidget constWidget = new PixelGaugeWidget();
    //private final DSEG7Display moneyWidget = new DSEG7Display(20, 30, 300, 30);
    
    public OLEDStatusScreen() {
        // Add date time section        
        // Add health bar
        // Add const bar
        // Add money section
        getChildren().addAll(timeWidget,healthWidget,constWidget);
        layout();
    }
    
}
