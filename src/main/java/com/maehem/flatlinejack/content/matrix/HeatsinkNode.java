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

import com.maehem.flatlinejack.engine.MatrixSite;
import com.maehem.flatlinejack.engine.matrix.MatrixNode;
import com.maehem.flatlinejack.engine.matrix.ObjTriangleMesh;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;

/**
 *
 * @author mark
 */
public class HeatsinkNode extends MatrixNode {
    private static final MeshView base = new MeshView(new ObjTriangleMesh(
            HeatsinkNode.class.getResourceAsStream("/content/matrix/core/heatsink-1-base.obj")
    ));
    private static final MeshView neck = new MeshView(new ObjTriangleMesh(
            HeatsinkNode.class.getResourceAsStream("/content/matrix/core/heatsink-1-neck.obj")
    ));
    private static final MeshView top = new MeshView(new ObjTriangleMesh(
            HeatsinkNode.class.getResourceAsStream("/content/matrix/core/heatsink-1-top.obj")
    ));
    
    public HeatsinkNode(MatrixSite site) {
        super(site);        
        
    }
    
    @Override
    protected void initCore() {
        base.setMaterial(new PhongMaterial(Color.DARKGREY));
        neck.setMaterial(new PhongMaterial(Color.DARKRED));
        top.setMaterial(new PhongMaterial(Color.DARKTURQUOISE));
        
        getChildren().addAll(base, neck,top);
    }
}
