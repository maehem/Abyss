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
import com.maehem.flatlinejack.engine.matrix.GroundMesh;
import com.maehem.flatlinejack.engine.matrix.MatrixNode;
import java.util.logging.Level;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
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

/**
 *
 * @author mark
 */
public class MatrixPane extends BorderPane {

    private final SubScene scene;
    
    private MatrixSite currentSite;
    
    private final Group root = new Group();
    private final GameState gameState;
    
    private final double nodeScaling = 0.333;
    private final double size = 1280;

    public MatrixPane(GameState gs, double width, double height) {
        this.gameState = gs;
        currentSite = gs.getSite(0x00101 ); // For now
        scene = new SubScene(root, width, height, true, SceneAntialiasing.BALANCED);
        
        PerspectiveCamera camera = new PerspectiveCamera(true);
        
        // Normal Camera
        camera.getTransforms().addAll (
                new Rotate(-12, Rotate.X_AXIS),  // Tilt down a little
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
//        camera.setFieldOfView(20.0);
        
        //LOGGER.log(Level.INFO, "FOV: {0}", camera.getFieldOfView());
        
        camera.setNearClip(100);
        camera.setFarClip(4500.0);
        
        root.getChildren().add(camera);
        scene.setCamera(camera);
        setCenter(scene);

        initBackDrop();        
        updateRoot();
    }

    private void initBackDrop() {
        // Background   scene.setFill  image
        ImageView backdrop = backgroundImage();
        backdrop.getTransforms().add(
                new Translate(
                        -backdrop.getImage().getWidth()/6.0, 
                        0, //-backdrop.getImage().getHeight()/2.0,
                        1980.0
                )
        );
        backdrop.setScaleY(2.0);
        backdrop.setScaleX(3.0);
        root.getChildren().add(backdrop);        
    }
    
    private void updateRoot() {
        //root.getChildren().clear();
        
        root.getChildren().add(getLighting());
        root.getChildren().add(getGrid(size));
        
        // Draw Ceiling (none for 'F')
        
        
        // Test Box of a site volume
//        Box testBox = new Box(100, 100, 100);
//        testBox.setMaterial(new PhongMaterial(Color.RED));
//        testBox.setDrawMode(DrawMode.LINE);
//        testBox.setTranslateY(-50);
//        root.getChildren().add(testBox);
 
        
        MatrixNode matrixNode = new HeatsinkNode(currentSite, nodeScaling*size);
        root.getChildren().add(matrixNode);
        
        // Surrounding Nodes
        addNeighbor(MatrixSiteNeighbor.N);
        addNeighbor(MatrixSiteNeighbor.S);
        addNeighbor(MatrixSiteNeighbor.E);
        addNeighbor(MatrixSiteNeighbor.W);
        
        addNeighbor(MatrixSiteNeighbor.NE);
        addNeighbor(MatrixSiteNeighbor.NW);
        addNeighbor(MatrixSiteNeighbor.SE);
        addNeighbor(MatrixSiteNeighbor.SW);
             
    }

    private void addNeighbor( MatrixSiteNeighbor n) {
        int neighborE = currentSite.getNeighbor(n);
        MatrixSite siteE = gameState.getSite(neighborE);
        if ( siteE == null ) {
            // Blank Site
            siteE = new MatrixSite(gameState, neighborE);
        }
        MatrixNode nodeE = new MatrixNode(siteE, nodeScaling*size );
        nodeE.setTranslateX(n.col*nodeScaling*size);
        nodeE.setTranslateZ(-n.row*nodeScaling*size);
        root.getChildren().add(nodeE);
     }
    
    private Group getGrid( double size) {
        Group g = new Group();

        for( int x=0; x<3; x++) {
            for ( int y=0; y<3; y++ ) {
                GroundMesh mesh = new GroundMesh(size);
                mesh.setRotationAxis(Rotate.X_AXIS);
                mesh.setRotate(90);                
                mesh.setTranslateX((x-1)*size);
                mesh.setTranslateZ((y-1)*size); // Negative is closer
                
                g.getChildren().add(mesh);
            }
        }
        
        // Re-center object
        g.setTranslateX(-size/2.0);
        g.setTranslateY(-size/2.0);
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
       
        return new Group( ambient, upperLight, light1, light2);
    }
    
    private ImageView backgroundImage() {
        Image im = new Image(getClass().getResourceAsStream("/content/matrix/background-1.png"));

        ImageView iv = new ImageView(im);
        iv.setPreserveRatio(true);
        
        return iv;
    }

}
