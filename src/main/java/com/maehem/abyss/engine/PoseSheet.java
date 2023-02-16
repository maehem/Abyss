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
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author Mark J Koch [@maehem on GitHub]
 */
public class PoseSheet extends ImageView {

    public static enum Direction { TOWARD, LEFT, RIGHT, AWAY }
    
    private Direction direction = Direction.TOWARD;  // Relative to camera.
    private int numDirections = 0;
    private int numPoses = 0;
    private int pose = 0;
    private double clipW;
    private double clipH;

    public PoseSheet( int height ) {        
        setDefaultSheet();
        
        setFitHeight(height);
        setPreserveRatio(true);
        setSmooth(false);
    }   

    public final void setDefaultSheet() {
        setSkin( PoseSheet.class.getResourceAsStream("/walk/pose-sheet.png"), 04, 12);                
    }
    
    public double getHeight() {
        return getBoundsInParent().getHeight();
    }
    
    public double getWidth() {
        return getBoundsInParent().getWidth();
    }
    
    /**
     *   All the walk poses are on one grid sheet.
     *   Each row represents character facing direction.
     *   Each of 12 columns is the animation walk cycle.
     * 
     * @param is input stream
     * @param rows
     * @param cols
     */
    public final void setSkin( InputStream is, int rows, int cols ) {
        this.setImage(new Image(is));
        this.numDirections = rows;
        this.numPoses = cols;
        
        //this.setScaleX(scale);
        //this.setScaleY(scale);
        
        // do we need to remove and re-add this?
//        character.getChildren().remove(this);
        
        this.clipW = getImage().getWidth() / cols;
        this.clipH = getImage().getHeight() / rows;
        
        setPose(pose);
        
//        character.getChildren().add(this);                
    }
    
//    /**
//     * Translate the character image so that the center (layoutX/Y)
//     * is at this place in the image. Usually near the feet of the character.
//     * setOrigin(0.5, 0.8) is a good starting point.
//     * 
//     * @param x range 0.0-1.0 (left to right)
//     * @param y range 0.0-1.0 (top to bottom(feet))
//     */
//    public final void setOrigin(double x, double y) {
//        character.setTranslateX( -getClipW() * x );
//        character.setTranslateY( -getClipH() * y );
//    }
//    
    public final void setPose(int n) {
        if (n >= 0 && n < getNumPoses()) {
            pose = n;
        }
        // TODO: Should throw error if pose number is illegal to avoid hard to find bugs.
        // or just use a division remainder %

        // Clip what we see down to a single character pose.
        setViewport(new Rectangle2D(
                pose * getClipW(), getDirection().ordinal() * getClipH(), 
                getClipW(), getClipH()
        ));
    }

    public void nextPose() {
        pose++;
        if (pose >= getNumPoses()) {
            pose = 0;
        }

        setPose(pose);
    }

    /**
     * @return the numPoses
     */
    public int getNumPoses() {
        return numPoses;
    }

    /**
     * @return the numDirections
     */
    public int getNumDirections() {
        return numDirections;
    }

    public Direction getDirection() {
        return direction;
    }
    
    /**
     * @param direction the direction to set
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
        setPose(pose);  // Recalculate displayed graphic
    }
    
    public ImageView getCameo() {
        ImageView cameo = new ImageView(getImage());
        cameo.setViewport(new Rectangle2D(0, 0, getClipW(), getClipH()/3));
                
        return cameo;
    }
    
    /**
     * @return the clipW
     */
    public double getClipW() {
        return clipW;
    }

    /**
     * @return the clipH
     */
    public double getClipH() {
        return clipH;
    }

}
