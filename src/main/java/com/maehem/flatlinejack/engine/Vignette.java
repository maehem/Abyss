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
import com.maehem.flatlinejack.engine.babble.DialogScreen;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public abstract class Vignette extends Pane {

    public static final String PROP_PREFIX = "vignette.";
    public static final double DEFAULT_SCALE = 4.2;  // Scale character up when they reach the fourth wall.
    public static final double DEFAULT_HORIZON = 0.24;  // Place horizon this far down from screen top.  0.0 - 1.0
    public static final int NATIVE_WIDTH = 1280; // Native width of PNG background.
    public static final int NATIVE_HEIGHT = 720; // Native height of PNG background.

    private double playerScale = DEFAULT_SCALE;
    private double horizon = DEFAULT_HORIZON;
    private final Player player;
    protected boolean showCollision = false;
    protected boolean showWalk = false;
    private boolean showHearing = false;
    private Polygon walkArea;
    private final Group walkAreaCoords = new Group();
    private boolean playerTalkToNPC = false;
    private final String assetFolderName;
    Group layerStack = new Group();
    private final Group bgGroup = new Group();
    private final Group mainGroup = new Group(walkAreaCoords);
    private final Group fgGroup = new Group();

    private final ArrayList<Port> doors = new ArrayList<>();
    private final ArrayList<Patch> patchList = new ArrayList<>();
    private final ArrayList<Character> characterList = new ArrayList<>();

    private String name = "<unnamed>";
    private final double width;
    private final double height;
    private DialogScreen dialogOverlay;
    private double debugOpacity = 0.7;
    public ResourceBundle bundle;

    public Vignette(int w, int h, String assetFolderName, Port prevPort, Player player, double[] walkBoundary) throws MissingResourceException {
        this.width = w;
        this.height = h;
        this.assetFolderName = assetFolderName;
        this.player = player;

        this.setWidth(width);
        this.setHeight(height);
        this.setClip(new Rectangle(width, height));
        
        LOGGER.log(Level.CONFIG, "class name: {0}", super.getClass().getSimpleName());

        getChildren().add(layerStack);
        addNode(bgGroup);
        addNode(mainGroup);
        addNode(fgGroup);
        
        //addNode(narrationPane);

        setWalkArea(walkBoundary);

        getMainGroup().getChildren().addAll(getPlayer());

        // Load the localization bundle for this Vignette
        String bPath = "content.messages." + this.getClass().getSimpleName();
        try {
            this.bundle = ResourceBundle.getBundle(bPath);

            initBackdrop();
            setName(bundle.getString("title"));
            LOGGER.config("call init()");
            init();
        } catch (MissingResourceException ex) {
            LOGGER.log(Level.WARNING,
                    "Unable to locate vignette resource bundle at: {0}", bPath);

            // TODO:  maybe load a default bundle here.
            throw ex;
        }

        // This will overwrite any player position defaults set in the implementing Vignette
        // Example: Player left through right door and you want them to appear in the next
        // vignette's left side.
        if (prevPort != null && prevPort.getPlayerX() >= 0 && prevPort.getPlayerY() >= 0) {
            LOGGER.log(Level.INFO, "set player xy override: {0},{1}", new Object[]{prevPort.getPlayerX(), prevPort.getPlayerY()});
            setPlayerPosition(new Point2D(prevPort.getPlayerX(), prevPort.getPlayerY()));
            player.setDirection(prevPort.getPlayerDir());
        } else {
            //LOGGER.log(Level.INFO, "Set player position to: {0},{1}", new Object[]{0.5, 0.66});
            setPlayerPosition(new Point2D(0.5, 0.66));
            player.setDirection(PoseSheet.Direction.TOWARD);
        }

        LOGGER.finest("do debug colllision bounds");
        debugCollisionBounds(showCollision);
        debugHearingBounds(showHearing);

        setOnMouseClicked((event) -> {
            if (dialogOverlay == null) { // As long as dialog is not showing.
                player.walkToward(event.getX(), event.getY(), walkArea);
            }
            event.consume();
        });

        LOGGER.log(Level.CONFIG, "[Vignette] \"{0}\" loaded.", getName());
    }

    protected abstract void init();

    protected abstract void loop();

    public abstract String getPropName();

    /**
     * @return the player
     */
    public final Player getPlayer() {
        return player;
    }

    /**
     *
     * @param input list of keyboard events
     * @return next room to load or @null to remain in current room
     */
    protected final Port processEvents(ArrayList<String> input) {

        if (dialogOverlay == null) {
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

            for (Port door : doors) {
                boolean playerExited = player.colidesWith(door);
                door.updateTriggerState(playerExited);
                if (playerExited) {
                    // Return the players pose skin back to default.
                    getPlayer().useDefaultSkin();
                    LOGGER.config("player triggered door.");
                    return door;
                }
            }

            getCharacterList().forEach((Character npc) -> {
                // Show dialog if player can hear.
                if (npc.canHear(player.getHearingBoundary())) {
                    npc.showTalkIcon(true);
                    if (playerTalkToNPC) {
                        npc.setTalking(true);
                        playerTalkToNPC = false;  // Consume event.
                    }
                } else {
                    npc.showTalkIcon(false);
                }
                if (npc.isTalking() && !npc.getDialog().isActionDone()) {
                    //mode = new DialogScreen(player, npc, width, height);
                    dialogOverlay = npc.getDialog();
                    addNode(dialogOverlay);
                    dialogOverlay.toFront();
                    LOGGER.warning("Show Dialog Mode.");
                }
            });
        } else {
            // Alternate mode is overlayed like a DialogScreen.  Handle that.
            if (dialogOverlay.isActionDone()) {
                removeNode(dialogOverlay);

                // See if the dialog invoked a scene exit event.
                Port exit = dialogOverlay.getExit();
                dialogOverlay = null;

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
            player.moveLeft(11, walkArea);
        }

        if (input.contains("RIGHT")) {
            input.remove("RIGHT");
            player.moveRight(11, walkArea);
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
            LOGGER.config("User talked to NPC.");
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
            door.setOpacity(show ? debugOpacity : 0.0);
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
    public ArrayList<Port> getDoors() {
        return doors;
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
            String xS = String.valueOf(points.get(i));
            String yS = String.valueOf(points.get(i+1));
            Text t = new Text(
                    vX, vY,
                    xS.substring(0, xS.lastIndexOf('.')) + "," + yS.substring(0,yS.lastIndexOf('.'))
            );
            t.setFill(Color.LIGHTGREEN);
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
        InputStream is = getClass().getResourceAsStream(backgroundName);
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
        p.setProperty(prefix, "visited");
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

        // load child properties
        loadProperties(p);
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

    protected void addPort(Port port) {
        port.setScale(getWidth(), getHeight());
        getDoors().add(port);
        getMainGroup().getChildren().add(port);
    }

    protected void addPatch(Patch patch) {
        getPatchList().add(patch);
        getFgGroup().getChildren().add(patch);
        getFgGroup().getChildren().add(patch.getBox());
    }
    
    public String getNarration() {
        return bundle.getString("narration");
    }
}
