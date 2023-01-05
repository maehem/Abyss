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
package com.maehem.flatlinejack.content.sites;

import com.maehem.flatlinejack.engine.GameState;
import com.maehem.flatlinejack.engine.gui.bbs.BBSHeader;
import com.maehem.flatlinejack.engine.gui.bbs.BBSPasswordBody;
import com.maehem.flatlinejack.engine.gui.bbs.BBSTerminal;
import com.maehem.flatlinejack.engine.gui.bbs.BBSText;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author mark
 */
public class AbyssSite extends BBSTerminal {
    private BBSPasswordBody passwordField;
    
    public AbyssSite(GameState gs, int width, int height) { //, int rows, int cols) {
        super(gs); //, width, height, rows, cols);
        setHeader(new BBSHeader(FONT, SiteHeader.ABYSS));
        
        passwordField = new BBSPasswordBody(FONT );//, rows, cols);
        passwordField.setPrefSize(width, height);
        HBox centerBox = new HBox(passwordField);
        centerBox.setAlignment(Pos.CENTER);
        VBox cBox = new VBox(centerBox);
        cBox.setAlignment(Pos.CENTER);
        setBody(cBox);
        setFooter(new BBSText(FONT,
                  "Copyright 2056 Abyss Technology         "
                + "                DJKE KIVN 99GJ JJKE JJ89"
        ));
        
        passwordField.setPrefSize(getPrefWidth(), getPrefHeight());
        
    }
    
}
