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
package com.maehem.flatlinejack.engine;

import com.maehem.flatlinejack.Engine;
import static com.maehem.flatlinejack.Engine.log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class GameState extends Properties {
    // KEYS
    public static final String PROP_CURRENT_VIGNETTE = "game.vignette";
    
    private final File gameSaveFile = new File(
            System.getProperty("user.home") 
            + File.separator + "frustumGameState"
    );

    public void quickSave() {
        FileOutputStream out = null;
        try {
            // TODO:  Backup current save file.
            out = new FileOutputStream(gameSaveFile);
            
            store(out, "Game Save");
            log.log(Level.WARNING, "Game State saved at: {0}", gameSaveFile.getAbsolutePath());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GameState.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GameState.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(GameState.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void load( String defaultVignetteName ) {
        try {
            FileInputStream in = new FileInputStream(gameSaveFile);
            load(in);
        } catch (FileNotFoundException ex) {
            log.config("No previous game state.  New Game.\n");
            
            setProperty(PROP_CURRENT_VIGNETTE, defaultVignetteName);  // Game starting room
            //setProperty(PLAYER_MONEY, String.valueOf(PLAYER_MONEY_AMOUNT_DEFAULT));
            
        } catch (IOException ex) {
            Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
