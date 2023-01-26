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

import com.maehem.flatlinejack.engine.matrix.MatrixSite;
import static com.maehem.flatlinejack.Engine.LOGGER;

import com.maehem.flatlinejack.Engine;
import com.maehem.flatlinejack.content.matrix.site.DefaultSitesList;
import com.maehem.flatlinejack.content.matrix.sitenode.HeatsinkNode;
import com.maehem.flatlinejack.content.sites.PublicTerminalSystem;
import com.maehem.flatlinejack.engine.gui.bbs.BBSTerminal;
import com.maehem.flatlinejack.engine.matrix.EmptyMatrixNode;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mark J Koch [maehem at GitHub]
 */
public class GameState extends Properties {

    // KEYS
    public static final String PROP_CURRENT_VIGNETTE = "game.vignette";
    public static final String PROP_CURRENT_DATE = "game.date";

    private static final String START_DATE = "2057/02/01 05:01";

//    // TODO: Bake into news.proerties
//    private static final List<String> DEFAULT_NEWS = List.of(
//            "100", "101", "102", "103", "104",
//            "105", "106", "107", "108", "109",
//            "110", "111"
//    );
    private static final int N_ZONES = 5;
//    private static final int N_ROWS = 10;
//    private static final int N_COLS = 10;

    public static final int MAP_SIZE = 64; // Row or Cols

    private final Player player;
    private final ArrayList<NewsStory> news = new ArrayList<>();
    private final ArrayList<BulletinMessage> messages = new ArrayList<>();
    private final ArrayList<GameStateListener> listeners = new ArrayList<>();
    private final ArrayList<MatrixSite> sites;
    //private long[][][] siteEdges =  new long[N_ZONES][N_ROWS][N_COLS]; // Long T R B L ints
    private final EdgeMap matrixEdges = new EdgeMap(MAP_SIZE, MAP_SIZE);

    private Vignette currentVignette;
    private BBSTerminal currentTerminal;
    private int newsIndex = 0;
    private int messageIndex = 0;

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

    // Debug toggles
    public boolean showWalkPerimeter = false;

    public GameState() {
        this.player = new Player(this);
        this.currentTerminal = new PublicTerminalSystem(this);

        setProperty(PROP_CURRENT_DATE, START_DATE);
        initNews();
        initMessages();

        sites = new DefaultSitesList(this);

//        addSite(new MatrixSite(this, 0, 1, 1, HeatsinkNode.class));
//        addSite(new MatrixSite(this, 0, 2, 5, HeatsinkNode.class));
//        addSite(new MatrixSite(this, 0, 3, 8, HeatsinkNode.class));
//        addSite(new MatrixSite(this, 0, 4, 2, HeatsinkNode.class));
//        addSite(new MatrixSite(this, 0, 5, 3, HeatsinkNode.class));
    }

    @Override
    public String getProperty(String key) {
        if (key.startsWith(Player.PLAYER_KEY)) {
            return player.getProperty(key);
        } else {
            return super.getProperty(key);
        }
    }

    public void quickSave() {
        player.saveState(this);

        saveNewsSettings();
        saveBulletinSettings();

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

    public void load(String defaultVignetteName) {
        try {
            FileInputStream in = new FileInputStream(gameSaveFile);
            Properties ldProps = new Properties();
            ldProps.load(in);
            LOGGER.log(Level.CONFIG,
                    "Loaded previous save file: {0}",
                    gameSaveFile.getAbsolutePath());

            setProperty(PROP_CURRENT_VIGNETTE,
                    ldProps.getProperty(PROP_CURRENT_VIGNETTE) // Game starting room
            );
            // Player tracks it's own properties. 
            player.loadState(ldProps);

            ldProps.keys().asIterator().forEachRemaining((t) -> {
                String key = (String) t;
                // Intake Vignette flag
                if (key.startsWith(Vignette.PROP_PREFIX)) {
                    LOGGER.log(Level.INFO, "Load Vignette prop: " + key);
                    setProperty(key, ldProps.getProperty(key));
                }
                if (key.startsWith(NewsStory.PROP_PREFIX)) {
                    LOGGER.log(Level.INFO, "Load NewsStory prop: " + key);
                    String uid = key.split("\\.")[1];
                    NewsStory newsStory = getNewsStory(uid);
                    if (newsStory != null) {
                        String property = ldProps.getProperty(key);
                        if (property.contains("show")) {
                            newsStory.setShow(true);
                        }
                        if (property.contains("read")) {
                            newsStory.setRead(true);
                        }
                    } else {
                        LOGGER.log(Level.WARNING,
                                "Save file references a news story that doesn''t exist. key: {0}",
                                key);
                    }
                }
            });

        } catch (FileNotFoundException ex) {
            LOGGER.config("No previous game state.  New Game.\n");
            setProperty(PROP_CURRENT_VIGNETTE, defaultVignetteName);  // Game starting room 
            //setDefaultNewsStories();
        } catch (IOException ex) {
            Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (GameStateListener l : listeners) {
            for (String s : stringPropertyNames()) {
                l.gameStatePropertyChanged(this, s);
            }
        }

    }

    @Override
    public synchronized Object setProperty(String key, String value) {
        Object prevVal = super.setProperty(key, value);
        for (GameStateListener l : listeners) {
            l.gameStatePropertyChanged(this, key);
        }

        return prevVal;
    }

    // TODO:  Listener is misspelled.
    public void addListenter(GameStateListener l) {
        listeners.add(l);
    }

    public void removeListener(GameStateListener l) {
        listeners.remove(l);
    }

    public Vignette getCurrentVignette() {
        return currentVignette;
    }

    public Player getPlayer() {
        return player;
    }

    public void setCurrentVignette(Vignette v) {
        this.currentVignette = v;
        setProperty(PROP_CURRENT_VIGNETTE, v.getClass().getSimpleName());

        // notify vignette change.
        for (GameStateListener l : listeners) {
            l.gameStateVignetteChanged(this);
        }
    }

    public void setShowInventory(boolean show) {
        this.showInventory = show;
        for (GameStateListener l : listeners) {
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

    public void setShowChips(boolean show) {
        this.showChips = show;
        for (GameStateListener l : listeners) {
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

    public void notifyPlayerStateChanged(String key) {
        for (GameStateListener l : listeners) {
            l.gameStatePropertyChanged(this, key);
        }
    }

    public void setShowDebug(boolean show) {
        showDebug = show;
        for (GameStateListener l : listeners) {
            l.gameStateShowDebug(this, showDebug);
        }
    }

    public void toggleDebugShowing() {
        setShowDebug(!showDebug);
    }

    public void setShowTerminal(boolean show) {
        this.showTerminal = show;
        for (GameStateListener l : listeners) {
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
        if (term.getClass() == currentTerminal.getClass()) {
            // Exit on main screen is link to itself.
            // So set not showing.
            setShowTerminal(false);
        }
        this.currentTerminal = term;
        for (GameStateListener l : listeners) {
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
            List<String> keys = Collections.list(bundle.getKeys());
            Collections.sort(keys);

            for (String key : keys) {
                if (key.startsWith(NewsStory.PROP_PREFIX)
                        && key.endsWith(".date")) {
                    String prefix = NewsStory.PROP_PREFIX + key.split("\\.")[1];
                    NewsStory ns = new NewsStory(bundle, prefix);
                    news.add(ns);
                }
            }
        } catch (MissingResourceException ex) {
            LOGGER.log(Level.WARNING,
                    "Unable to locate news resource bundle at: {0}",
                    bPath
            );

            // TODO:  maybe load a default bundle here.
            throw ex;
        }

    }

//    /**
//     * Set the default visible news stories. For first-time players.
//     */
//    private void setDefaultNewsStories() {
//        for ( NewsStory ns : news ) {
//            if ( DEFAULT_NEWS.contains(ns.getUid() ) ) {
//                ns.setShow(true);
//            }
//        }
//    }
//    
    public NewsStory getNewsStory(String uid) {
        for (NewsStory ns : news) {
            if (uid.equals(ns.getUid())) {
                return ns;
            }
        }
        return null;
    }

    private void saveNewsSettings() {
        for (NewsStory ns : news) {
            StringBuilder sb = new StringBuilder();
            if (ns.canShow()) {
                sb.append(NewsStory.SHOW_FLAG);
            }
            if (ns.isRead()) {
                if (sb.length() != 0) {
                    sb.append(",");
                }
                sb.append(NewsStory.READ_FLAG);
            }

            if (sb.length() != 0) {
                setProperty(NewsStory.PROP_PREFIX + ns.getUid(), sb.toString());
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

    public ArrayList<BulletinMessage> getMessages() {
        return messages;
    }

    private void initMessages() {
        LOGGER.fine("Initialize Bulletin Messages");
        // Load the localization bundle for the News

        String bPath = "content.messages.bbs.bulletin";
        try {
            this.bundle = ResourceBundle.getBundle(bPath);
            List<String> keys = Collections.list(bundle.getKeys());
            Collections.sort(keys);

            for (String key : keys) {
                if (key.startsWith(BulletinMessage.PROP_PREFIX)
                        && key.endsWith(".date")) {
                    String prefix = BulletinMessage.PROP_PREFIX + key.split("\\.")[1];
                    BulletinMessage ns = new BulletinMessage(bundle, prefix);
                    messages.add(ns);
                }
            }
        } catch (MissingResourceException ex) {
            LOGGER.log(Level.WARNING,
                    "Unable to locate news resource bundle at: {0}",
                    bPath
            );

            // TODO:  maybe load a default bundle here.
            throw ex;
        }

    }

    public BulletinMessage getBulletingMessage(String uid) {
        for (BulletinMessage bs : messages) {
            if (uid.equals(bs.getUid())) {
                return bs;
            }
        }
        return null;
    }

    private void saveBulletinSettings() {
        for (BulletinMessage bs : messages) {
            StringBuilder sb = new StringBuilder();
            if (bs.canShow()) {
                sb.append(BulletinMessage.SHOW_FLAG);
            }
            if (bs.isRead()) {
                if (sb.length() != 0) {
                    sb.append(",");
                }
                sb.append(BulletinMessage.READ_FLAG);
            }
            if (bs.hasReplied()) {
                if (sb.length() != 0) {
                    sb.append(",");
                }
                sb.append(BulletinMessage.REPLIED_FLAG);
            }

            if (sb.length() != 0) {
                setProperty(BulletinMessage.PROP_PREFIX + bs.getUid(), sb.toString());
            }
        }
    }

    /**
     * @return the bulletin message index
     */
    public int getBulletinIndex() {
        return messageIndex;
    }

    /**
     * @param idx the bulletin message index to set
     */
    public void setBulletinIndex(int idx) {
        this.messageIndex = idx;
    }

    public final MatrixSite addSite(MatrixSite site) {
        if ( siteExists(site.getIntAddress())) {
            LOGGER.log(Level.SEVERE,
                    "Tried to add matrix site at existing address! {0}",
                    site.getAddress()
            );
            
            // Return the one that's already there.
            return getSite(site.getIntAddress());
        } else {
            // Add the site
            sites.add(site);
            return site;
        }
        
//        if (getSite(site.getIntAddress()) == null) {
//            sites.add(site);
//            LOGGER.log(Level.INFO, "Added site to list as {0} with int: {1}", new Object[]{site.getAddress(), site.getIntAddress()});
//        } else {
//            LOGGER.log(Level.SEVERE,
//                    "Tried to add matrix site at existing address! {0}",
//                    site.getAddress()
//            );
//        }
//        return site;
    }

    private boolean siteExists( int address ) {
        for (MatrixSite s : sites) {
            if (s.getIntAddress() == address) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Known sites are set up at start of game. All other
     * sites are blank sites and generated as requested.
     * 
     * @param address
     * @return 
     */
    public MatrixSite getSite(int address) {
        for (MatrixSite s : sites) {
            if (s.getIntAddress() == address) {
                return s;
            }
        }
        // Create blank site and add it.
        if (       MatrixSite.decodeCol(address) >= 0
                && MatrixSite.decodeRow(address) >= 0
                && MatrixSite.decodeCol(address) < MAP_SIZE-1
                && MatrixSite.decodeRow(address) < MAP_SIZE-1) {
            return addSite(new MatrixSite(this, address));
        }

        LOGGER.log(Level.INFO, "Tried to add a site out of bounds!" );
        return null; // Out of bounds
    }

    public EdgeMap getMatrixMap() {
        return matrixEdges;
    }
}
