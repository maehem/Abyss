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
import com.maehem.flatlinejack.engine.gui.bbs.BBSTerminal;
import com.maehem.flatlinejack.engine.matrix.MatrixNodeFactory;
import com.maehem.flatlinejack.engine.matrix.MatrixSiteNeighbor;
import com.maehem.flatlinejack.engine.matrix.MatrixSite;
import com.maehem.flatlinejack.engine.matrix.MatrixNode;
import com.maehem.flatlinejack.engine.matrix.SoftwareTabNode;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

/**
 *
 * @author mark
 */
public class MatrixPane extends BorderPane implements GameStateListener {

    private static final GameState.Display display = GameState.Display.MATRIX;
    
    private boolean showSoftwareTabs = false;
    private boolean showTerminalTab;

    final static double TERM_TAB_H = 180.0;
    final static double TERM_TAB_W = 800.0;

    public enum Direction {
        LEFT, RIGHT, FORWARD, BACKWARD
    }

    private final SubScene scene;

    private MatrixSite previousSite; // used to track when we get change events.

    // For movement, cache a list of nodes to make
    // shifting and removing them easier.
    MatrixNode nodeCenter;
    MatrixNode nodeN;
    MatrixNode nodeS;
    MatrixNode nodeE;
    MatrixNode nodeW;

    MatrixNode nodeNE;
    MatrixNode nodeNW;
    MatrixNode nodeSE;
    MatrixNode nodeSW;

    private final StackPane paneGroup = new StackPane();
    private final Group root = new Group();
    private final Group siteGroup = new Group();
    private final Group hudGroup = new Group();
    private final Group cameraHudGroup;
    private final HBox hudTabs = new HBox();
    private final StackPane terminalTab = new StackPane();

    private final GameState gameState;

    private final double nodeScaling = 0.333;
    private final double size = 1280;

    public MatrixPane(GameState gs, double width, double height) {
        this.gameState = gs;
        gameState.addListenter(this);
        previousSite = gameState.getCurrentMatrixSite();
        
        //currentSite = gs.getSite(0x00101); // For now

        scene = new SubScene(root, width, height, true, SceneAntialiasing.BALANCED);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(1);
        camera.setFarClip(2800.0);
        
        cameraHudGroup = new Group(camera, hudGroup);

        // Normal Camera
        cameraHudGroup.getTransforms().addAll(
                new Rotate(-12, Rotate.X_AXIS), // Tilt down a little
                new Translate(0, -22, -600)); // Just off the ground
        camera.setFieldOfView(30.0);

//        // DEBUG:  Ortho Camera
//        camera.getTransforms().addAll (
//                new Rotate(-90, Rotate.X_AXIS),  // Tilt down a little
//                new Translate(0, 80, -1600)); // Just off the ground
//        camera.setFieldOfView(45.0);
        // Top down
//        camera.getTransforms().addAll (
//                new Rotate(-90, Rotate.X_AXIS),  // Tilt down a little
//                new Translate(0, 80, -1600)); // Just off the ground
//        camera.setFieldOfView(40.0);
        //LOGGER.log(Level.INFO, "FOV: {0}", camera.getFieldOfView());
        

        scene.setCamera(camera);
        paneGroup.getChildren().add(scene);
        setCenter(paneGroup);

        Rectangle r = new Rectangle(200, 100);
        r.setFill(Color.RED);
                
        initBackDrop();
        root.getChildren().add(siteGroup);
        initRoot();
        //root.getChildren().add(hudGroup);
        initHUD();
        root.getChildren().addAll(cameraHudGroup);
        
        setVisible(false);

    }

    private MatrixSite getCurrentSite() {
        return gameState.getCurrentMatrixSite();
    }
    
    void processEvents(ArrayList<String> input) {
        if (input.contains("LEFT")) {
            input.remove("LEFT");
            move(Direction.LEFT);
        }

        if (input.contains("RIGHT")) {
            input.remove("RIGHT");
            move(Direction.RIGHT);
        }

        if (input.contains("UP")) {
            input.remove("UP");
            move(Direction.FORWARD);
        }

        if (input.contains("DOWN")) {
            input.remove("DOWN");
            move(Direction.BACKWARD);
        }
    }

    public void move(Direction d) {
        switch (d) {
            case BACKWARD:
                if (gameState.getCurrentMatrixSite().getRow() < GameState.MAP_SIZE - 1) { // Determine if player can move.
                    // Yes. We can move.
                    // Remove the nodes to the South
                    MatrixNode tempSW = addNeighbor(MatrixSiteNeighbor.SSW);
                    MatrixNode tempS = addNeighbor(MatrixSiteNeighbor.SS);
                    MatrixNode tempSE = addNeighbor(MatrixSiteNeighbor.SSE);

                    fadeNode(nodeNW, false);
                    fadeNode(nodeN, false);
                    fadeNode(nodeNE, false);

                    // Translate items in site groups
                    translateNode(tempSW, Direction.FORWARD, false);
                    translateNode(tempS, Direction.FORWARD, false);
                    translateNode(tempSE, Direction.FORWARD, false);

                    translateNode(nodeSW, Direction.FORWARD, false);
                    translateNode(nodeS, Direction.FORWARD, false);
                    translateNode(nodeSE, Direction.FORWARD, false);

                    translateNode(nodeW, Direction.FORWARD, false);
                    translateNode(nodeCenter, Direction.FORWARD, false);
                    translateNode(nodeE, Direction.FORWARD, false);

                    translateNode(nodeNW, Direction.FORWARD, true);
                    translateNode(nodeN, Direction.FORWARD, true);
                    translateNode(nodeNE, Direction.FORWARD, true);

                    nodeNW = nodeW;
                    nodeN = nodeCenter;
                    nodeNE = nodeE;

                    nodeW = nodeSW;
                    nodeCenter = nodeS;
                    nodeE = nodeSE;

                    // Update current site and get it.
                    //currentSite = gameState.getSite(currentSite.getNeighbor(MatrixSiteNeighbor.S));
                    previousSite = gameState.getCurrentMatrixSite();
                    gameState.setCurrentMatrixSite( MatrixSiteNeighbor.S );
                    //LOGGER.log(Level.INFO, "Current Site is: {0}:{1}", new Object[]{currentSite.getRow(), currentSite.getCol()});
                    // Generate new sites ahead of current site.
                    nodeSW = tempSW;
                    nodeS = tempS;
                    nodeSE = tempSE;
                }
                break;
            case FORWARD:
                if (gameState.getCurrentMatrixSite().getRow() > 1) { // Determine if player can move.

                    MatrixNode tempNW = addNeighbor(MatrixSiteNeighbor.NNW);
                    MatrixNode tempN = addNeighbor(MatrixSiteNeighbor.NN);
                    MatrixNode tempNE = addNeighbor(MatrixSiteNeighbor.NNE);

                    fadeNode(tempN, true);
                    fadeNode(tempNW, true);
                    fadeNode(tempNE, true);

                    // Translate items in site groups
                    translateNode(tempNW, Direction.BACKWARD, false);
                    translateNode(tempN, Direction.BACKWARD, false);
                    translateNode(tempNE, Direction.BACKWARD, false);

                    translateNode(nodeNW, Direction.BACKWARD, false);
                    translateNode(nodeN, Direction.BACKWARD, false);
                    translateNode(nodeNE, Direction.BACKWARD, false);

                    translateNode(nodeW, Direction.BACKWARD, false);
                    translateNode(nodeCenter, Direction.BACKWARD, false);
                    translateNode(nodeE, Direction.BACKWARD, false);

                    translateNode(nodeSW, Direction.BACKWARD, true);
                    translateNode(nodeS, Direction.BACKWARD, true);
                    translateNode(nodeSE, Direction.BACKWARD, true);

                    nodeSW = nodeW;
                    nodeS = nodeCenter;
                    nodeSE = nodeE;

                    nodeW = nodeNW;
                    nodeCenter = nodeN;
                    nodeE = nodeNE;

                    // Update current site and get it.
                    //currentSite = gameState.getSite(currentSite.getNeighbor(MatrixSiteNeighbor.N));
                    previousSite = gameState.getCurrentMatrixSite();
                    gameState.setCurrentMatrixSite( MatrixSiteNeighbor.N );
                    //LOGGER.log(Level.INFO, "Current Site is: {0}:{1}", new Object[]{currentSite.getRow(), currentSite.getCol()});

                    nodeNW = tempNW;
                    nodeN = tempN;
                    nodeNE = tempNE;

                }
                break;
            case LEFT:
                if (gameState.getCurrentMatrixSite().getCol() > 1) { // Determine if player can move.

                    MatrixNode tempNW = addNeighbor(MatrixSiteNeighbor.NWW);
                    MatrixNode tempW = addNeighbor(MatrixSiteNeighbor.WW);
                    MatrixNode tempSW = addNeighbor(MatrixSiteNeighbor.SWW);

                    // Translate items in site groups
                    translateNode(nodeN, Direction.RIGHT, false);
                    translateNode(nodeCenter, Direction.RIGHT, false);
                    translateNode(nodeS, Direction.RIGHT, false);
                    translateNode(nodeNW, Direction.RIGHT, false);
                    translateNode(nodeW, Direction.RIGHT, false);
                    translateNode(nodeSW, Direction.RIGHT, false);
                    translateNode(nodeNE, Direction.RIGHT, true);
                    translateNode(nodeE, Direction.RIGHT, true);
                    translateNode(nodeSE, Direction.RIGHT, true);
                    translateNode(tempNW, Direction.RIGHT, false);
                    translateNode(tempW, Direction.RIGHT, false);
                    translateNode(tempSW, Direction.RIGHT, false);

                    //root.getChildren().removeAll(nodeNE, nodeE, nodeSE);
                    nodeNE = nodeN;
                    nodeE = nodeCenter;
                    nodeSE = nodeS;

                    nodeN = nodeNW;
                    nodeCenter = nodeW;
                    nodeS = nodeSW;
                    // Generate new sites left of current site.
                    //currentSite = gameState.getSite(currentSite.getNeighbor(MatrixSiteNeighbor.W));
                    previousSite = gameState.getCurrentMatrixSite();
                    gameState.setCurrentMatrixSite( MatrixSiteNeighbor.W );
                    //LOGGER.log(Level.INFO, "Current Site is: {0}:{1}", new Object[]{currentSite.getRow(), currentSite.getCol()});

                    nodeNW = tempNW;
                    nodeW = tempW;
                    nodeSW = tempSW;
                }

                break;
            case RIGHT:
                if (gameState.getCurrentMatrixSite().getCol() < GameState.MAP_SIZE - 1) { // Determine if player can move.
                    // Yes. We can move.
                    //ArrayList<MatrixNode> trashNodes = colLeftNodes;
                    MatrixNode tempNE = addNeighbor(MatrixSiteNeighbor.NEE);
                    MatrixNode tempE = addNeighbor(MatrixSiteNeighbor.EE);
                    MatrixNode tempSE = addNeighbor(MatrixSiteNeighbor.SEE);

                    //root.getChildren().removeAll(nodeNW, nodeW, nodeSW);
                    // Translate items in site groups
                    translateNode(tempNE, Direction.LEFT, false);
                    translateNode(tempE, Direction.LEFT, false);
                    translateNode(tempSE, Direction.LEFT, false);
                    translateNode(nodeN, Direction.LEFT, false);
                    translateNode(nodeCenter, Direction.LEFT, false);
                    translateNode(nodeS, Direction.LEFT, false);
                    translateNode(nodeNE, Direction.LEFT, false);
                    translateNode(nodeE, Direction.LEFT, false);
                    translateNode(nodeSE, Direction.LEFT, false);

                    translateNode(nodeNW, Direction.LEFT, true); // Remove after scoot
                    translateNode(nodeW, Direction.LEFT, true);
                    translateNode(nodeSW, Direction.LEFT, true);

                    nodeNW = nodeN;
                    nodeW = nodeCenter;
                    nodeSW = nodeS;

                    nodeN = nodeNE;
                    nodeCenter = nodeE;
                    nodeS = nodeSE;

                    // Generate new sites right of current site.
                    //currentSite = gameState.getSite(currentSite.getNeighbor(MatrixSiteNeighbor.E));
                    previousSite = gameState.getCurrentMatrixSite();
                    gameState.setCurrentMatrixSite( MatrixSiteNeighbor.E );
                    //LOGGER.log(Level.INFO, "Current Site is: {0}:{1}", new Object[]{currentSite.getRow(), currentSite.getCol()});

                    nodeNE = tempNE;
                    nodeE = tempE;
                    nodeSE = tempSE;

                }
                break;
        }
        
        updateHUDshowState();
    }
    
    /**
     * Pop tabs up or down based on current status.
     */
    private void updateHUDshowState() {
        MatrixSite cs = gameState.getCurrentMatrixSite();
        LOGGER.log(Level.INFO, "Site attackable: {0}", cs.isAttackable());
        setShowSoftwareTabs(cs.isAttackable());
        LOGGER.log(Level.INFO, "Site terminal: [{0}] {1}", 
                new Object[]{cs.getAddress(), cs.terminalAvailable()}
        );
        setShowTerminalTab(cs.terminalAvailable());       
    }

    /**
     * Fade a Matrix Node in or out on the horizon
     * NOTE:  Doesn't actually fade. JavaFX 3D groups cannot be faded. This acts like
     * a visibility delay and we set it's time for half of the translation.
     *
     * @param n the node to fade
     * @param in true=fade-in, false=fade-out
     */
    private void fadeNode(MatrixNode n, boolean in) {
        FadeTransition ft = new FadeTransition(Duration.millis(in ? 100 : 100), n);
        
        ft.setFromValue(in ? 0.0 : 1.0);
        ft.setToValue(in ? 1.0 : 0.0);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);
        ft.play();
    }

    private void translateNode(MatrixNode node, Direction d, boolean removeWhenDone) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(180), node);
        tt.setCycleCount(1);
        tt.setAutoReverse(false);
        if (removeWhenDone) {
            tt.setOnFinished((t) -> {
                siteGroup.getChildren().remove(node);
            });
        }
        switch (d) {
            case FORWARD:
                tt.setByZ(nodeScaling * size);
                tt.play();
                break;
            case BACKWARD:
                tt.setByZ(-nodeScaling * size);
                tt.play();
                break;
            case LEFT: // When moving right
                tt.setByX(-nodeScaling * size);
                tt.play();
                break;
            case RIGHT: // When moving left
                tt.setByX(nodeScaling * size);
                tt.play();
                break;

        }
    }

    private void initRoot() {

        root.getChildren().add(getLighting());
        updateSiteNodes();
        
        // Draw Ceiling (none for 'F')
        
        
        // Test Box of a site volume
//        Box testBox = new Box(100, 100, 100);
//        testBox.setMaterial(new PhongMaterial(Color.RED));
//        testBox.setDrawMode(DrawMode.LINE);
//        testBox.setTranslateY(-50);
//        root.getChildren().add(testBox);
    }
    
    private void updateSiteNodes() {
        siteGroup.getChildren().clear();
        
        //nodeCenter = new HeatsinkNode(currentSite, nodeScaling * size);
        nodeCenter = MatrixNodeFactory.getNewMatrixNode(gameState.getCurrentMatrixSite(), nodeScaling * size);
        siteGroup.getChildren().add(nodeCenter);

        // Surrounding Nodes
        nodeN = addNeighbor(MatrixSiteNeighbor.N);
        nodeS = addNeighbor(MatrixSiteNeighbor.S);
        nodeE = addNeighbor(MatrixSiteNeighbor.E);
        nodeW = addNeighbor(MatrixSiteNeighbor.W);

        nodeNE = addNeighbor(MatrixSiteNeighbor.NE);
        nodeNW = addNeighbor(MatrixSiteNeighbor.NW);
        nodeSE = addNeighbor(MatrixSiteNeighbor.SE);
        nodeSW = addNeighbor(MatrixSiteNeighbor.SW);

    }

    /**
     * Software attack hudTabs
     */
    private void initHUD() {
        hudTabs.setSpacing(4);
        hudTabs.getTransforms().addAll(
                new Translate(-608, -10, 0)  // More is down. -10 a bit showing.
        );

        hudGroup.getTransforms().addAll(
                new Translate(0, 7.20, 27),  // More is down
                new Rotate(-45, Rotate.X_AXIS),
                new Scale(0.02, 0.02)
        );
        
        updateHudSoftwareTabs();
        initTerminalTab();
        hudGroup.getChildren().add(hudTabs);
    }

    private void setShowSoftwareTabs( boolean show ) {
        if ( this.showSoftwareTabs == show ) return;
        this.showSoftwareTabs = show;
        
        TranslateTransition tr = new TranslateTransition(new Duration(200), hudTabs);
        tr.setByY(show?-100:100);
        
        tr.play();
    }
    
    public void updateHudSoftwareTabs() {
        LOGGER.log(Level.INFO, "Update HUD Software Tabs");
        DeckThing currentDeck = gameState.getPlayer().getCurrentDeck();
        hudTabs.getChildren().clear();

        if (currentDeck != null) {
            List<SoftwareThing> software = currentDeck.getSoftware();
            int[] loadout = currentDeck.getSoftwareLoadout();
            for (int i = 0; i < loadout.length; i++) {
                int idx = loadout[i];
                SoftwareThing st;
                if ( idx >= 0 ) {
                    st = software.get(loadout[i]);
                } else {
                    st = new EmptySoftwareThing();
                }
                LOGGER.log(Level.INFO, "    [{0}] put software thing: {1}", new Object[]{i, st.getName()});
                hudTabs.getChildren().add(new SoftwareTabNode(st, (char) ('0' + i + 1)));                    
            }
        } else { // Empty hudTabs
            LOGGER.log(Level.INFO, "    No current deck: Fill blank HUD.");
            for (int i = 0; i < 4; i++) {
                hudTabs.getChildren().add(new SoftwareTabNode(new EmptySoftwareThing(), (char)('0' + i + 1)));
            }
        }

    }
    
    private void initTerminalTab() {
        
        final String FONT = "/fonts/VT323-Regular.ttf";
        Font termFont = Font.loadFont(getClass().getResource(FONT).toExternalForm(),
                32.0
        );

        terminalTab.setMinSize(TERM_TAB_W, TERM_TAB_H);
        terminalTab.setBackground(new Background(new BackgroundFill(
                Color.DARKGREY, 
                new CornerRadii(20, 20, 0, 0, false), 
                new Insets(4)
        )));
        terminalTab.setTranslateX(-TERM_TAB_W/2.0);
        
        Pane screenRect = new Pane();
        screenRect.setPrefSize(TERM_TAB_W-50, TERM_TAB_H-50);
        screenRect.setBackground(new Background(new BackgroundFill(
                new Color(0.2,0.2,0.2,1.0), new CornerRadii(20), 
                new Insets(10,20,0,20 )
        )));
        
        Text termTitleText = new Text("Matrix Site Service Interface");
        termTitleText.setFont(Font.font(28));
        
        Text initText = new Text(
                "HeliOS 16.3.321b (GENERIC): 2047.08.23:00.21.00.123\n"
               + "node-HR089JKJD# svcport -c -t init-term\n"
               + "port 00048D5 open...  <tap or click to enter site>"
        );
        initText.setFont(termFont);
        initText.setFill(Color.GREEN.brighter());
        initText.setLineSpacing(0.0);
        initText.setLayoutX(40);
        initText.setLayoutY(50);
        screenRect.getChildren().add( initText);
        VBox termtabItems = new VBox(termTitleText,screenRect);
        termtabItems.setFillWidth(true);
        termtabItems.setAlignment(Pos.CENTER);
        terminalTab.getChildren().add(termtabItems);
        hudGroup.getChildren().add(terminalTab);
        
        setShowTerminalTab(false);
        
        screenRect.setOnMouseClicked((t) -> {
            LOGGER.log(Level.INFO, "User clicked open terminal.");
            doTerminalOpenAnimation();
        });
    }
    
    private void setShowTerminalTab( boolean show ) {
        if ( this.showTerminalTab == show ) return;
        this.showTerminalTab = show;
        
        TranslateTransition tr = new TranslateTransition(new Duration(200), terminalTab);
        tr.setByY(show?-TERM_TAB_H:TERM_TAB_H);
        
        tr.play();
    }
    
    /**
     * Cause a rectangle to appear near the clicked element and then
     * grow rapidly, filling the screen.  Then disappear, switching out
     * the animated element with actual terminal screen Pane.
     * 
     */
    private void doTerminalOpenAnimation() {
        double w = scene.getWidth();
        double h = scene.getHeight();
        double t = 200.0;
        
        Rectangle r = new Rectangle( w, h );
        r.setFill(new Color(0.2,0.2,0.2,0.74));
        Text text = new Text(
                "[    0.000000] Machine model: Hokkaido Oni 4+ Model 6 Rev 1.4\n" +
                "[    0.000012] hsv: UHSV not found.\n" +
                "[    0.000026] Reserved memory: created CMA memory pool, size 256 PiB\n" +
                "[    0.000027] OF: reserved mem: initialized node tinux,cma, shared-dma-pool\n" +
                "[    0.000033] Zone ranges:\n" +
                "[    0.000035]   DMA      [mem 0x0000000000000000-0x000000003fffffff]\n" +
                "[    0.000086]   DMA32    [mem 0x0000000040000000-0x00000000ffffffff]\n" +
                "[    0.000098]   Normal   [mem 0x0000000100000000-0x00000001ffffffff]\n" +
                "[    0.000101] Movable zone start for each node\n" +
                "[    0.000103] Early memory node ranges\n" +
                "[    0.000103]   node   0: [mem 0x0000000000000000-0x000000001fffffff]\n" +
                "[    0.000103]   node   0: [mem 0x0000000040000000-0x00000000fbffffff]\n" +
                "[    0.000104]   node   0: [mem 0x0000000100000000-0x00000001ffffffff]\n" +
                "[    0.000109] Initmem setup node 0 [mem 0x0000000000000000-0x00000001ffffffff]\n" +
                "[    0.000111] On node 0 totalpages: 1949696\n" +
                "[    0.000113]   DMA zone: 2048 pages used for memmap\n" +
                "[    0.000114]   DMA zone: 0 pages reserved\n" +
                "[    0.000120]   DMA zone: 131072 pages, LIFO batch:31\n" +
                "[    0.000121]   DMA32 zone: 12288 pages used for memmap\n" +
                "[    0.000121]   DMA32 zone: 770048 pages, LIFO batch:63\n" +
                "[    0.000122]   Normal zone: 16384 pages used for memmap\n" +
                "[    0.000126]   Normal zone: 1048576 pages, LIFO batch:63"
         
        );
        text.setFill(new Color(0.2,1.0,0.2,0.7));
        text.setFont(BBSTerminal.FONT);
        
        StackPane tPane = new StackPane(r, text);
        
        ScaleTransition st = new ScaleTransition(new Duration(t), tPane);
        st.setFromX(0.01);
        st.setFromY(0.01);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(1);
        
        paneGroup.getChildren().add(tPane);
        st.setOnFinished((f) -> {
            paneGroup.getChildren().remove(tPane);
            gameState.setCurrentTerminal(initTerminal(gameState.getCurrentMatrixSite().getTerminal()));
            gameState.setShowing(GameState.Display.TERMINAL);
        });
        
        st.play();        
    }
    
    private BBSTerminal initTerminal( Class<? extends BBSTerminal> t ) {
        try {
            //Class<? extends MatrixNode> c = site.getNodeClass();
            //Class<?> c = Class.forName(Engine.class.getPackageName() + ".content.matrix.sitenode." + site.getNodeClass().getSimpleName() );
            Constructor<?> cons = t.getConstructor(GameState.class);
            Object object = cons.newInstance(gameState );
            LOGGER.log(Level.FINER, "Init Terminal: Loaded Terminal Node: {0}", t.getSimpleName());
            return (BBSTerminal) object;
        } catch ( //ClassNotFoundException |
                 NoSuchMethodException
                | SecurityException
                | InstantiationException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException ex) {
            ex.printStackTrace();
        }
        return null;
        
    }
    
    private MatrixNode addNeighbor(MatrixSiteNeighbor n) {
        int neighbor = gameState.getCurrentMatrixSite().getNeighbor(n);
        MatrixSite site = gameState.getSite(neighbor);
        if (site == null) {
            // Blank Site
            site = new MatrixSite(gameState, neighbor);
        }
        MatrixNode node = MatrixNodeFactory.getNewMatrixNode(site, nodeScaling * size);
        node.setTranslateX(n.col * nodeScaling * size);
        node.setTranslateZ(-n.row * nodeScaling * size);
        if ( n==MatrixSiteNeighbor.N || n==MatrixSiteNeighbor.NE || n == MatrixSiteNeighbor.NW ||
             n==MatrixSiteNeighbor.NN || n==MatrixSiteNeighbor.NNE || n == MatrixSiteNeighbor.NNW 
                )   {
            siteGroup.getChildren().add(0, node);
        } else {
            siteGroup.getChildren().add(node);            
        }

        return node;
    }

    private Group getLighting() {
        AmbientLight ambient = new AmbientLight(new Color(0.2, 0.2, 0.25, 1.0));

        PointLight upperLight = new PointLight(Color.GRAY);
        upperLight.setMaxRange(300);
        upperLight.setTranslateY(-350);
        upperLight.setTranslateX(100);
        upperLight.setTranslateZ(100);

        PointLight lowerLight = new PointLight(Color.BLUE);
        lowerLight.setMaxRange(500);
        Box lightBox = new Box(20, 20, 20);
        lightBox.setMaterial(new PhongMaterial(Color.BLUE));
        lightBox.setDrawMode(DrawMode.LINE);
        Group light1 = new Group(lowerLight, lightBox);

        light1.setTranslateY(-100);
        light1.setTranslateX(-100);
        light1.setTranslateZ(-100);

        PointLight underLight = new PointLight(Color.VIOLET.darker());
        underLight.setMaxRange(500);
        Box lightBox2 = new Box(20, 20, 20);
        lightBox2.setMaterial(new PhongMaterial(Color.BLUE));
        lightBox2.setDrawMode(DrawMode.LINE);
        Group light2 = new Group(underLight, lightBox2);

        light2.setTranslateY(-20);
        light2.setTranslateX(0);
        light2.setTranslateZ(-200);

        return new Group(/*ambient,*/ /* upperLight, */ light1, light2);
    }

    private void initBackDrop() {
        ImageView backdrop = backgroundImage();
        backdrop.getTransforms().addAll(
                new Translate(
                        -backdrop.getImage().getWidth() ,
                        -580, //-backdrop.getImage().getHeight()/2.0,
                        1900.0
                ),
                new Scale(3.0, 2.0)
        );
        root.getChildren().add(0, backdrop);
    }

    private ImageView backgroundImage() {
        Image im = new Image(getClass().getResourceAsStream("/content/matrix/background-1.png"));

        ImageView iv = new ImageView(im);
        iv.setPreserveRatio(true);

        return iv;
    }

    @Override
    public void gameStateVignetteChanged(GameState gs) {}

    @Override
    public void gameStatePropertyChanged(GameState gs, String propKey) {}

    @Override
    public void gameStateShowDebug(GameState gs, boolean state) {}

    @Override
    public void gameStateTerminalChanged(GameState gs, BBSTerminal term) {}

    @Override
    public void gameStateDisplayChanged(GameState aThis, GameState.Display d) {
        setVisible(d == display);
    }

    @Override
    public void gameStateMatrixSiteChanged(GameState gs, int newAddr) {
        if ( previousSite == gs.getCurrentMatrixSite() ) return;  // That was us, ignore.
        
        // Someone else changed out location. Update visuals.
        updateSiteNodes();
        updateHUDshowState();
    }

    
}
