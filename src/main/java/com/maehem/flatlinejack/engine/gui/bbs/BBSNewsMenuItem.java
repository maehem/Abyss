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
import javafx.scene.text.Font;

/**
 *
 * @author mark
 */
public class BBSNewsMenuItem extends BBSText {

    private final Character key;
    
    public BBSNewsMenuItem(Font f, Character key, String text, String uid,
            GameState gs ) {
        super(f, key + ": " + text);
        this.key = key;
        
        setOnMouseClicked((t) -> {
            LOGGER.log(Level.INFO, "User clicked news item: " + key);
            //try {
                //BBSTerminal term = tClass.getDeclaredConstructor(GameState.class).newInstance(gs);
                BBSNewsReader term = new BBSNewsReader(gs, uid);
                gs.setCurrentTerminal(term);
                
//            } catch (SecurityException ex) {
//                LOGGER.log(Level.SEVERE, null, ex);
//            } catch (IllegalArgumentException ex) {
//                LOGGER.log(Level.SEVERE, null, ex);
//            }
            
        });
    }
    
}
