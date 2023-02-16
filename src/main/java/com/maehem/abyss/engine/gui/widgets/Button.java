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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class Button extends StackPane {

    private static final double BTN_STROKE_W = 8;
    public Button(double w, double h, InputStream is) {
        Rectangle rect = new Rectangle(w, h, Color.SLATEGRAY);
        rect.setStroke(Color.DARKSLATEGRAY);
        
        // Icon Image
        ImageView icon = new ImageView();
        icon.setImage(new Image(is));
        icon.setPreserveRatio(true);
        icon.setFitWidth(h*0.66);
        
        showHighlight(false);
        
        getChildren().addAll(rect, icon);

        setOnMouseEntered((event) -> {
            rect.setFill(Color.LIGHTSLATEGRAY);
        });
        
        setOnMouseExited((event) -> {
            rect.setFill(Color.SLATEGRAY);
        });        
    }
            

    public void setGreyout( boolean g ) {
        setDisable(g);
        setOpacity(g?0.3:1.0);
    }
    
    public final void showHighlight(boolean highlight) {
        if ( highlight ) {
            setBorder(new Border(new BorderStroke(new Color(0.1,0.1,1.0,1.0), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(BTN_STROKE_W))));
        } else {
            setBorder(new Border(new BorderStroke(Color.DARKGREY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(BTN_STROKE_W))));            
        }
    }
    
}
