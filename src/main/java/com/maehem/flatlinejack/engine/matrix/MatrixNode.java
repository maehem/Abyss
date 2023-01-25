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
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

/**
 *
 * @author mark
 */
public abstract class MatrixNode extends Group {

    private final static double GRID_SCALE = 0.333;
    
    public static final double FONT_H = 32;
    private static final String FONT_FILE = "/fonts/Orbitron-Regular.ttf";
    public static final Font FONT= Font.loadFont(
            MatrixNode.class.getResourceAsStream(FONT_FILE),
            FONT_H
        );

    private final MatrixSite site; // Matrix Site (model)
    private final double size;
    
    public final Group shieldGroup = new Group();
    public final Group structureGroup = new Group();
    
    //private Group shieldGroup = new Group();
    
    public MatrixNode( MatrixSite site, double size ) {
        this.site = site;
        this.size = size;
        
        initFloor();
        
        setRotationAxis(Rotate.Z_AXIS);
        setRotate(180);  // Objects inport upside down so we flip to make that easier.
    }
    
    public void init() {
        //getChildren().add(shieldGroup);
        initStructure();  // The thing/building-like-thing in the center of the site.
        initShields();    // The ICE that protects the site.
        
        // Groups must be added after init or weird visual things happen.
        // JavaFX bug?
        getChildren().add(structureGroup);
        getChildren().add(shieldGroup);
        
        // Your OBJs probably have a different size, placement and scale
        // than this system.  Adjust here.
        setTranslateY(-getBoundsInLocal().getHeight());
    }
    
    private void initFloor() {
        // Add the grid
        getChildren().add(new GroundMesh(size));

        // Circuit floor decor
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
                new Rotate(180, Rotate.Z_AXIS),  //  It's also flipped to make objects import easier.
                new Translate(-size/2, -size/2+6, 0)   // Offset and lift a touch.
        );
        
        getChildren().add(im);
        
        // Coords Displayed in Lower Left of Matrix Floor
        Text coordText = new Text(site.getAddress());
        coordText.setFont(FONT);
        coordText.setFill(Color.LIMEGREEN);
        coordText.getTransforms().addAll(
                new Rotate(90, Rotate.X_AXIS),  // Image was generated on difffernt plane
                new Rotate(180, Rotate.Z_AXIS),  //  It's also flipped
                new Translate(-size/2.4, size/2.3, -1)//,   // Offset to lower-left corner
                //new Scale(3.0, 3.0)
        );
        getChildren().add(coordText);
    }
    
    public abstract void initStructure();
    
    public abstract void initShields();
        
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
