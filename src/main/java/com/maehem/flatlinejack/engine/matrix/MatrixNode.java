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
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

/**
 *
 * @author mark
 */
public class MatrixNode extends Group {

    private final MatrixSite site; // Matrix Site (model)
    private final double size;
    
    public MatrixNode( MatrixSite site, double size ) {
        this.site = site;
        this.size = size;
        
        // Overridable methods, let;s fix this.
        initFloor();
        //initCore();
        //initShield();
        
        setRotationAxis(Rotate.Z_AXIS);
        setRotate(180);
        
    }
    
    private void initFloor() {
        Router floor = new Router(16, size, 
                Color.MAGENTA.darker().darker(), 10, 
                Color.BLUEVIOLET, 10, 
                Color.BLACK, 5
        );
        
        floor.setTop(site.getTopBits());
        floor.setRight(site.getRightBits());
        floor.setBottom(site.getBottomBits());
        floor.setLeft(site.getLeftBits());
        
        floor.generate();
        
        ImageView im = new ImageView(floor.snap());
        //sg.hide();
        im.getTransforms().addAll(
                new Rotate(90, Rotate.X_AXIS),  // Image was generated on difffernt plane
                new Rotate(180, Rotate.Z_AXIS),  //  It's also flipped
                new Translate(-size/2, -size/2+6, 0)   // Offset and lift a touch.
        );
        
        getChildren().add(new Group(im));
    }
    
 //   protected void initCore() {
        // Empty site by default.  Overide to add your own core.
        
//        MeshView core = new MeshView(new ObjTriangleMesh(
//                getClass().getResourceAsStream("/content/matrix/core/heatsink-1.obj")
//        ));

 //       core.setDrawMode(DrawMode.LINE);
//        core.setMaterial(new PhongMaterial(Color.LIGHTGRAY));
        //core.setRotationAxis(Rotate.X_AXIS);
        //core.setRotate(90);
        //LOGGER.log(INFO, "Core Z: " +  core.getBoundsInLocal().getHeight());
//        getChildren().add(core);
//    }
    
    
    //  TODO:  Maybe move this to a utils class?
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
