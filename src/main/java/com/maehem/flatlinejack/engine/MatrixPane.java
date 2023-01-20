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
import com.maehem.flatlinejack.engine.matrix.BallShield;
import com.maehem.flatlinejack.engine.matrix.GroundMesh;
import com.maehem.flatlinejack.engine.matrix.MatrixNode;
import com.maehem.flatlinejack.engine.matrix.ObjTriangleMesh;
import com.maehem.flatlinejack.engine.matrix.WallShield;
import java.util.logging.Level;
import javafx.animation.RotateTransition;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 *
 * @author mark
 */
public class MatrixPane extends BorderPane {

    private final SubScene scene;
    
    private MatrixSite currentSite;
    
    private final Group root = new Group();
    private final GameState gameState;

    public MatrixPane(GameState gs, double width, double height) {
        this.gameState = gs;
        
        scene = new SubScene(root, width, height, true, SceneAntialiasing.BALANCED);

        boolean fixedEyeAtCameraZero = false;
        PerspectiveCamera camera = new PerspectiveCamera(fixedEyeAtCameraZero);
        //camera.setTranslateX(150);
        //camera.setTranslateY(-100);
        camera.setTranslateZ(300); // Higher is forward
        camera.setRotationAxis(Rotate.X_AXIS);
        camera.setRotate(-10);
        camera.setNearClip(0.01);
        scene.setCamera(camera);
        
        camera.setFieldOfView(30.0);
        LOGGER.log(Level.INFO, "FOV: {0}", camera.getFieldOfView());

        // Background   scene.setFill  image
        scene.setFill(new ImagePattern(backgroundImage()));
        setCenter(scene);
        
        updateRoot();
    }

    private void updateRoot() {
        root.getChildren().clear();
        
        root.getChildren().add(getLighting());
        root.getChildren().add(getGround());
        
        // Draw Ceiling (none for 'F')
        //MatrixNode matrixNode = new MatrixNode(currentSite);
        MatrixNode matrixNode = new HeatsinkNode(currentSite);
        root.getChildren().add(matrixNode);
        matrixNode.setTranslateX(640);
        matrixNode.setTranslateY(200); // Higher is down
        matrixNode.setTranslateZ(-640); // Higher is farther away
            
    }

    private Group getGround() {
        Group g = new Group();
        
        float size = 1280;

        for( int x=0; x<3; x++) {
            for ( int y=0; y<2; y++ ) {
                GroundMesh mesh = new GroundMesh(size);
                mesh.setRotationAxis(Rotate.X_AXIS);
                mesh.setRotate(90);
                
                mesh.setTranslateX((x-1)*size);
                
                mesh.setTranslateY(-220); // Negative is up
                mesh.setTranslateZ(y*size); // Negative is closer
                
                g.getChildren().add(mesh);
            }
        }
        
        initFloorDecor();
        
        return g;
    }
    
    private void initFloorDecor() {
        
    }
    
   private Group getLighting() {
        AmbientLight ambient = new AmbientLight(new Color(0.2, 0.2, 0.25, 1.0));
        PointLight upperLight = new PointLight();
        upperLight.setColor(Color.WHITE);
        upperLight.setTranslateY(-500);
        upperLight.setTranslateX(600);
        upperLight.setTranslateZ(-700);
        upperLight.setRotationAxis(Rotate.X_AXIS);
        //upperLight.setRotate(90);
        
        PointLight lowerLight = new PointLight();
        lowerLight.setColor(Color.BLUE);
        lowerLight.setTranslateY(100);
        lowerLight.setTranslateX(440);
        lowerLight.setTranslateZ(-700);
        lowerLight.setRotationAxis(Rotate.X_AXIS);
        lowerLight.setRotate(90);
        
        return new Group( ambient, upperLight, lowerLight);
    }
    
    private Image backgroundImage() {
        Image im = new Image(getClass().getResourceAsStream("/content/matrix/background-1.png"));

        return im;
    }

}
