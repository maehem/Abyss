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
package com.maehem.abyss.engine.bbs.widgets;

import com.maehem.abyss.engine.bbs.BBSTerminal;
import static com.maehem.abyss.Engine.LOGGER;
import com.maehem.abyss.engine.GameState;
import java.util.logging.Level;
import javafx.scene.text.Font;

/**
 *
 * @author mark
 */
public class BBSSimpleMenuItem extends BBSText {

    private final Character key;

    public BBSSimpleMenuItem( Font f, String text ) {
        super(f, "   " + text);
        this.key = null;
    }
    
    public BBSSimpleMenuItem( Font f, String text, GameState gs, BBSTerminal dest) {
        super(f, text);
        this.key = null;
        
        setOnMouseClicked((t) -> {
            LOGGER.log(Level.INFO, "User clicked menu item: {0}", dest.getClass().getSimpleName());
            dest.updateContent(gs);
            gs.setCurrentTerminal(dest);
        });
    }
    
//    public BBSSimpleMenuItem(Font f, Character key, String text, 
//            GameState gs, Class<? extends BBSTerminal> tClass ) {
//        super(f, key + ": " + text);
//        this.key = key;
//        
//        setOnMouseClicked((t) -> {
//            LOGGER.log(Level.INFO, "User clicked menu item: " + key);
//            try {
//                BBSTerminal term = tClass.getDeclaredConstructor(GameState.class).newInstance(gs);
//                gs.setCurrentTerminal(term);
//            } catch (NoSuchMethodException ex) {
//                LOGGER.log(Level.SEVERE, null, ex);
//            } catch (SecurityException ex) {
//                LOGGER.log(Level.SEVERE, null, ex);
//            } catch (InstantiationException ex) {
//                LOGGER.log(Level.SEVERE, null, ex);
//            } catch (IllegalAccessException ex) {
//                LOGGER.log(Level.SEVERE, null, ex);
//            } catch (IllegalArgumentException ex) {
//                LOGGER.log(Level.SEVERE, null, ex);
//            } catch (InvocationTargetException ex) {
//                LOGGER.log(Level.SEVERE, null, ex);
//            }
//            
//        });
//    }
    
}
