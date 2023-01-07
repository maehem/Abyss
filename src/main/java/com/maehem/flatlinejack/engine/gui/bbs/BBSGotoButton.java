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

import static com.maehem.flatlinejack.Engine.LOGGER;

import com.maehem.flatlinejack.engine.GameState;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

/**
 *
 * @author mark
 */
public class BBSGotoButton extends StackPane {
    
    public BBSGotoButton(Font f, String text, GameState gs, Class<? extends BBSTerminal> tClass) {
        
        BBSText loginButtonText = new BBSText(f, text);
        this.getChildren().add(loginButtonText);
        
        setPadding(new Insets(8,12,8,12));
        setAlignment(Pos.CENTER);
        setBorder(new Border(new BorderStroke(
                BBSText.FILL_COLOR, 
                BorderStrokeStyle.SOLID, 
                CornerRadii.EMPTY, 
                new BorderWidths(3)
        )));
        setOnMouseClicked((t) -> {
            try {
                BBSTerminal term = tClass.getDeclaredConstructor(GameState.class).newInstance(gs);
                gs.setCurrentTerminal(term);
            } catch (NoSuchMethodException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        });
    }
    
}
