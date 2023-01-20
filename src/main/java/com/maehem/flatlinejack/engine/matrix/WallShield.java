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

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;

/**
 *
 * @author mark
 */
public class WallShield extends Group {

    //private final static double BALL_SIZE = 10.0;
    private final static double SHIELD_RADIUS = 80.0;

    public WallShield() {
        int nShields = 3;
        double rad = 2.0 * Math.PI / nShields;
        double rot = 360/nShields;
        for (int i = 0; i < nShields; i++) {
            double sin = Math.sin(i * rad);
            double cos = Math.cos(i * rad);
            MeshView sh = new MeshView(new ObjTriangleMesh(
                    getClass().getResourceAsStream("/content/matrix/shield1.obj")
            ));
            sh.setMaterial(new PhongMaterial(new Color(1.0, 0.2, 0.5, 0.4)));
            sh.setRotationAxis(Rotate.Y_AXIS);
            sh.setRotate(rot*i);
            sh.setTranslateX(sin * SHIELD_RADIUS);
            sh.setTranslateZ(cos * SHIELD_RADIUS);

            getChildren().add(sh);
            
            setTranslateZ(-50);
        }
    }

}
