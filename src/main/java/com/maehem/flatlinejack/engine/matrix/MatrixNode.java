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
package com.maehem.flatlinejack.engine.matrix;

import com.maehem.flatlinejack.engine.MatrixSite;
import javafx.animation.RotateTransition;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 *
 * @author mark
 */
public class MatrixNode extends Group {

    private final MatrixSite site; // Matrix Site (model)
    
    public MatrixNode( MatrixSite site ) {
        this.site = site;
        initCore();
        initShield();
        
        setRotationAxis(Rotate.Z_AXIS);
        setRotate(180);
        
    }
    
    protected void initCore() {        
        MeshView core = new MeshView(new ObjTriangleMesh(
                getClass().getResourceAsStream("/content/matrix/core/heatsink-1.obj")
        ));

 //       core.setDrawMode(DrawMode.LINE);
        core.setMaterial(new PhongMaterial(Color.LIGHTGRAY));
        core.setRotationAxis(Rotate.X_AXIS);
        //core.setRotate(90);
        
        getChildren().add(core);
    }
    
    private void initShield() {
        Group shieldGroup = new Group();
        
        BallShield bs = new BallShield(12);
        
        // Set up a Rotate Transition the Rectangle
        bs.setRotationAxis(Rotate.Y_AXIS);
        RotateTransition trans = new RotateTransition(Duration.seconds(8), bs);
        trans.setFromAngle(0.0);
        trans.setToAngle(360.0);
        trans.setCycleCount(RotateTransition.INDEFINITE); // Let the animation run forever
        trans.setAutoReverse(false); // Reverse direction on alternating cycles
        trans.play(); // Play the Animation
        
        WallShield ws = new WallShield();
        ws.setRotationAxis(Rotate.Y_AXIS);
        RotateTransition wsT = new RotateTransition(Duration.seconds(8), ws);
        wsT.setFromAngle(60.0);
        wsT.setToAngle(120.0);
        wsT.setCycleCount(RotateTransition.INDEFINITE); // Let the animation run forever
        wsT.setAutoReverse(true); // Reverse direction on alternating cycles
        wsT.play(); // Play the Animation
        
        
        shieldGroup.getChildren().addAll(bs, ws);
        
        getChildren().add(shieldGroup);
        
        
//        siteBlob.setRotationAxis(Rotate.X_AXIS);
//        RotateTransition trans2 = new RotateTransition(Duration.seconds(8), siteBlob);
//        trans2.setFromAngle(0.0);
//        trans2.setToAngle(360.0);
//        trans2.setCycleCount(RotateTransition.INDEFINITE); // Let the animation run forever
//        trans2.setAutoReverse(false); // Reverse direction on alternating cycles
//        trans2.play(); // Play the Animation
        
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
