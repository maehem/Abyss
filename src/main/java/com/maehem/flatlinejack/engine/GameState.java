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

import static com.maehem.flatlinejack.Engine.LOGGER;

import com.maehem.flatlinejack.Engine;
import static com.maehem.flatlinejack.Engine.LOGGER;
import com.maehem.flatlinejack.content.sites.PublicTerminalSystem;
import com.maehem.flatlinejack.engine.gui.bbs.BBSTerminal;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
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
    private static final List<String> DEFAULT_NEWS = List.of("1", "12343");

    private final Player player;
    private final ArrayList<NewsStory> news = new ArrayList<>();
    
    private Vignette currentVignette;
    private BBSTerminal currentTerminal;
    private int newsIndex = 0;
    
    private ResourceBundle bundle;
    
    private boolean showInventory = false;
    private boolean showChips = false;
    private boolean showTerminal = false;
    private boolean showDebug = true;
    
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
    
    // Debug toggles
    public boolean showWalkPerimeter = false;

    public GameState() {
        this.player = new Player(this);
        this.currentTerminal = new PublicTerminalSystem(this);
        
        initNews();
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
        
        saveNewsSettings();
        
        FileOutputStream out = null;
        try {
            // TODO:  Backup current save file.
            out = new FileOutputStream(gameSaveFile);
            
            store(out, "Game Save");
            LOGGER.log(Level.WARNING, "Game State saved at: {0}", gameSaveFile.getAbsolutePath());
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
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
            LOGGER.log(Level.CONFIG, 
                    "Loaded previous save file: {0}", 
                    gameSaveFile.getAbsolutePath());
            
            
            setProperty(PROP_CURRENT_VIGNETTE, 
                    ldProps.getProperty(PROP_CURRENT_VIGNETTE)  // Game starting room
            );
            // Player tracks it's own properties. 
            player.loadState(ldProps);
            
            ldProps.keys().asIterator().forEachRemaining( (t) -> {
                String key = (String) t;
                // Intake Vignette flag
                if ( key.startsWith(Vignette.PROP_PREFIX) ) {
                    LOGGER.log(Level.INFO, "Load Vignette prop: " + key);
                    setProperty(key, ldProps.getProperty(key));
                }
                if ( key.startsWith(NewsStory.PROP_PREFIX) ) {
                    LOGGER.log(Level.INFO, "Load NewsStory prop: " + key);
                    String uid = key.split("\\.")[1];
                    NewsStory newsStory = getNewsStory(uid);
                    if ( newsStory != null ) {
                        String property = ldProps.getProperty(key);
                        if ( property.contains("show") ) {
                            newsStory.setShow(true);
                        }
                        if ( property.contains("read") ) {
                            newsStory.setRead(true);
                        }
                    } else {
                        LOGGER.log(Level.WARNING, 
                                "Save file references a news story that doesn''t exist. key:{0}", 
                                key );
                    }
                }
            });
            
            
        } catch (FileNotFoundException ex) {
            LOGGER.config("No previous game state.  New Game.\n");            
            setProperty(PROP_CURRENT_VIGNETTE, defaultVignetteName);  // Game starting room 
            setDefaultNewsStories();
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
    
    // TODO:  Listener is misspelled.
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
        setShowTerminal(false);
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
        setShowTerminal(false);
        setShowChips(!showChips);
    }
    
    public void notifyPlayerStateChanged( String key ) {
        for (GameStateListener l: listenters) {
            l.gameStatePropertyChanged(this, key);                
        }
    }
    
    public void setShowDebug( boolean show ) {
        showDebug = show;
        for (GameStateListener l: listenters) {
            l.gameStateShowDebug(this, showDebug);                
        }
    }
    
    public void toggleDebugShowing() {
        setShowDebug(!showDebug);
    }

    public void setShowTerminal( boolean show ) {
        this.showTerminal = show;
        for ( GameStateListener l: listenters ) {
            l.gameStateShowTerminal(this, showTerminal);
        }
    }
    
    public void setCurrentTerminal(BBSTerminal term) {
        LOGGER.log(Level.INFO, "Terminal changed from:{0} to: {1}", 
                new Object[]{
                    currentTerminal.getClass().getSimpleName(), 
                    term.getClass().getSimpleName()
                }
        );
        if ( term.getClass() == currentTerminal.getClass() ) {
            // Exit on main screen is link to itself.
            // So set not showing.
            setShowTerminal(false);
        }
        this.currentTerminal = term;
        for (GameStateListener l: listenters) {
            l.gameStateTerminalChanged(this, currentTerminal);                
        }
    }
    
    public BBSTerminal getTerminal() {
        return currentTerminal;
    }

    public void toggleTerminalShowing() {
        setShowInventory(false);
        setShowChips(false);
        setShowTerminal(!showTerminal);
    }
    
    public ArrayList<NewsStory> getNews() {
        return news;
    }
    
    private void initNews() {
        LOGGER.fine("Initialize News Stories");
        // Load the localization bundle for the News
        String bPath = "content.messages.bbs.news";
        try {
            this.bundle = ResourceBundle.getBundle(bPath);
            List<String> keys =  Collections.list( bundle.getKeys() );
            Collections.sort(keys);
            
            for ( String key : keys ) {
                if ( key.startsWith(NewsStory.PROP_PREFIX) 
                        && key.endsWith(".date")) {
                    String prefix = NewsStory.PROP_PREFIX + key.split("\\.")[1];
                    NewsStory ns = new NewsStory(bundle, prefix);
                    news.add(ns);
                }
            }
            //Iterator<String> keys = bundle.getKeys().asIterator();
//            while ( keys.hasNext() ) {
//                String key = keys.next();
//                if ( key.startsWith(NewsStory.PROP_PREFIX) 
//                        && key.endsWith(".date")) {
//                    String prefix = NewsStory.PROP_PREFIX + key.split("\\.")[1];
//                    NewsStory ns = new NewsStory(bundle, prefix);
//                    news.add(ns);
//                }
//            }
        } catch (MissingResourceException ex) {
            LOGGER.log(Level.WARNING,
                    "Unable to locate vignette resource bundle at: {0}", bPath);

            // TODO:  maybe load a default bundle here.
            throw ex;
        }

    }
    
    /**
     * Set the default visible news stories. For first-time players.
     */
    private void setDefaultNewsStories() {
        for ( NewsStory ns : news ) {
            if ( DEFAULT_NEWS.contains(ns.getUid() ) ) {
                ns.setShow(true);
            }
        }
    }
    
    public NewsStory getNewsStory(String uid) {
        for ( NewsStory ns: news ) {
            if ( uid.equals(ns.getUid()) ) {
                return ns;
            }
        }
        return null;
    }
    
    private void saveNewsSettings() {
        for ( NewsStory ns: news ) {
            StringBuilder sb = new StringBuilder();
            if ( ns.canShow() ) {
                sb.append(NewsStory.SHOW_FLAG);
            }
            if ( ns.isRead() ) {
                if ( sb.length() == 0 ) {
                    sb.append(",");
                }
                sb.append(NewsStory.READ_FLAG);
            }
            
            if ( sb.length() == 0 ) {
                setProperty(NewsStory.PROP_PREFIX+ns.getUid(), sb.toString());
            }
        }
    }
    
    /**
     * @return the newsIndex
     */
    public int getNewsIndex() {
        return newsIndex;
    }

    /**
     * @param newsIndex the newsIndex to set
     */
    public void setNewsIndex(int newsIndex) {
        this.newsIndex = newsIndex;
    }
    
}
