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
import com.maehem.flatlinejack.content.matrix.HeatsinkNode;
import static com.maehem.flatlinejack.engine.MatrixPane.Direction.RIGHT;
import com.maehem.flatlinejack.engine.matrix.GroundMesh;
import com.maehem.flatlinejack.engine.matrix.MatrixNode;
import java.util.ArrayList;
import java.util.logging.Level;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

/**
 *
 * @author mark
 */
public class MatrixPane extends BorderPane {

    public enum Direction {
        LEFT, RIGHT, FORWARD, BACKWARD
    }

    private final SubScene scene;

    private MatrixSite currentSite;

    // For movement, cache a list of nodes to make
    // shifting and removing them easier.
//    private ArrayList<MatrixNode> colRightNodes = new  ArrayList<>();
//    private ArrayList<MatrixNode> colLeftNodes = new ArrayList<>();
//    private ArrayList<MatrixNode> colCenterNodes = new ArrayList<>();
//    private ArrayList<MatrixNode> rowAheadNodes = new ArrayList<>();
//    private ArrayList<MatrixNode> rowBehindNodes = new ArrayList<>();
//    private ArrayList<MatrixNode> rowMiddleNodes = new ArrayList<>();
    MatrixNode nodeCenter;
    MatrixNode nodeN;
    MatrixNode nodeS;
    MatrixNode nodeE;
    MatrixNode nodeW;

    MatrixNode nodeNE;
    MatrixNode nodeNW;
    MatrixNode nodeSE;
    MatrixNode nodeSW;

    private final Group root = new Group();

    private final GameState gameState;

    private final double nodeScaling = 0.333;
    private final double size = 1280;

    public MatrixPane(GameState gs, double width, double height) {
        this.gameState = gs;
        currentSite = gs.getSite(0x00101); // For now
        scene = new SubScene(root, width, height, true, SceneAntialiasing.BALANCED);

        PerspectiveCamera camera = new PerspectiveCamera(true);

        // Normal Camera
        camera.getTransforms().addAll(
                new Rotate(-12, Rotate.X_AXIS), // Tilt down a little
                new Translate(0, -30, -600)); // Just off the ground
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
        camera.setNearClip(100);
        camera.setFarClip(4500.0);

        root.getChildren().addAll(camera);
        scene.setCamera(camera);
        setCenter(scene);

        initBackDrop();
        initRoot();
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
                if (currentSite.getRow() < GameState.MAP_SIZE - 1) { // Determine if player can move.
                    // Yes. We can move.
                    // Remove the nodes to the South
//                    for ( MatrixNode t: rowAheadNodes ) {
//                        root.getChildren().remove(t);
//                    }
                    MatrixNode tempSW = addNeighbor(MatrixSiteNeighbor.SSW);
                    MatrixNode tempS = addNeighbor(MatrixSiteNeighbor.SS);
                    MatrixNode tempSE = addNeighbor(MatrixSiteNeighbor.SSE);

                    fadeNode(nodeNW, false);
                    fadeNode(nodeN, false);
                    fadeNode(nodeNE, false);
                    //root.getChildren().removeAll(nodeNW, nodeN, nodeNE);
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
                    currentSite = gameState.getSite(currentSite.getNeighbor(MatrixSiteNeighbor.S));
                    //LOGGER.log(Level.INFO, "Current Site is: {0}:{1}", new Object[]{currentSite.getRow(), currentSite.getCol()});
//                    rowAheadNodes = rowMiddleNodes;
//                    rowMiddleNodes = rowBehindNodes;
//                    rowBehindNodes = new ArrayList<>();
                    // Generate new sites ahead of current site.
                    nodeSW = tempSW;
                    nodeS = tempS;
                    nodeSE = tempSE;
                }
                break;
            case FORWARD:
                if (currentSite.getRow() > 1) { // Determine if player can move.
                    // Yes. We can move.
                    // Remove the nodes to the South
//                    for ( MatrixNode t: rowBehindNodes ) {
//                        root.getChildren().remove(t);
//                    }
                    MatrixNode tempNW = addNeighbor(MatrixSiteNeighbor.NNW);
                    MatrixNode tempN = addNeighbor(MatrixSiteNeighbor.NN);
                    MatrixNode tempNE = addNeighbor(MatrixSiteNeighbor.NNE);

                    fadeNode(tempN, true);
                    fadeNode(tempNW, true);
                    fadeNode(tempNE, true);
                    
                    //root.getChildren().removeAll(nodeSW, nodeS, nodeSE);
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
                    currentSite = gameState.getSite(currentSite.getNeighbor(MatrixSiteNeighbor.N));
                    //LOGGER.log(Level.INFO, "Current Site is: {0}:{1}", new Object[]{currentSite.getRow(), currentSite.getCol()});

                    nodeNW = tempNW;
                    nodeN = tempN;
                    nodeNE = tempNE;

//                    rowBehindNodes = rowMiddleNodes;
//                    rowMiddleNodes = rowAheadNodes;
//                    rowAheadNodes = new ArrayList<>();
//                    // Generate new sites ahead of current site.
//                    rowAheadNodes.add(addNeighbor(MatrixSiteNeighbor.NE));
//                    rowAheadNodes.add(addNeighbor(MatrixSiteNeighbor.N));
//                    rowAheadNodes.add(addNeighbor(MatrixSiteNeighbor.NW));
                }
                break;
            case LEFT:
                if (currentSite.getCol() > 1) { // Determine if player can move.
                    // Yes. We can move.
                    // Remove the nodes to the East
//                    for ( MatrixNode t: colRightNodes ) {
//                        root.getChildren().remove(t);
//                    }

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
                    currentSite = gameState.getSite(currentSite.getNeighbor(MatrixSiteNeighbor.W));
                    //LOGGER.log(Level.INFO, "Current Site is: {0}:{1}", new Object[]{currentSite.getRow(), currentSite.getCol()});

                    nodeNW = tempNW;
                    nodeW = tempW;
                    nodeSW = tempSW;

//                    colRightNodes = colCenterNodes;
//                    colCenterNodes = colLeftNodes;
//                    colLeftNodes = new ArrayList<>();
//                    // Generate left nodes, fill, place.
//                    colLeftNodes.add(addNeighbor(MatrixSiteNeighbor.NW));
//                    colLeftNodes.add(addNeighbor(MatrixSiteNeighbor.W));
//                    colLeftNodes.add(addNeighbor(MatrixSiteNeighbor.SW));
                }

                break;
            case RIGHT:
                if (currentSite.getCol() < GameState.MAP_SIZE - 1) { // Determine if player can move.
                    // Yes. We can move.
                    //ArrayList<MatrixNode> trashNodes = colLeftNodes;
//                    for ( MatrixNode t: colLeftNodes ) {
//                        root.getChildren().remove(t);
//                    }
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
                    currentSite = gameState.getSite(currentSite.getNeighbor(MatrixSiteNeighbor.E));
                    //LOGGER.log(Level.INFO, "Current Site is: {0}:{1}", new Object[]{currentSite.getRow(), currentSite.getCol()});

                    nodeNE = tempNE;
                    nodeE = tempE;
                    nodeSE = tempSE;

//                    colLeftNodes = colCenterNodes;
//                    colCenterNodes = colRightNodes;
//                    colRightNodes = new ArrayList<>();
//                    // Generate left nodes, fill, place.
//                    colRightNodes.add(addNeighbor(MatrixSiteNeighbor.NE));
//                    colRightNodes.add(addNeighbor(MatrixSiteNeighbor.E));
//                    colRightNodes.add(addNeighbor(MatrixSiteNeighbor.SE));
                }
                break;
        }
    }

    /**
     * Fade a Matrix Node in or out on the horizon
     * 
     * @param n the node to fade
     * @param in true=fade-in, false=fade-out
     */
    private void fadeNode(MatrixNode n, boolean in) {
        FadeTransition ft = new FadeTransition(Duration.millis(in?100:300), n);
        ft.setFromValue(in?0.0:1.0);
        ft.setToValue(in?1.0:0.0);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);
        ft.play();
    }

    private void translateNode(MatrixNode node, Direction d, boolean removeWhenDone) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(200), node);
        tt.setCycleCount(1);
        tt.setAutoReverse(false);
        if (removeWhenDone) {
            tt.setOnFinished((t) -> {
                root.getChildren().remove(node);
            });
        }
        switch (d) {
            case FORWARD:
                tt.setByZ(nodeScaling * size);
                tt.play();
                //node.setTranslateZ(nodeScaling * size + node.getTranslateZ());
                break;
            case BACKWARD:
                tt.setByZ(-nodeScaling * size);
                tt.play();
                //node.setTranslateZ(-nodeScaling * size + node.getTranslateZ());
                break;
            case LEFT: // When moving right
                tt.setByX(-nodeScaling * size);
                tt.play();
                //node.setTranslateX(-nodeScaling * size + node.getTranslateX());
                break;
            case RIGHT: // When moving left
                tt.setByX(nodeScaling * size);
                tt.play();
                //node.setTranslateX(nodeScaling * size + node.getTranslateX());
                break;

        }
    }

    private void initRoot() {

        root.getChildren().add(getLighting());
        root.getChildren().add(getGrid(size));

        // Draw Ceiling (none for 'F')
        // Test Box of a site volume
//        Box testBox = new Box(100, 100, 100);
//        testBox.setMaterial(new PhongMaterial(Color.RED));
//        testBox.setDrawMode(DrawMode.LINE);
//        testBox.setTranslateY(-50);
//        root.getChildren().add(testBox);
        nodeCenter = new HeatsinkNode(currentSite, nodeScaling * size);
        root.getChildren().add(nodeCenter);

        // Surrounding Nodes
        nodeN = addNeighbor(MatrixSiteNeighbor.N);
        nodeS = addNeighbor(MatrixSiteNeighbor.S);
        nodeE = addNeighbor(MatrixSiteNeighbor.E);
        nodeW = addNeighbor(MatrixSiteNeighbor.W);

        nodeNE = addNeighbor(MatrixSiteNeighbor.NE);
        nodeNW = addNeighbor(MatrixSiteNeighbor.NW);
        nodeSE = addNeighbor(MatrixSiteNeighbor.SE);
        nodeSW = addNeighbor(MatrixSiteNeighbor.SW);

//        colCenterNodes.add(nodeN);
//        colCenterNodes.add(nodeCenter);
//        colCenterNodes.add(nodeS);
//                
//        colLeftNodes.add(nodeNW);
//        colLeftNodes.add(nodeW);
//        colLeftNodes.add(nodeSW);
//        
//        colRightNodes.add(nodeNE);
//        colRightNodes.add(nodeE);
//        colRightNodes.add(nodeSE);
//        
//        rowAheadNodes.add(nodeNW);
//        rowAheadNodes.add(nodeN);
//        rowAheadNodes.add(nodeNE);
//        
//        rowMiddleNodes.add(nodeW);
//        rowMiddleNodes.add(nodeCenter);
//        rowMiddleNodes.add(nodeE);
//        
//        rowBehindNodes.add(nodeSW);
//        rowBehindNodes.add(nodeS);
//        rowBehindNodes.add(nodeSE);
    }

    private MatrixNode addNeighbor(MatrixSiteNeighbor n) {
        int neighbor = currentSite.getNeighbor(n);
        MatrixSite site = gameState.getSite(neighbor);
        if (site == null) {
            // Blank Site
            site = new MatrixSite(gameState, neighbor);
        }
        MatrixNode node = new MatrixNode(site, nodeScaling * size);
        node.setTranslateX(n.col * nodeScaling * size);
        node.setTranslateZ(-n.row * nodeScaling * size);
        root.getChildren().add(node);

        return node;
    }

    private Group getGrid(double size) {
        Group g = new Group();

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                GroundMesh mesh = new GroundMesh(size);
                mesh.setTranslateX((x - 1) * size);
                mesh.setTranslateZ((y - 1) * size); // Negative is closer

                g.getChildren().add(mesh);
            }
        }

        // Re-center object
        g.setTranslateX(-size / 2.0);
        g.setTranslateY(-size / 2.0);
        g.setScaleX(nodeScaling); // Lines appear sharper when we scale down a larger grid.
        g.setScaleY(nodeScaling);
        g.setScaleZ(nodeScaling);

        return g;
    }

    private Group getLighting() {
        AmbientLight ambient = new AmbientLight(new Color(0.2, 0.2, 0.25, 1.0));

        PointLight upperLight = new PointLight(Color.GRAY);
        upperLight.setMaxRange(300);
        upperLight.setTranslateY(-350);
        upperLight.setTranslateX(100);
        upperLight.setTranslateZ(100);
        //upperLight.setRotationAxis(Rotate.X_AXIS);
        //upperLight.setRotate(90);

        PointLight lowerLight = new PointLight(Color.BLUE);
        lowerLight.setMaxRange(300);
        Box lightBox = new Box(20, 20, 20);
        lightBox.setMaterial(new PhongMaterial(Color.BLUE));
        lightBox.setDrawMode(DrawMode.LINE);
        Group light1 = new Group(lowerLight, lightBox);

        light1.setTranslateY(-100);
        light1.setTranslateX(-100);
        light1.setTranslateZ(-100);

        PointLight underLight = new PointLight(Color.VIOLET.darker());
        underLight.setMaxRange(300);
        //underLight.setColor(Color.MAGENTA);
        Box lightBox2 = new Box(20, 20, 20);
        lightBox2.setMaterial(new PhongMaterial(Color.BLUE));
        lightBox2.setDrawMode(DrawMode.LINE);
        Group light2 = new Group(underLight, lightBox2);

        light2.setTranslateY(-20);
        light2.setTranslateX(0);
        light2.setTranslateZ(-200);

        return new Group(/*ambient, upperLight, */light1, light2);
    }

    private void initBackDrop() {
        // Background   scene.setFill  image
        ImageView backdrop = backgroundImage();
        backdrop.getTransforms().add(
                new Translate(
                        -backdrop.getImage().getWidth() / 6.0,
                        0, //-backdrop.getImage().getHeight()/2.0,
                        1980.0
                )
        );
        backdrop.setScaleY(2.0);
        backdrop.setScaleX(3.0);
        root.getChildren().add(backdrop);
    }

    private ImageView backgroundImage() {
        Image im = new Image(getClass().getResourceAsStream("/content/matrix/background-1.png"));

        ImageView iv = new ImageView(im);
        iv.setPreserveRatio(true);

        return iv;
    }

}
