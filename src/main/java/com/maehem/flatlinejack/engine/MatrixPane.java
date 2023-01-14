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
import com.maehem.flatlinejack.engine.matrix.BallShield;
import com.maehem.flatlinejack.engine.matrix.GroundMesh;
import com.maehem.flatlinejack.engine.matrix.ObjTriangleMesh;
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

    SubScene scene;

    public MatrixPane(GameState gs, double width, double height) {
        scene = new SubScene(demoContent(), width, height, true, SceneAntialiasing.BALANCED);

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
        LOGGER.log(Level.INFO, "FOV: " + camera.getFieldOfView());

        // Background   scene.setFill  image
        scene.setFill(new ImagePattern(backgroundImage()));
        setCenter(scene);
    }

    private Group demoContent() {
        Group root = new Group();       
        
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
        
        Group lightGroup = new Group( ambient, upperLight, lowerLight);

        root.getChildren().add(lightGroup);

        float size = 1280;

        for( int x=0; x<3; x++) {
            for ( int y=0; y<2; y++ ) {
                GroundMesh mesh = new GroundMesh(size);
                mesh.setRotationAxis(Rotate.X_AXIS);
                mesh.setRotate(90);
                
                mesh.setTranslateX((x-1)*size);
                
                mesh.setTranslateY(-220); // Negative is up
                mesh.setTranslateZ(y*size); // Negative is closer
                
                root.getChildren().add(mesh);
            }
        }

        Group siteBlob = new Group();
        Box box = new Box(40, 40, 40);
        box.setCullFace(CullFace.NONE);
        matrixRotateNode(box, 100   , 120, 150);
        
        MeshView box2 = new MeshView(new ObjTriangleMesh(
                getClass().getResourceAsStream("/content/matrix/cheese.obj")
        ));
        box2.setMaterial(new PhongMaterial(Color.BLUE));
        box2.setRotationAxis(Rotate.Y_AXIS);
        box2.setRotate(20);
       
        BallShield bs = new BallShield(12);
        
        // Set up a Rotate Transition the Rectangle
        bs.setRotationAxis(Rotate.Y_AXIS);
        RotateTransition trans = new RotateTransition(Duration.seconds(8), bs);
        trans.setFromAngle(0.0);
        trans.setToAngle(360.0);
        trans.setCycleCount(RotateTransition.INDEFINITE); // Let the animation run forever
        trans.setAutoReverse(false); // Reverse direction on alternating cycles
        trans.play(); // Play the Animation
           
        siteBlob.getChildren().addAll(box2, bs);
        siteBlob.setTranslateX(640);
        siteBlob.setTranslateY(200); // Higher is down
        siteBlob.setTranslateZ(-640); // Higher is farther away
        
        siteBlob.setRotationAxis(Rotate.X_AXIS);
        RotateTransition trans2 = new RotateTransition(Duration.seconds(8), siteBlob);
        trans2.setFromAngle(0.0);
        trans2.setToAngle(360.0);
        trans2.setCycleCount(RotateTransition.INDEFINITE); // Let the animation run forever
        trans2.setAutoReverse(false); // Reverse direction on alternating cycles
        trans2.play(); // Play the Animation
        
        
        root.getChildren().add(siteBlob);
        
        root.setRotationAxis(Rotate.X_AXIS);
        //root.setRotate(30);
        

        return root;
    }

    private Image backgroundImage() {
        Image im = new Image(getClass().getResourceAsStream("/content/matrix/background-1.png"));
        //ImageView iv = new ImageView(im);
        //iv.setPreserveRatio(true);

        return im;
    }

    /**
     * Rotate Node
     * from: https://stackoverflow.com/questions/30145414/rotate-a-3d-object-on-3-axis-in-javafx-properly
     * and:  https://github.com/jperedadnr/Leap3DFX    --  MIT License
     * 
     * @param n
     * @param alf
     * @param bet
     * @param gam 
     */
    private void matrixRotateNode(Node n, double alf, double bet, double gam) {
        double A11 = Math.cos(alf) * Math.cos(gam);
        double A12 = Math.cos(bet) * Math.sin(alf) + Math.cos(alf) * Math.sin(bet) * Math.sin(gam);
        double A13 = Math.sin(alf) * Math.sin(bet) - Math.cos(alf) * Math.cos(bet) * Math.sin(gam);
        double A21 = -Math.cos(gam) * Math.sin(alf);
        double A22 = Math.cos(alf) * Math.cos(bet) - Math.sin(alf) * Math.sin(bet) * Math.sin(gam);
        double A23 = Math.cos(alf) * Math.sin(bet) + Math.cos(bet) * Math.sin(alf) * Math.sin(gam);
        double A31 = Math.sin(gam);
        double A32 = -Math.cos(gam) * Math.sin(bet);
        double A33 = Math.cos(bet) * Math.cos(gam);

        double d = Math.acos((A11 + A22 + A33 - 1d) / 2d);
        if (d != 0d) {
            double den = 2d * Math.sin(d);
            Point3D p = new Point3D((A32 - A23) / den, (A13 - A31) / den, (A21 - A12) / den);
            n.setRotationAxis(p);
            n.setRotate(Math.toDegrees(d));
        }
    }
}
