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
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class GameState extends Properties {
    
    ArrayList<GameStateListener> listenters = new ArrayList<>();
    // KEYS
    public static final String PROP_CURRENT_VIGNETTE = "game.vignette";

    private Vignette currentVignette;
    private final Player player;
    private boolean showInventory = false;
    private boolean showChips = false;
    
    private final File gameSaveFile = new File(
            System.getProperty("user.home") 
            + File.separator + "Documents"
            + File.separator + "FlatlineJack"
            + File.separator + "save-0.properties"
    );
//    private final File gameLoadFile = new File(
//            System.getProperty("user.home") 
//            + File.separator + "Documents"
//            + File.separator + "FlatlineJack"
//            + File.separator + "save-202212131234.properties"
//    );

    public GameState() {
        this.player = new Player(this);
    }

    @Override
    public String getProperty(String key) {
        if ( key.startsWith(Player.PLAYER_KEY ) ) {
            return player.getProperty(key);
        } else {
            return super.getProperty(key);
        }
    }

    
    public void quickSave() {
        player.saveState(this);
        
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
            Properties ldProps = new Properties();
            ldProps.load(in);
            log.log(Level.CONFIG, "Loaded previous save file: " + gameSaveFile.getAbsolutePath());
            
            
            setProperty(PROP_CURRENT_VIGNETTE, ldProps.getProperty(PROP_CURRENT_VIGNETTE));  // Game starting room
            
            // Player tracks it's own properties. 
            player.loadState(ldProps);
            
            ldProps.keys().asIterator().forEachRemaining( (t) -> {
                String key = (String) t;
                // Intake Vignette flag
                if ( key.startsWith(Vignette.PROP_PREFIX) ) {
                    log.log(Level.INFO, "Load Vignette prop: " + key);
                    setProperty(key, ldProps.getProperty(key));
                }                
            });
            
        } catch (FileNotFoundException ex) {
            log.config("No previous game state.  New Game.\n");            
            setProperty(PROP_CURRENT_VIGNETTE, defaultVignetteName);  // Game starting room                        
        } catch (IOException ex) {
            Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for (GameStateListener l: listenters) {
            for ( String s: stringPropertyNames() ) {
                l.gameStatePropertyChanged(this, s);                
            }
        }
    }

    @Override
    public synchronized Object setProperty(String key, String value) {
        Object prevVal = super.setProperty(key, value);
        for ( GameStateListener l: listenters ) {
            l.gameStatePropertyChanged(this, key);
        }
        
        return prevVal;
    }
    
    public void addListenter( GameStateListener l ) {
        listenters.add(l);
    }
    
    public void removeListener( GameStateListener l ) {
        listenters.remove(l);
    }
    
    public Vignette getCurrentVignette() {
        return currentVignette;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public void setCurrentVignette( Vignette v ) {
        this.currentVignette = v;
        setProperty(PROP_CURRENT_VIGNETTE, v.getClass().getSimpleName());
        
        // notify vignette change.
        for ( GameStateListener l: listenters ) {
            l.gameStateVignetteChanged(this);
        }
    }
    
    public void setShowInventory( boolean show ) {
        this.showInventory = show;
        for ( GameStateListener l: listenters ) {
            l.gameStateShowInventory(this, showInventory);
        }
    }
    
    public boolean inventoryShowing() {
        return showInventory;
    }
    
    public void toggleInventoryShowing() {
        setShowChips(false);
        setShowInventory(!showInventory);
    }
    
    public void setShowChips( boolean show ) {
        this.showChips = show;
        for ( GameStateListener l: listenters ) {
            l.gameStateShowChips(this, showChips);
        }
    }
    
    public boolean chipsShowing() {
        return showChips;
    }
    
    public void toggleChipsShowing() {
        setShowInventory(false);
        setShowChips(!showChips);
    }
    
    public void notifyPlayerStateChanged( String key ) {
        for (GameStateListener l: listenters) {
            l.gameStatePropertyChanged(this, key);                
        }
    }
    
}
