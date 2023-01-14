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

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author mark
 */
public class GroundMesh extends Pane {

    Rectangle plane;
    final int DIVISIONS = 16;
    final Color PLANE_COLOR = new Color(0.5, 0.1, 0.5, 0.3);
    final Color LINE_COLOR = new Color(0.2, 0.2, 0.2, 0.5);
    final Color BORDER_COLOR = new Color(0.1, 0.1, 0.1, 1.0);

    public GroundMesh(double size) {

        plane = new Rectangle(size, size);
        plane.setFill(PLANE_COLOR);
        getChildren().add(plane);

        double lineTranslate = 0;
        double lineWidth = 6.0;

        for (int y = 0; y <= size; y += size / DIVISIONS) {
            Line line = new Line(0, 0, size, 0);
            if ( y == 0 || y == size ) {
                line.setStroke(BORDER_COLOR);
            } else {
                line.setStroke(LINE_COLOR);
            }
            line.setTranslateY(y);
            line.setTranslateZ(lineTranslate);
            line.setStrokeWidth(lineWidth);

            getChildren().add(line);
        }

        for (int x = 0; x <= size; x += size / DIVISIONS) {
            Line line = new Line(0, 0, 0, size);
            if ( x == 0 || x == size ) {
                line.setStroke(BORDER_COLOR);
            } else {
                line.setStroke(LINE_COLOR);
            }
            line.setTranslateX(x);
            line.setTranslateZ(lineTranslate);
            line.setStrokeWidth(lineWidth / 3.0); // Looks better

            getChildren().add(line);
        }

    }

}
