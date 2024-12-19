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
package com.maehem.abyss.engine;

import com.maehem.abyss.Engine;
import static com.maehem.abyss.Engine.LOGGER;
import com.maehem.abyss.engine.audio.music.MusicTrack;
import com.maehem.abyss.engine.bbs.BBSTerminal;
import com.maehem.abyss.engine.bbs.PublicTerminalSystem;
import com.maehem.abyss.engine.matrix.MatrixSite;
import com.maehem.abyss.engine.matrix.MatrixSiteNeighbor;
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
public final class GameState extends Properties {

    private String shortName;
    private String longName;
    private String version;
    private PublicTerminalSystem publicTerminal;
    private MusicTrack musicTrack;

    public enum Display {
        SPLASH, INVENTORY, CHIPS, TERMINAL, VIGNETTE, MATRIX
    }

    // KEYS
    public static final String PROP_CURRENT_VIGNETTE = "game.vignette";
    public static final String PROP_CURRENT_DATE = "game.date";
    public static final String EPHEMERAL_KEY = "ephemeral";  // Temp state that is not saved in a file.

    private static final String START_DATE = "2057/02/01 05:01";

//    // TODO: Bake into news.proerties
//    private static final List<String> DEFAULT_NEWS = List.of(
//            "100", "101", "102", "103", "104",
//            "105", "106", "107", "108", "109",
//            "110", "111"
//    );
    private static final int N_ZONES = 1; // TODO: Set by sub-game.
    public static final int MAP_SIZE = 64; // Row or Cols

    private Player player;
    private final ArrayList<NewsStory> news = new ArrayList<>();
    private final ArrayList<BulletinMessage> messages = new ArrayList<>();
    private final ArrayList<GameStateListener> listeners = new ArrayList<>();
    private final Properties ephemerals = new Properties();

    private SitesList sites;
    private final EdgeMap matrixEdges = new EdgeMap(MAP_SIZE, MAP_SIZE);

    private Vignette currentVignette;
    private BBSTerminal currentTerminal;
    //private int currentMatrixAddress = 0x00305;
    private MatrixSite currentMatrixSite;
    private int newsIndex = 0;
    private int messageIndex = 0;

    //private ResourceBundle bundle;
    private Display showing = Display.MATRIX;
    private Display termPop = Display.VIGNETTE; // What to display if we leave terminal.

    private boolean showDebug = true;

    private File gameSaveFile = null;
//    private File gameSaveFile = new File(
//            System.getProperty("user.home")
//            + File.separator + "Documents"
//            + File.separator + "Abyss"
//            + File.separator + "save-0.properties"
//    );

    // Debug toggles
    public boolean showWalkPerimeter = false;

    private Object contentPack;
    private ResourceLoader contentLoader;

    public GameState() {
    }

    public String getShortGameName() {
        return shortName;
    }

    public String getLongGameName() {
        return longName;
    }

    public void setGameName(String shortName, String longName) {
        this.shortName = shortName;
        this.longName = longName;

        gameSaveFile = new File(
                System.getProperty("user.home")
                + File.separator + "Documents"
                + File.separator + shortName
                + File.separator + "save-0.properties"
        );
    }

    public String getGameVersion() {
        return version;
    }

    public void setGameVersion(String versionString) {
        this.version = versionString;
    }

    public void setSites(SitesList sl) {
        // TODO: Check for null and don't allow changing after set once.
        this.sites = sl;
    }

    public void setContentLoader(ResourceLoader rl) {
        contentLoader = rl;
    }

    public ResourceLoader getContentLoader() {
        return contentLoader;
    }

    public void init() {
        if (gameSaveFile == null) {
            LOGGER.log(Level.SEVERE, "Game name was not set by content pack! There will be issues...");
        }
        setProperty(PROP_CURRENT_DATE, START_DATE);
        this.player = new Player(this);
        this.publicTerminal = new PublicTerminalSystem(this);
        this.currentTerminal = publicTerminal;
        //this.sites = new DefaultSitesList(this);

        currentMatrixSite = getSite(0x00305);

        //initNews();
        //initMessages();
    }

    @Override
    public String getProperty(String key) {
        if (key.startsWith(Player.PLAYER_KEY)) {
            return player.getProperty(key);
        }
        if (key.startsWith(EPHEMERAL_KEY)) {
            return ephemerals.getProperty(key);
        } else {
            return super.getProperty(key);
        }
    }

    public void setContentPack(Object o) {
        this.contentPack = o;
    }

    public Object getContentPack() {
        return contentPack;
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
            LOGGER.log(Level.INFO, "Game State saved at: {0}", gameSaveFile.getAbsolutePath());
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

            setProperty(PROP_CURRENT_DATE,
                    ldProps.getProperty(PROP_CURRENT_DATE) // Game starting room
            );
            setProperty(PROP_CURRENT_VIGNETTE,
                    ldProps.getProperty(PROP_CURRENT_VIGNETTE) // Game starting room
            );

            // Player tracks it's own properties.
            player.loadState(ldProps);

            ldProps.keys().asIterator().forEachRemaining((t) -> {
                String key = (String) t;
                // Intake Vignette flag
                if (key.startsWith(Vignette.PROP_PREFIX)) {
                    LOGGER.log(Level.FINE, "Load Vignette prop: {0}", key);
                    setProperty(key, ldProps.getProperty(key));
                }
                if (key.startsWith(NewsStory.PROP_PREFIX)) {
                    LOGGER.log(Level.FINE, "Load NewsStory prop: {0}", key);
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
        Object prevVal;
        if (key.startsWith(EPHEMERAL_KEY)) {
            prevVal = ephemerals.setProperty(key, value);
        } else {
            prevVal = super.setProperty(key, value);
        }

        for (GameStateListener l : listeners) {
            l.gameStatePropertyChanged(this, key);
        }
        return prevVal;
    }

    @Override
    public synchronized Object remove(Object key) {
        Object prevVal;
        String kkey = (String) key;
        if (kkey.startsWith(EPHEMERAL_KEY)) {
            prevVal = ephemerals.remove(key);
        } else {
            prevVal = super.remove(key);
        }

        for (GameStateListener l : listeners) {
            l.gameStatePropertyChanged(this, kkey);
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

    public MatrixSite getCurrentMatrixSite() {
        return currentMatrixSite;
    }

    public void setCurrentMatrixSite(MatrixSite site) {
        this.currentMatrixSite = site;
        for (GameStateListener l : listeners) {
            l.gameStateMatrixSiteChanged(this, site.getIntAddress());
        }

    }

    public void setCurrentMatrixSite(MatrixSiteNeighbor n) {
        setCurrentMatrixSite(getSite(getCurrentMatrixSite().getNeighbor(n)));
    }

    public void setShowing(Display d) {
        LOGGER.log(Level.FINER, "GameState set showing from: {0} to: {1}", new Object[]{showing.toString(), d.toString()});
        if (d == Display.TERMINAL) {
            // If the user goes into terminal, remember where to come back to.
            // Should only ever be MATRIX or VIGNETTE
            termPop = showing;
        }
        showing = d;

        for (GameStateListener l : listeners) {
            l.gameStateDisplayChanged(this, d);
        }

    }

    /**
     * If it is showing, hide it. If hidden, show it.
     *
     * @param d
     */
    public void toggleShowing(Display d) {
        if (showing == d) {
            // Hide it.
            LOGGER.log(Level.FINER, "GameState: toggleShowing():  hide:{0}", showing);
            switch (d) {
                case CHIPS:
                case INVENTORY:
                case MATRIX:
                    setShowing(Display.VIGNETTE);
                    break;
                case TERMINAL:
                case SPLASH:
                    setShowing(termPop);
                    break;
            }
        } else {
            // Show it
            LOGGER.log(Level.FINER, "GameState: toggleShowing():  show:" + d);
            if (d == Display.TERMINAL || d == Display.SPLASH) {
                LOGGER.log(Level.FINER, "GameState: set termpop from: " + termPop + "to: " + showing);
                termPop = showing;
            }
            setShowing(d);
        }
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

//    public int getCurrentMatrixAddress() {
//        return currentMatrixAddress;
//    }
//
//    public void setCurrentMatrixAddress( int addr ) {
//        this.currentMatrixAddress = addr;
//    }
//
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

    public void setCurrentTerminal(BBSTerminal term) {
        LOGGER.log(Level.CONFIG, "Terminal changed from:{0} to: {1}",
                new Object[]{
                    currentTerminal.getClass().getSimpleName(),
                    term.getClass().getSimpleName()
                }
        );
        if (term.getClass() == currentTerminal.getClass()) {
            // Exit on main screen is link to itself.
            // So set not showing.
            setShowing(termPop);
            //setShowTerminal(false);
        }
        this.currentTerminal = term;
        for (GameStateListener l : listeners) {
            l.gameStateTerminalChanged(this, currentTerminal);
        }
    }

    public BBSTerminal getTerminal() {
        return currentTerminal;
    }

    public ArrayList<NewsStory> getNews() {
        return news;
    }

    public void initNews(ResourceBundle bundle) {
        LOGGER.fine("Initialize News Stories");
        // Load the localization bundle for the News
        //String bPath = "content.messages.bbs.news";
        try {
            //ResourceBundle bundle = ResourceBundle.getBundle(bPath);
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
//            LOGGER.log(Level.WARNING,
//                    "Unable to locate news resource bundle at: {0}",
//                    bPath
//            );

            // TODO:  maybe load a default bundle here.
            throw ex;
        }

    }

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

    public void initMessages(ResourceBundle bundle) {
        LOGGER.fine("Initialize Bulletin Messages");
        // Load the localization bundle for the News

        //String bPath = "content.messages.bbs.bulletin";
        try {
            //this.bundle = ResourceBundle.getBundle(bPath);
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
//            LOGGER.log(Level.WARNING,
//                    "Unable to locate news resource bundle at: {0}",
//                    bPath
//            );

            // TODO:  maybe load a default bundle here.
            throw ex;
        }

    }

    public BulletinMessage getBulletinMessage(String uid) {
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

    public void initHelp(ResourceBundle bundle) {
        LOGGER.fine("Initialize Help System");

        getPublicTerminal().getHelp().setBundle(bundle);
    }

    public final MatrixSite addSite(MatrixSite site) {
        if (siteExists(site.getIntAddress())) {
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
    }

    private boolean siteExists(int address) {
        for (MatrixSite s : sites) {
            if (s.getIntAddress() == address) {
                return true;
            }
        }
        return false;
    }

    /**
     * Known sites are set up at start of game. All other sites are blank sites
     * and generated as requested.
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
        if (MatrixSite.decodeCol(address) >= 0
                && MatrixSite.decodeRow(address) >= 0
                && MatrixSite.decodeCol(address) < MAP_SIZE - 1
                && MatrixSite.decodeRow(address) < MAP_SIZE - 1) {
            return addSite(new MatrixSite(this, address));
        }

        LOGGER.log(Level.INFO, "Tried to add a site out of bounds!");
        return null; // Out of bounds
    }

    public EdgeMap getMatrixMap() {
        return matrixEdges;
    }

    public PublicTerminalSystem getPublicTerminal() {
        return publicTerminal;
    }

    public MusicTrack getMusicTrack() {
        return musicTrack;
    }

    public void setMusicTrack(MusicTrack track) {
        musicTrack = track;
    }
}
