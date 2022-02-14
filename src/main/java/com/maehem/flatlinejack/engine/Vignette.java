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
import java.util.ArrayList;
import java.util.Properties;
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

    /**
     * @return the narrationPane
     */
    public NarrationPane getNarrationPane() {
        return narrationPane;
    }

    public static final Logger log = Logger.getLogger("flatline");

    //public static final String PROP_SEEN_KEY = "seen";
    //public static final String PROP_NAME_KEY = "name";
    public static final String PROP_PREFIX = "vignette.";

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
    private DialogScreen mode;

    public Vignette(int w, int h, String assetFolderName, Port prevPort, Player player) {
        this.width = w;
        this.height = h;
        this.assetFolderName = assetFolderName;
        this.player = player;


        add(bgGroup);
        add(mainGroup);
        add(fgGroup);
        add(narrationPane);
        //add(gui);

        walkArea = new Polygon(0, 0, w, 0, w, h, 0, h); // Default. User should redifine in init()

        //player = new Player();
        initBackdrop();
        init();  // Called in Loop

        // This will overwrite any player position defaults set in the implementing Vignette
        if (prevPort != null && prevPort.getPlayerX() >= 0 && prevPort.getPlayerY() >= 0) {
            player.setLayoutX(prevPort.getPlayerX());
            player.setLayoutY(prevPort.getPlayerY());
            player.setDirection(prevPort.getPlayerDir());
        }

        showCollisionBounds(showCollision);

        setOnMouseClicked((event) -> {
            if (mode == null) { // As long as dialog is not showing.
                player.walkToward(event.getX(), event.getY(), walkArea);

                // debug: draw a small box where suer clicked
                Circle circle = new Circle();
                circle.setCenterX(event.getX());
                circle.setCenterY(event.getY());
                circle.setRadius(8);
                circle.setStroke(Color.MAGENTA);
                circle.setStrokeWidth(2);
                circle.setFill(Color.TRANSPARENT);
                bgGroup.getChildren().add(circle);
                
                Circle playerCircle = new Circle();
                playerCircle.setCenterX(player.getLayoutX());
                playerCircle.setCenterY(player.getLayoutY());
                playerCircle.setRadius(8);
                playerCircle.setStroke(Color.LIGHTBLUE);
                playerCircle.setStrokeWidth(2);
                playerCircle.setFill(Color.TRANSPARENT);
                bgGroup.getChildren().add(playerCircle);
            }
        });
        
        setOnKeyPressed((k) -> {
            log.log(Level.CONFIG, "Key pressed: [{0}]", name);
            //System.out.println("["+ name + "] Key Pressed... ");
        });

        narrationPane.setLayoutX(width - narrationPane.getPrefWidth());
        narrationPane.setLayoutY(height - NarrationPane.MENU_TAB_SHOW);

        narrationPane.setTitle(name);
        
        // Start a timer for 10 seconds.  Mark vignette as visited.
        
        // Debug for walk cycle
        //mainGroup.getChildren().add(createWalkPanel());

        log.log(Level.CONFIG, "{0} scene loaded.", getName());
    }

    private void add(Node node) {
        getChildren().add(node);
    }

    private void remove(Node node) {
        getChildren().remove(node);
    }

    protected abstract void init();
    
    public abstract String getPropName();
    

    /**
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    public Port processEvents(ArrayList<String> input) {

        if (mode == null) {
            if (!input.isEmpty()) {
                log.log(Level.CONFIG, "keyboard input:  {0}", input.toString());

                processUDLR(input);
                processDebugKeys(input);
                processHotKeys(input);
                input.clear();
            }

            getPatchList().forEach((patch) -> {
                getFgGroup().setVisible(player.getLayoutY() < patch.getThreshold());
            });
            //getDoors().forEach((<Port> door) -> {
            for (Port door : doors) {
                boolean playerExited = player.colidesWith(door.getTrigger());
                door.updateTriggerState(playerExited);
                if (playerExited) {
                    // Return the players pose skin back to dedault.
                    getPlayer().useDefaultSkin();
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
                    mode = npc.getDialog();
                    add(mode);
                    mode.toFront();
                    log.warning("Show Dialog Mode.");
                }
            });
        } else {
            // Alternate mode is overlayed like a DialogScreen.  Handle that.
            if (mode.isActionDone()) {
                remove(mode);

                Port exit = mode.getExit();
                mode = null;

                // If mode/dialog set the exit door then return that.
                if (exit != null) {
                    return exit;
                }
            }
        }

        return null;
    }

    public void processUDLR(ArrayList<String> input) {
        if (input.contains("LEFT")) {
            player.moveLeft(11, walkArea);
        }

        if (input.contains("RIGHT")) {
            player.moveRight(11, walkArea);
        }

        if (input.contains("UP")) {
            player.moveUp(6, walkArea);
        }

        if (input.contains("DOWN")) {
            player.moveDown(6, walkArea);
        }
    }

    public void processDebugKeys(ArrayList<String> input) {
        if (input.contains("C")) {
            input.remove("C");
            showCollision = !showCollision;
            showCollisionBounds(showCollision);
        }
        if (input.contains("W")) {
            input.remove("W");
            showWalk = !showWalk;
            showWalkPanel(showWalk);
        }

    }

    public void processHotKeys(ArrayList<String> input) {
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

    protected final void showCollisionBounds(boolean show) {
        String colStatus;
        if (show) {
            colStatus = "Showing";
        } else {
            colStatus = "Hidden";
        }
        log.log(Level.FINER, "Collision Bounds: {0}", colStatus);

        getWalkArea().setOpacity(show ? 1.0 : 0.0);
        walkAreaCoords.setOpacity(show ? 1.0 : 0.0);
        getFgGroup().setOpacity(show ? 0.7 : 1.0);
    //    player.showCollisionBounds(show);  // player is a child of this node.
        doors.forEach((p) -> {
            p.setVisible(show);
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
    
//    private Node createWalkPanel() {
//        Rectangle r = new Rectangle(200, 400, Color.WHITE);
//        Character c = new Character("LEFT");
//        Group g = new Group(r, c);
//
//        c.setDirection(PoseSheet.Direction.AWAY);
//        c.setLayoutX(100);
//        c.setLayoutY(300);
//        c.setScale(2.0);
//        g.setLayoutX(300);
//        g.setLayoutY(200);
//
//        Timeline walkCycle = new Timeline(
//                new KeyFrame(Duration.millis(60), (ActionEvent event) -> {
//                    c.nextPose();
//        }));
//        
//        walkCycle.setCycleCount(Timeline.INDEFINITE);
//        walkCycle.play();
//
//        return g;
//    }
    
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
    public Group getMainGroup() {
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
        mainGroup.getChildren().add(0,this.walkArea);
        
        ObservableList<Double> points = walkArea.getPoints();
        for ( int i=0; i< points.size(); i+=2 ) {
            Text t = new Text(
                    points.get(i), points.get(i+1), 
                    points.get(i) + "," + points.get(i+1)
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
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
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
        if ( is == null ) {
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
                
        if ( !p.containsKey(PROP_PREFIX + getPropName()) ) {
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

}
