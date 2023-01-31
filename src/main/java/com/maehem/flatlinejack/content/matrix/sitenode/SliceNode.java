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
package com.maehem.flatlinejack.content.matrix.sitenode;

import com.maehem.flatlinejack.engine.matrix.MatrixSite;
import com.maehem.flatlinejack.engine.matrix.MatrixNode;
import com.maehem.flatlinejack.engine.matrix.ObjTriangleMesh;
import com.maehem.flatlinejack.content.matrix.shieldnode.WallShieldNode;
import com.maehem.flatlinejack.engine.matrix.Shield;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 *
 * @author mark
 */
public class SliceNode extends MatrixNode {

    private final MeshView core = new MeshView(new ObjTriangleMesh(
            SliceNode.class.getResourceAsStream("/content/matrix/core/slice-core.obj")
    ));
    private final MeshView shell = new MeshView(new ObjTriangleMesh(
            SliceNode.class.getResourceAsStream("/content/matrix/core/slice-shell.obj")
    ));
    
    public SliceNode(MatrixSite site, double size) {
        super(site, size);     
        
        // TODO: Read the add-on properties string and do those things.
        // Comma separated list of custom values to apply.
        String nodeProperties = site.getNodeProperties();
        
        // Call init() if you overrode initStructure() or initShields()
        init();
        
        
        site.setTerminalAvailable(false);
    }
    
    /**
     * Initialize the structure item.
     * Add your OBJs to the structureGroup.
     * 
     */
    @Override
    public void initStructure() {
        core.setMaterial(new PhongMaterial(Color.MAGENTA));
        shell.setMaterial(new PhongMaterial(Color.YELLOW));
        
        structureGroup.getChildren().addAll(core, shell);
        
        structureGroup.setRotationAxis(Rotate.Y_AXIS);
        structureGroup.setRotate(180);
        
        RotateTransition rt = new RotateTransition(new Duration(30000), structureGroup);
        rt.setInterpolator(Interpolator.LINEAR);
        rt.setAxis(Rotate.Y_AXIS);
        rt.setFromAngle(0);
        rt.setToAngle(360);
        rt.setCycleCount(RotateTransition.INDEFINITE);
        rt.play();
        
    }
    
    /**
     * Initialize any shields.
     * Add your items to the shieldGroup.
     * 
     * 
     */
    @Override
    public void initShields() {
        
//        Shield ballShield = new Shield(200, 124);
//        getSite().getShields().add(ballShield);
//        
//        BallShieldNode bs = new BallShieldNode(ballShield, 12, 20.0);        
//        // Set up a Rotate Transition the Rectangle
//        bs.setRotationAxis(Rotate.Y_AXIS);
//        RotateTransition trans = new RotateTransition(Duration.seconds(8), bs);
//        trans.setFromAngle(0.0);
//        trans.setToAngle(360.0);
//        trans.setCycleCount(RotateTransition.INDEFINITE); // Let the animation run forever
//        trans.setAutoReverse(false); // Reverse direction on alternating cycles
//        trans.play(); // Play the Animation
        
        Shield wallShield1 = new Shield(240, 20);
        getSite().getShields().add(wallShield1);
        WallShieldNode ws1Node = new WallShieldNode(wallShield1, 35.0);
        ws1Node.setRotationAxis(Rotate.Y_AXIS);
        RotateTransition wsT = new RotateTransition(Duration.seconds(4), ws1Node);
        wsT.setFromAngle(45.0);
        wsT.setToAngle(165.0);
        wsT.setCycleCount(RotateTransition.INDEFINITE); // Let the animation run forever
        wsT.setAutoReverse(true); // Reverse direction on alternating cycles
        wsT.play(); // Play the Animation
        
//        Shield wallShield2 = new Shield(240, 20);
//        getSite().getShields().add(wallShield2);
//        WallShieldNode ws2Node = new WallShieldNode(wallShield2, 60.0);
//        ws2Node.setRotationAxis(Rotate.Y_AXIS);
//        RotateTransition wsT2 = new RotateTransition(Duration.seconds(3), ws2Node);
//        wsT2.setFromAngle(165.0);
//        wsT2.setToAngle(45.0);
//        wsT2.setCycleCount(RotateTransition.INDEFINITE); // Let the animation run forever
//        wsT2.setAutoReverse(true); // Reverse direction on alternating cycles
//        wsT2.play(); // Play the Animation

        shieldGroup.getChildren().addAll(ws1Node );                
    }

    @Override
    public void initAttacks() {
//        // Add objects for attack animations.
//        // Add your attack tools thusly.
//        BackOffice3SoftwareThing backOfficeSoft = new BackOffice3SoftwareThing();
//        // Site uses for control
//        getSite().getAttackTools().add(backOfficeSoft);
//        // Node uses for visual
//        SoftwareNode backOffficeNode = new FlameBallAttackNode(backOfficeSoft);
//        
//        DrillSoftwareThing drillSoft = new DrillSoftwareThing();
//        getSite().getAttackTools().add(drillSoft);
//        SoftwareNode drillNode = new BulletsAttackNode(drillSoft);
//        
//        attacksGroup.getChildren().addAll(backOffficeNode, drillNode);
//       
    }
    
    
}
