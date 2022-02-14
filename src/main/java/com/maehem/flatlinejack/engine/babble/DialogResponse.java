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
package com.maehem.flatlinejack.engine.babble;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

/**
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class DialogResponse extends Button {
    private final Rectangle box = new Rectangle();
    private final DialogResponseAction action;

    public DialogResponse(String t, DialogResponseAction action) {
        super(t);
        this.action = action;
        setFont(new Font(16.0));
        
        setAlignment(Pos.CENTER);
                
        setOnMouseClicked((event) -> {
            event.consume();
            action.doResponseAction();
        });
        
        //getChildren().addAll(box, text);        
    }
    
}
