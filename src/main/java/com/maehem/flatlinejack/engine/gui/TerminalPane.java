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

import com.maehem.flatlinejack.engine.GameState;
import com.maehem.flatlinejack.engine.gui.bbs.BBSTerminal;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 *
 * @author mark
 */
public class TerminalPane extends StackPane {

    private BBSTerminal terminal;
    public TerminalPane( GameState gs, int w, int h ) {
        this.setPrefSize(w, h);
        
        setBackground(new Background(
                new BackgroundFill(Color.BLACK, 
                        CornerRadii.EMPTY, 
                        Insets.EMPTY)
        ));
        terminal = new BBSTerminal(gs, w, h, 25, 80);
        getChildren().add(terminal);
    }
    
    public BBSTerminal getTerminal() {
        return terminal;
    }
}
