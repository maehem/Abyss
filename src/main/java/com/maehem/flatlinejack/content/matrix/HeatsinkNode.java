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
package com.maehem.flatlinejack.content.matrix;

import static com.maehem.flatlinejack.Engine.LOGGER;
import com.maehem.flatlinejack.engine.MatrixSite;
import com.maehem.flatlinejack.engine.matrix.BallShield;
import com.maehem.flatlinejack.engine.matrix.MatrixNode;
import com.maehem.flatlinejack.engine.matrix.ObjTriangleMesh;
import com.maehem.flatlinejack.engine.matrix.WallShield;
import java.util.logging.Level;
import javafx.animation.RotateTransition;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 *
 * @author mark
 */
public class HeatsinkNode extends MatrixNode {
    private final MeshView base = new MeshView(new ObjTriangleMesh(
            HeatsinkNode.class.getResourceAsStream("/content/matrix/core/heatsink-1-base.obj")
    ));
    private final MeshView neck = new MeshView(new ObjTriangleMesh(
            HeatsinkNode.class.getResourceAsStream("/content/matrix/core/heatsink-1-neck.obj")
    ));
    private final MeshView top = new MeshView(new ObjTriangleMesh(
            HeatsinkNode.class.getResourceAsStream("/content/matrix/core/heatsink-1-top.obj")
    ));
    
    public HeatsinkNode(MatrixSite site, double size) {
        super(site, size);     
        
        init();
    }
    
    @Override
    public void initCore() {
        base.setMaterial(new PhongMaterial(Color.DARKGREY));
        neck.setMaterial(new PhongMaterial(Color.DARKRED));
        top.setMaterial(new PhongMaterial(Color.DARKTURQUOISE));
        
        getChildren().addAll(base, neck,top);
        LOGGER.log(Level.INFO, "Core Z: {0}", getBoundsInLocal().getHeight());
        // Y:0 seems to be the top after OBJs added.
        // So we shift the Node up so that zero is our bottom or base.

    }
    
    /**
     * Initialize any shields.
     * 
     */
    @Override
    public void initShields() {
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
        RotateTransition wsT = new RotateTransition(Duration.seconds(4), ws);
        wsT.setFromAngle(45.0);
        wsT.setToAngle(165.0);
        wsT.setCycleCount(RotateTransition.INDEFINITE); // Let the animation run forever
        wsT.setAutoReverse(true); // Reverse direction on alternating cycles
        wsT.play(); // Play the Animation
        
        WallShield ws2 = new WallShield();
        ws2.setRotationAxis(Rotate.Y_AXIS);
        RotateTransition wsT2 = new RotateTransition(Duration.seconds(3), ws2);
        wsT2.setFromAngle(165.0);
        wsT2.setToAngle(45.0);
        wsT2.setCycleCount(RotateTransition.INDEFINITE); // Let the animation run forever
        wsT2.setAutoReverse(true); // Reverse direction on alternating cycles
        wsT2.play(); // Play the Animation

        bs.setTranslateY(20);
        ws.setTranslateY(35);
        ws2.setTranslateY(60);
        shieldGroup.getChildren().addAll(bs, ws, ws2);
        
        getChildren().add(shieldGroup);
        
        
//        siteBlob.setRotationAxis(Rotate.X_AXIS);
//        RotateTransition trans2 = new RotateTransition(Duration.seconds(8), siteBlob);
//        trans2.setFromAngle(0.0);
//        trans2.setToAngle(360.0);
//        trans2.setCycleCount(RotateTransition.INDEFINITE); // Let the animation run forever
//        trans2.setAutoReverse(false); // Reverse direction on alternating cycles
//        trans2.play(); // Play the Animation
        
    }
}
