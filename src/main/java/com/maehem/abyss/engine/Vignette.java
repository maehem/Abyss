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

import com.maehem.abyss.engine.view.ViewPane;
import static com.maehem.abyss.Engine.LOGGER;
import com.maehem.abyss.engine.babble.DialogPane;
import com.maehem.abyss.engine.bbs.BBSTerminal;
import com.maehem.abyss.engine.gui.GiveCreditsPane;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author Mark J Koch [@maehem on GitHub]
 */
public abstract class Vignette extends ViewPane {

    public static final String PROP_PREFIX = "vignette.";
    public enum RoomState { VISITED, LOCKED }
    
    public static final double DEFAULT_SCALE = 4.2;  // Scale character up when they reach the fourth wall.
    public static final double DEFAULT_HORIZON = 0.24;  // Place horizon this far down from screen top.  0.0 - 1.0
    //public static final int NATIVE_WIDTH = 1280; // Native width of PNG background.
    //public static final int NATIVE_HEIGHT = 720; // Native height of PNG background.

    private final GameState gameState;
    private final Group walkAreaCoords = new Group();
    private final Group skylineGroup = new Group(); // Scene sky
    private final Group bgGroup = new Group(); // Scene backdrop
    private final Group mainGroup = new Group(walkAreaCoords); // Character/player. All collisions.
    private final Group fgGroup = new Group(); // Foreground decorations.
    private final GiveCreditsPane giveCredits;

    private final ArrayList<VignetteTrigger> doors = new ArrayList<>();
    private final ArrayList<MatrixTrigger>   jacks = new ArrayList<>();
    private final ArrayList<TerminalTrigger> terminals = new ArrayList<>();
    private final ArrayList<Patch> patchList = new ArrayList<>();
    private final ArrayList<Character> characterList = new ArrayList<>();
    
    private double playerScale = DEFAULT_SCALE;
    private double horizon = DEFAULT_HORIZON;
    private final Player player;
    protected boolean showCollision = false;
    protected boolean showWalk = false;
    private boolean showHearing = false;
    private Polygon walkArea;
    private boolean playerTalkToNPC = false;
    private boolean playerJackToMatrix = false;
    private boolean playerUseTerminal = false;
    private final String assetFolderName;
    Group layerStack = new Group();

    private String name = "<unnamed>";
//    private final double width;
//    private final double height;
    //private DialogScreen dialogOverlay;
    private double debugOpacity = 0.7;
    public ResourceBundle bundle;
    private DialogPane dialogPane;
    private int moveDistanceRL = 20;

    public Vignette(GameState gs, String assetFolderName, VignetteTrigger prevPort, Player player, double[] walkBoundary) throws MissingResourceException {
        this.gameState = gs;
        this.assetFolderName = assetFolderName;
        this.player = player;

        this.setWidth(WIDTH);
        this.setHeight(HEIGHT);
        this.setClip(new Rectangle(WIDTH, HEIGHT));
        
        LOGGER.log(Level.CONFIG, "class name: {0}", super.getClass().getSimpleName());

        getChildren().add(layerStack);
        addNode(skylineGroup);
        addNode(bgGroup);
        addNode(mainGroup);
        addNode(fgGroup);
        
        setWalkArea(walkBoundary);

        getMainGroup().getChildren().addAll(getPlayer());

        // Load the localization bundle for this Vignette
        String bPath = "content.messages." + this.getClass().getSimpleName();
        try {
            //this.bundle = ResourceBundle.getBundle(bPath);
            // Bundle is in content JAR, so look there.
            this.bundle = gameState.getContentLoader().getBundle(bPath);
            initBackdrop();
            setName(bundle.getString("title"));
            LOGGER.config("call Vignette.init()");
            init();
        } catch (MissingResourceException ex) {
            LOGGER.log(Level.SEVERE,
                    "Unable to locate vignette resource bundle at: {0}", bPath);

            // TODO:  maybe load a default bundle here.
            throw ex;
        }

        // This will overwrite any player position defaults set in the implementing Vignette
        // Example: Player left through right door and you want them to appear in the next
        // vignette's left side.
        if (prevPort != null && prevPort.getPlayerX() >= 0 && prevPort.getPlayerY() >= 0) {
            LOGGER.log(Level.FINE, "set player xy override: {0},{1}", new Object[]{prevPort.getPlayerX(), prevPort.getPlayerY()});
            setPlayerPosition(new Point2D(prevPort.getPlayerX(), prevPort.getPlayerY()));
            player.setDirection(prevPort.getPlayerDir());
        } else {
            Point2D pp = getDefaultPlayerPosition();
            LOGGER.log(Level.FINER, 
                    "Set default player position to: {0},{1}", 
                    new Object[]{pp.getX(), pp.getY()}
            );
            setPlayerPosition(pp);
            player.setDirection(PoseSheet.Direction.TOWARD);
        }

        LOGGER.finest("do debug colllision bounds");
        debugCollisionBounds(showCollision);
        debugHearingBounds(showHearing);

        setOnMouseClicked((event) -> {
            if (dialogPane == null || !dialogPane.isVisible() ) { // As long as dialog is not showing.
                player.walkToward(event.getX(), event.getY(), walkArea);
            }
            event.consume();
        });

        giveCredits = new GiveCreditsPane(gameState);
        
        getChildren().add(giveCredits);
        giveCredits.setVisible(false);
        
        String prefix = PROP_PREFIX + getPropName();
        getGameState().setProperty(prefix, RoomState.VISITED.name());        
        
        LOGGER.log(Level.CONFIG, "[Vignette] \"{0}\" loaded.", getName());
    }

    protected abstract void init();

    protected abstract void loop();

    //public abstract String getPropName();
    public String getPropName() {
        return getClass().getSimpleName();
    }
    
    public abstract Point2D getDefaultPlayerPosition();

    public final GameState getGameState() {
        return gameState;
    }
    
    /**
     * @return the player
     */
    public final Player getPlayer() {
        return player;
    }

    /**
     * If showing, Called by loop every tick.
     * 
     * @param input list of keyboard events
     * @return next room to load or @null to remain in current room
     */
    protected final VignetteTrigger processEvents(ArrayList<String> input) {

        if (dialogPane == null || !dialogPane.isVisible() ) {
            if (!input.isEmpty()) {
                LOGGER.log(Level.FINE, "vignette process input event:  {0}", input.toString());

                processUDLR(input);
                processDebugKeys(input);
                processHotKeys(input);
                input.clear();
            }

            // Make patch visible if player is potentially behind it.
            getPatchList().forEach((patch) -> {
                getFgGroup().setVisible(player.getLayoutY() < patch.getThreshold());
            });

            for (VignetteTrigger door : doors) {
                boolean playerTriggered = player.colidesWith(door.getTriggerShape());
                door.updateTriggerState(playerTriggered);
                
                if (playerTriggered) {
                    if (!door.isLocked()) {
                        // Return the players pose skin back to default.
                        //getPlayer().useDefaultSkin();
                        LOGGER.fine("player triggered door.");
                        return door;
                    } else {
                        LOGGER.log(Level.FINE, "Door to {0} is locked.", door.getDestination());
                    }
                }
            }

            for (MatrixTrigger trig : jacks) {
                boolean playerCanMatrix = player.colidesWith(trig.getTriggerShape());
                trig.updateTriggerState(playerCanMatrix);
                trig.showIcon(playerCanMatrix);
                if (playerCanMatrix) {
                    // Show a Matrix clickable at this location.
                    // If user clicks it:
                    // Tell gameState to enter matrix
                    if ( playerJackToMatrix || trig.isJacking() ) {
                        LOGGER.fine("player triggered matrix.");
                        playerJackToMatrix = false;
                        trig.setJacking(false);
                        gameState.setCurrentMatrixSite(
                                gameState.getSite(trig.getDestination())
                        );
                        // We're in.  "We know Morphius, STFU".
                        gameState.setShowing(GameState.Display.MATRIX);
                    }
                }
            }

            for (TerminalTrigger trig : terminals) {
                boolean playerCanTerminal = player.colidesWith(trig.getTriggerShape());
                trig.updateTriggerState(playerCanTerminal);
                trig.showIcon(playerCanTerminal);
                if (playerCanTerminal) {
                    // Show a Matrix clickable at this location.
                    // If user clicks it:
                    // Tell gameState to enter matrix
                    if ( playerUseTerminal || trig.isUsingTerminal()) {
                        LOGGER.config("player triggered terminal.");
                        playerUseTerminal = false;
                        trig.setUsingTerminal(false);
                        Class<? extends BBSTerminal> destination = trig.getDestination();
                        if ( destination == null ) {
                            gameState.setCurrentTerminal(gameState.getPublicTerminal());
                        } else {
                            // Load the class
                            gameState.setCurrentTerminal(BBSTerminal.createTerminal(destination, gameState));
                        }
                        gameState.setShowing(GameState.Display.TERMINAL);
                    }
                }
            }

            getCharacterList().forEach((Character npc) -> {
                // Show dialog if player can hear.
                if (npc.canHear(player.getHearingBoundary())) {
                    npc.showTalkIcon(true);
                    //gameState.setProperty(GameState.EPHEMERAL_KEY + ".npc", npc.getName());
                    if (playerTalkToNPC) {
                        npc.setTalking(true);
                        playerTalkToNPC = false;  // Consume event.
                    }
                } else {
                    npc.showTalkIcon(false);
                    //gameState.remove(GameState.EPHEMERAL_KEY + ".npc");
                }
//                if (npc.isTalking() && !npc.getDialog().isActionDone()) {
//                    //mode = new DialogScreen(player, npc, width, height);
//                    dialogOverlay = npc.getDialog();
//                    dialogOverlay2 = npc.getDialogPane();
//                    //addNode(dialogOverlay);
//                    addNode(dialogOverlay2);
//                    
//                    dialogOverlay.toFront();
//                    dialogOverlay2.toFront();
//                    
//                    LOGGER.warning("Show Dialog Mode.");
//                }
                if (npc.isTalking() && !npc.getDialogPane().isActionDone()) {
                    if ( dialogPane == null ) {
                        dialogPane = npc.getDialogPane();
                        addNode(dialogPane);
                    }
                    dialogPane.setVisible(true);
                    dialogPane.toFront();
                    
                    LOGGER.warning("Show Dialog Mode.");
                } else if (npc.isTalking() && npc.getDialogPane().isActionDone()) {
                    LOGGER.log(Level.WARNING, "NPC is talking but the action is marked as done.");
                }
            });
        } else {
            // Alternate mode is overlayed like a DialogScreen.  Handle that.
//            if (dialogOverlay.isActionDone()) {
//                removeNode(dialogOverlay);
//
//                // See if the dialog invoked a scene exit event.
//                VignetteTrigger exit = dialogOverlay.getExit();
//                dialogOverlay = null;
//
//                // If mode/dialog set the exit door then return that.
//                if (exit != null) {
//                    return exit;
//                }
//            }
            if (dialogPane.isActionDone()) {
                removeNode(dialogPane);

                // See if the dialog invoked a scene exit event.
                VignetteTrigger exit = dialogPane.getExit();
                dialogPane = null;

                // If mode/dialog set the exit door then return that.
                if (exit != null) {
                    return exit;
                }
            }
        }

        getPlayer().setScale(
                getPlayerScale()
                * (getPlayer().getLayoutY() / getHeight() - getHorizon())
        );
        //LOGGER.log(Level.FINER, "Player scale set to: " + getPlayer().getScaleY() );
        loop(); // Run the user defined @loop() code.

        // TODO:  maybe return loop() and allow the child to cause exit of scene?
        return null;
    }

    private void processUDLR(ArrayList<String> input) {
        if (input.contains("LEFT")) {
            input.remove("LEFT");
            player.moveLeft(moveDistanceRL, walkArea);
        }

        if (input.contains("RIGHT")) {
            input.remove("RIGHT");
            player.moveRight(moveDistanceRL, walkArea);
        }

        if (input.contains("UP")) {
            input.remove("UP");
            player.moveUp(6, walkArea);
        }

        if (input.contains("DOWN")) {
            input.remove("DOWN");
            player.moveDown(6, walkArea);
        }
    }

    private void processDebugKeys(ArrayList<String> input) {
        if (input.contains("C")) {
            input.remove("C");
            showCollision = !showCollision;
            debugCollisionBounds(showCollision);
        }
        if (input.contains("H")) {
            input.remove("H");
            showHearing = !showHearing;
            debugHearingBounds(showHearing);
        }
//        if (input.contains("W")) {
//            input.remove("W");
//            showWalk = !showWalk;
//            showWalkPanel(showWalk);
//        }
    }

    private void processHotKeys(ArrayList<String> input) {
//        if (input.contains("S")) {
//            input.remove("S");
//            LOGGER.config("User saved game.");
//        }
        if (input.contains("T")) {
            input.remove("T");
            playerTalkToNPC = true;
            LOGGER.config("Player has requested talking to NPC.");
        }
        if (input.contains("M")) {
            input.remove("M");
            playerJackToMatrix = true;
            LOGGER.config("User jacked into matrix.");
        }
        if (input.contains("B")) {
            input.remove("B");
            playerUseTerminal = true;
            LOGGER.config("User used terminal.");
        }
    }

    protected final void debugCollisionBounds(boolean show) {
        String colStatus;
        if (show) {
            colStatus = "Showing";
        } else {
            colStatus = "Hidden";
        }
        LOGGER.log(Level.FINER, "Collision Bounds: {0}", colStatus);

        getWalkArea().setOpacity(show ? debugOpacity : 0.0);
        walkAreaCoords.setOpacity(show ? debugOpacity : 0.0);
        getFgGroup().setOpacity(show ? 0.5 : 1.0); // Wings translucent when debuging.
        getDoors().forEach((door) -> {
            door.setShowDebug(show);
        });
        jacks.forEach((jack) -> {
            jack.setShowDebug(show);
        });
        terminals.forEach((terminal) -> {
            terminal.setShowDebug(show);
        });

        // Display box around patches when showing collision bounds.
        getPatchList().forEach((patch) -> {
            patch.getBox().setOpacity(show ? debugOpacity : 0.0);
        });

        getPlayer().showCollisionBounds(show);
        getCharacterList().forEach((Character npc) -> {
            ((Character) npc).showCollisionBounds(show);
        });
        
    }

    protected final void debugHearingBounds(boolean show) {
        String status;
        if (show) {
            status = "Showing";
        } else {
            status = "Hidden";
        }
        LOGGER.log(Level.FINER, "Hearing Bounds: {0}", status);

        getPlayer().showHearingBounds(show);
        getCharacterList().forEach((Character npc) -> {
            ((Character) npc).showHearingBounds(show);
        });
        
    }

    /**
     *
     * @return the bgGroup
     */
    public Group getBgGroup() {
        return bgGroup;
    }
    
    /**
     *
     * @return the bgGroup
     */
    public Group getSkylineGroup() {
        return skylineGroup;
    }

    /**
     * @return the mainGroup
     */
    public final Group getMainGroup() {
        return mainGroup;
    }

    /**
     * @return the fgGroup
     */
    public Group getFgGroup() {
        return fgGroup;
    }

    /**
     * @return the doors
     */
    public ArrayList<VignetteTrigger> getDoors() {
        return doors;
    }

    /**
     * @return the doors
     */
    public ArrayList<MatrixTrigger> getJacks() {
        return jacks;
    }

    /**
     * @return the terminals
     */
    public ArrayList<TerminalTrigger> getTerminals() {
        return terminals;
    }

    /**
     * Walk area as an array of XY pairs with a value of 0.0-1.0
     * relative to screen width and height.
     * 
     * @param xyArray list of X/Y pair doubles.
     */
    public final void setWalkArea( double[] xyArray ) {
        
        //setWalkAreaOld(WALK_AREA); // set custom walk area
        double waPx[] = new double[xyArray.length];
        
        for ( int i=0; i< xyArray.length; i+=2) {
            waPx[i] = xyArray[i]*getWidth();
            waPx[i+1] = xyArray[i+1]*getHeight();
        }
        
        setWalkArea(new Polygon(waPx));
    }
    
    /**
     * @param walkArea the walkArea to set
     */
    public void setWalkArea(Polygon walkArea) {
        mainGroup.getChildren().remove(this.walkArea);
        walkAreaCoords.getChildren().clear();
        this.walkArea = walkArea;
        mainGroup.getChildren().add(0, getWalkArea());
        
        ObservableList<Double> points = walkArea.getPoints();
        for (int i = 0; i < points.size(); i += 2) {
            double vX = points.get(i);
            double vY = points.get(i+1);
            double pX = points.get(i);
            double pY = points.get(i+1);
            //String xS = String.format(String.valueOf(pX), "%4.0f" );
            //String yS = String.format(String.valueOf(pY), "%4.0f" );
            String xS = String.valueOf((int)pX);
            String yS = String.valueOf((int)pY);
            //String xP = String.format(String.valueOf(pX/getWidth()), "%4.3f" );
            //String yP = String.format(String.valueOf(pY/getHeight()), "%4.3f" );
            String xP = String.format("%4.3f", pX/getWidth()  );
            String yP = String.format("%4.3f", pY/getHeight() );
            
            Text t = new Text(
                    vX, vY,
                    xS + "," + yS + " (" +  xP + "," + yP + ")"
            );
            t.setFill(Color.LIGHTGREEN);
            t.setFont(Font.font(14));
            walkAreaCoords.getChildren().add(t);
        }

        Color collisionStroke = Color.GREEN;
        getWalkArea().setStroke(new Color(
                collisionStroke.getRed(), 
                collisionStroke.getGreen(), 
                collisionStroke.getBlue(), 
                0.6
        ));
        getWalkArea().setStrokeWidth(3.0);
        getWalkArea().setFill(Color.TRANSPARENT);
    }

    /**
     * @return the walkArea
     */
    public Polygon getWalkArea() {
        return walkArea;
    }

    public ArrayList<Patch> getPatchList() {
        return patchList;
    }

    /**
     * @return the name
     */
    public final String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public final void setName(String name) {
        LOGGER.log(Level.CONFIG, "set name to: {0}", name);
        this.name = name;
    }

//    /**
//     * @return the width
//     */
//    public double getWidth() {
//        return width;
//    }
//
//    /**
//     * @return the height
//     */
//    public double getHeight() {
//        return height;
//    }

    /**
     * @return the scale
     */
    public double getPlayerScale() {
        return playerScale;
    }

    /**
     * @param playerScale the scale to set
     */
    public void setPlayerScale(double playerScale) {
        this.playerScale = playerScale;
    }

    /**
     * 
     * @param pos a 2D point X and Y range of 0.0 to 1.0
     */
    public final void setPlayerPosition( Point2D pos ) {
        getPlayer().setLayoutX(getWidth() * pos.getX());
        getPlayer().setLayoutY(getHeight() * pos.getY());
        LOGGER.log(Level.INFO, "Set player position to: {0},{1}", 
                new Object[]{getPlayer().getLayoutX(), getPlayer().getLayoutY()});
    }
    
    /**
     * @return the horizon
     */
    public double getHorizon() {
        return horizon;
    }

    /**
     * @param horizon the horizon to set
     */
    public void setHorizon(double horizon) {
        this.horizon = horizon;
    }

    /**
     * @return the characterList
     */
    public ArrayList<Character> getCharacterList() {
        return characterList;
    }

//    public String getNarrationText() {
//        return "This is a test.";
//    }

    private void addNode(Node node) {
        layerStack.getChildren().add(node);
    }

    private void removeNode(Node node) {
        layerStack.getChildren().remove(node);
    }

    private void initBackdrop() {
        // Load images for BACKGROUND_IMAGE_FILENAME.
        String backgroundName = assetFolderName + "backdrop.png";
        ResourceLoader contentLoader = gameState.getContentLoader();
        //InputStream is = getClass().getResourceAsStream(backgroundName);
        InputStream is = contentLoader.getStream(backgroundName);
        if (is == null) {
            LOGGER.log(Level.SEVERE, "Cannot find image for: {0}", backgroundName);
            return;
        }
        final ImageView bgv = new ImageView();
        bgv.setImage(new Image(is));
        getBgGroup().getChildren().add(bgv);
    }

    public void saveState(Properties p) {
        player.saveState(p);
        // save local settings
        String prefix = PROP_PREFIX + getPropName();
//        p.setProperty(prefix, "visited");
        // Gather any custom value from subclass and store those too.
        Properties childProperties = saveProperties();
        childProperties.forEach(
                (k, v) -> {
                    p.setProperty(prefix + "." + k, (String) v);
                }
        );
    }

    public void loadState(Properties p) {
        // load vignette parent properties.
//        if (!p.containsKey(PROP_PREFIX + getPropName())) {
//            // Pop the narration pane if this vignette has never been saved.
//            // which means the player has not yet been here.
//            narrationPane.pop();
//        }

        // Check for doors that are marked as locked.
        p.entrySet().forEach((t) -> {
            String key = (String) t.getKey();
            String prefix = PROP_PREFIX + getPropName() + ".door";
            if ( key.startsWith(prefix)) {
                String val = (String)t.getValue();
                //LOGGER.log(Level.WARNING, "Found key [{0}] = {1}", new Object[]{key, val});
                String doorName = key.substring(prefix.length() + 1);
                //LOGGER.log(Level.INFO, "    ====> Door Name: [{0}]", doorName);
                doors.forEach((d) -> {
                    if ( d.getDestination().equals(doorName) ) {
                        d.setLocked(val.equals(Vignette.RoomState.LOCKED.name()));
                    }
                });
            }
        });
        
        // load child properties
        loadProperties(p); // Overridden by sub-class.
    }

    /**
     * Store custom subclass properties.
     *
     * Suggested implementation in subclass, as follows:<br><br>
     *
     * <pre><code>
     *   Properties p = new Properties();
     *   p.setProperty("myProperty", myProperty.toString());
     *
     *   return p;
     * </code></pre>
     *
     * @return custom properties to store
     */
    public abstract Properties saveProperties();

    /**
     * Load in any stored properties for subclass objects.
     *
     * @param p
     */
    public abstract void loadProperties(Properties p);

//    /**
//     * @return the narrationPane
//     */
//    public final NarrationPane getNarrationPane() {
//        return narrationPane;
//    }

    protected void addPort(VignetteTrigger port) {
        //port.setScale(getWidth(), getHeight());
        getDoors().add(port);
        getMainGroup().getChildren().add(port);
    }

    protected void addJack(MatrixTrigger jack) {
        //jack.setScale(getWidth(), getHeight());
        getJacks().add(jack);
        getMainGroup().getChildren().add(jack);
    }

    protected void addTerminal(TerminalTrigger terminal) {
        //terminal.setScale(getWidth(), getHeight());
        getTerminals().add(terminal);
        getMainGroup().getChildren().add(terminal);
    }

    protected void addPatch(Patch patch) {
        getPatchList().add(patch);
        getFgGroup().getChildren().add(patch);
        getFgGroup().getChildren().add(patch.getBox());
    }
    
    public String getNarration() {
        String longerText;
        try {
            longerText = " " + bundle.getString("narrationL");
        } catch ( MissingResourceException ex ) {
            longerText = "";
        }
        return bundle.getString("narration") + longerText;
    }

    public void setGiveMoneyShowing(int amount, String title, EventHandler handler) {
        // Show pane for transering money to npc.
        giveCredits.show(amount, gameState.getPlayer().getMoney(), title, handler);
    }
}
