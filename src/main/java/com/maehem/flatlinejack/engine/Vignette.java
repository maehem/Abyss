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

import com.maehem.flatlinejack.engine.babble.DialogScreen;
import com.maehem.flatlinejack.engine.gui.NarrationPane;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

/**
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public abstract class Vignette extends Group {

    public static final Logger log = Logger.getLogger("flatline");

    public static final String PROP_PREFIX = "vignette.";
    public static final double DEFAULT_SCALE = 4.8;  // Scale character up when they reach the fourth wall.
    public static final double DEFAULT_HORIZON = 0.24;  // Place horizon this far down from screen top.  0.0 - 1.0

    private double playerScale = DEFAULT_SCALE;
    private double horizon = DEFAULT_HORIZON;
    private final Player player;
    protected boolean showCollision = false;
    protected boolean showWalk = false;
    private Polygon walkArea;
    private final Group walkAreaCoords = new Group();
    private boolean playerTalkToNPC = false;
    private final String assetFolderName;
    private final Group bgGroup = new Group();
    private final Group mainGroup = new Group(walkAreaCoords);
    private final Group fgGroup = new Group();
    private final NarrationPane narrationPane = new NarrationPane();

    private final ArrayList<Port> doors = new ArrayList<>();
    private final ArrayList<Patch> patchList = new ArrayList<>();
    private final ArrayList<Character> characterList = new ArrayList<>();

    private String name = "<unnamed>";
    private final double width;
    private final double height;
    private DialogScreen dialogOverlay;
    private double debugOpacity = 0.7;
    public ResourceBundle bundle;

    public Vignette(int w, int h, String assetFolderName, Port prevPort, Player player) throws MissingResourceException{
        this.width = w;
        this.height = h;
        this.assetFolderName = assetFolderName;
        this.player = player;

        log.log(Level.CONFIG, "class name: {0}", super.getClass().getSimpleName());

        add(bgGroup);
        add(mainGroup);
        add(fgGroup);
        add(narrationPane);

        walkArea = new Polygon(0, 0, w, 0, w, h, 0, h); // Default. User should redifine in init()

        getMainGroup().getChildren().addAll(getPlayer());
        //log.config("call initBackdrop()");
        log.config("vName: " + this.getClass().getSimpleName());

        // Load the localization bundle for this Vignette
        String bPath = "content.messages." + this.getClass().getSimpleName();
        try {
            this.bundle = ResourceBundle.getBundle(bPath);

            initBackdrop();
            setName(bundle.getString("title"));
            getNarrationPane().setText(bundle.getString("narration"));
            log.config("call init()");
            init();
        } catch (MissingResourceException ex) {
            log.log(Level.WARNING,
                    "Unable to locate vignette resource bundle at: {0}", bPath);
            
            // TODO:  maybe load a default bundle here.
            throw ex;
        }

        log.config("more stuff");
        
        // This will overwrite any player position defaults set in the implementing Vignette
        // Example: Player left through right door and you want them to appear in the next
        // vignette's left side.
        if (prevPort != null && prevPort.getPlayerX() >= 0 && prevPort.getPlayerY() >= 0) {
            log.config("set player xy override");
            player.setLayoutX(prevPort.getPlayerX());
            player.setLayoutY(prevPort.getPlayerY());
            player.setDirection(prevPort.getPlayerDir());
        }

        log.config("do debug colllision bounds");
        debugCollisionBounds(showCollision);

        setOnMouseClicked((event) -> {
            if (dialogOverlay == null) { // As long as dialog is not showing.
                player.walkToward(event.getX(), event.getY(), walkArea);

//                // debug: draw a small box where suer clicked
//                Circle circle = new Circle();
//                circle.setCenterX(event.getX());
//                circle.setCenterY(event.getY());
//                circle.setRadius(8);
//                circle.setStroke(Color.MAGENTA);
//                circle.setStrokeWidth(2);
//                circle.setFill(Color.TRANSPARENT);
//                bgGroup.getChildren().add(circle);
//                
//                Circle playerCircle = new Circle();
//                playerCircle.setCenterX(player.getLayoutX());
//                playerCircle.setCenterY(player.getLayoutY());
//                playerCircle.setRadius(8);
//                playerCircle.setStroke(Color.LIGHTBLUE);
//                playerCircle.setStrokeWidth(2);
//                playerCircle.setFill(Color.TRANSPARENT);
//                bgGroup.getChildren().add(playerCircle);
            }
            event.consume();
        });

//        setOnKeyPressed((k) -> {
//            log.log(Level.CONFIG, "Vignette Key pressed: [{0}]", name);
//        });
        narrationPane.setLayoutX(width - narrationPane.getPrefWidth());
        narrationPane.setLayoutY(height - NarrationPane.MENU_TAB_SHOW);
        narrationPane.setTitle(name);

        // Start a timer for 10 seconds.  Mark vignette as visited.
        // Debug for walk cycle
        //mainGroup.getChildren().add(createWalkPanel());
        log.log(Level.CONFIG, "[Vignette] \"{0}\" loaded.", getName());
    }

    private void add(Node node) {
        getChildren().add(node);
    }

    private void remove(Node node) {
        getChildren().remove(node);
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
                log.log(Level.FINE, "vignette process input event:  {0}", input.toString());

                processUDLR(input);
                processDebugKeys(input);
                processHotKeys(input);
                input.clear();
            }

            getPatchList().forEach((patch) -> {
                getFgGroup().setVisible(player.getLayoutY() < patch.getThreshold());
            });

            for (Port door : doors) {
                boolean playerExited = player.colidesWith(door);
                door.updateTriggerState(playerExited);
                if (playerExited) {
                    // Return the players pose skin back to default.
                    getPlayer().useDefaultSkin();
                    log.config("player triggered door.");
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
                    add(dialogOverlay);
                    dialogOverlay.toFront();
                    log.warning("Show Dialog Mode.");
                }
            });
        } else {
            // Alternate mode is overlayed like a DialogScreen.  Handle that.
            if (dialogOverlay.isActionDone()) {
                remove(dialogOverlay);

                // See if the dialog invoked a scene exit event.
                Port exit = dialogOverlay.getExit();
                dialogOverlay = null;

                // If mode/dialog set the exit door then return that.
                if (exit != null) {
                    return exit;
                }
            }
        }

        getPlayer().setScale(getPlayerScale() * (getPlayer().getLayoutY() / getHeight() - getHorizon())
        );
        loop();

        return null;
    }

    public void processUDLR(ArrayList<String> input) {
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
        if (input.contains("W")) {
            input.remove("W");
            showWalk = !showWalk;
            showWalkPanel(showWalk);
        }
    }

    private void processHotKeys(ArrayList<String> input) {
        if (input.contains("S")) {
            input.remove("S");
            log.config("User saved game.");
        }
        if (input.contains("T")) {
            input.remove("T");
            playerTalkToNPC = true;
            log.config("User talked to NPC.");
        }
    }

    protected final void debugCollisionBounds(boolean show) {
        String colStatus;
        if (show) {
            colStatus = "Showing";
        } else {
            colStatus = "Hidden";
        }
        log.log(Level.FINER, "Collision Bounds: {0}", colStatus);

        getWalkArea().setOpacity(show ? debugOpacity : 0.0);
        walkAreaCoords.setOpacity(show ? debugOpacity : 0.0);
        //getFgGroup().setOpacity(show ? 0.5 : 1.0); // Wings translucent when debuging.
        getFgGroup().setOpacity(show ? 0.2 : 1.0); // Wings translucent when debuging.
        getDoors().forEach((door) -> {
            door.setOpacity(show ? debugOpacity : 0.0);
        });
        for (Node n : getChildren()) {
            if (n instanceof Character) {
                ((Character) n).showCollisionBounds(show);
            }

            if (n instanceof Group) {
                for (Node nn : ((Group) n).getChildren()) {
                    if (nn instanceof Character) {
                        ((Character) nn).showCollisionBounds(show);
                    }
                }
            }
        }
    }

    protected final void showWalkPanel(boolean show) {
        // Walk window set opacity show.
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
     * @param walkArea the walkArea to set
     */
    public void setWalkArea(Polygon walkArea) {
        mainGroup.getChildren().remove(this.walkArea);
        walkAreaCoords.getChildren().clear();
        this.walkArea = walkArea;
        mainGroup.getChildren().add(0, this.walkArea);

        ObservableList<Double> points = walkArea.getPoints();
        for (int i = 0; i < points.size(); i += 2) {
            Text t = new Text(
                    points.get(i), points.get(i + 1),
                    points.get(i) + "," + points.get(i + 1)
            );
            t.setFill(Color.LIGHTGREEN);
            walkAreaCoords.getChildren().add(t);
        }

        getWalkArea().setFill(Color.TRANSPARENT);
        Color AQUA = Color.AQUA;
        getWalkArea().setStroke(new Color(AQUA.getRed(), AQUA.getGreen(), AQUA.getBlue(), 0.5));
        getWalkArea().setStrokeWidth(3.0);
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
    public void setName(String name) {
        log.config("set name to:" + name);
        this.name = name;
    }

    /**
     * @return the width
     */
    public double getWidth() {
        return width;
    }

    /**
     * @return the height
     */
    public double getHeight() {
        return height;
    }

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

    public String getNarrationText() {
        return "This is a test.";
    }

    private void initBackdrop() {
        // Load images for BACKGROUND_IMAGE_FILENAME.
        String backgroundName = assetFolderName + "backdrop.png";
        InputStream is = getClass().getResourceAsStream(backgroundName);
        if (is == null) {
            log.log(Level.SEVERE, "Cannot find image for: {0}", backgroundName);
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

        if (!p.containsKey(PROP_PREFIX + getPropName())) {
            // Pop the narration pane if this vignette has never been saved.
            // which means the player has not yet been here.
            narrationPane.pop();
        }

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

    /**
     * @return the narrationPane
     */
    public NarrationPane getNarrationPane() {
        return narrationPane;
    }

}
