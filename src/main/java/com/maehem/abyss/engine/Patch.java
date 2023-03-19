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
package com.maehem.abyss.engine;

import java.io.InputStream;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * An image that is composited over the scene that the player may walk behind
 * or in front of and is conditionally set transparent depending on the 
 * distance the player is from the camera.
 * 
 * For example, a right or left door Portal needs to create the illusion that
 * the player is walking though the doorway, but the background scene is a
 * 2D rendered image.  So we create a "@Patch" to draw on top of the character
 * when the character is walking through the doorway, creating the illusion
 * that they walked into the door area.  Conversely, the patch is transparent 
 * when the character is beyond the doorway and closer to the camera creating
 * the illusion that they are in front of that target area.
 *
 * @author Mark J Koch [@maehem on GitHub]
 */
public class Patch extends ImageView {

    private double threshold;
    private final Rectangle box;
        
    public Patch(double x, double y, double thresholdY, InputStream is) {
        super(new Image(is));
        setLayoutX(x);
        setLayoutY(y);
        this.threshold = thresholdY;
        
        // TODO:  Add a threshold opacity.
        
        // TODO:  Maybe translate to thresholdY and use layoutY as the
        // comparitor for showing it.
        
        this.box = new Rectangle(x, y, getImage().getWidth(), thresholdY);
        this.box.setStroke(Color.RED);
        this.box.setStrokeWidth(2.0);
        this.box.setFill(Color.TRANSPARENT);
    }
    
    /**
     * @return the threshold
     */
    public double getThreshold() {
        return threshold;
    }

    /**
     * @param threshold the threshold to set
     */
    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }
    
    /**
     * @return the box
     */
    public Rectangle getBox() {
        return box;
    }

}
