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
package com.maehem.flatlinejack.engine;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Doors and other transitions to other Vignettes.
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class TriggerShape extends Rectangle {

    public static final Color TRIGGER_FILL_DEFAULT = Color.GOLD;
    public static final Color TRIGGER_FILL_ACTIVE = Color.RED;

    private double rawX = 0.0;
    private double rawY = 0.0;
    private double rawW = 0.1;
    private double rawH = 0.1;
    
//    private String destination;
//    private double playerX = -1;
//    private double playerY = -1;
//    private Direction playerDir;

    public TriggerShape(double x, double y, double w, double h) {
        super(x, y, w, h);
        this.rawX = x;
        this.rawY = y;
        this.rawW = w;
        this.rawH = h;
        
        setFill(TRIGGER_FILL_DEFAULT);

    }

//    /**
//     * @return the @destination
//     */
//    public String getDestination() {
//        return destination;
//    }
//
//    /**
//     * @param desination the @destination to set
//     */
//    public final void setDestination(String desination) {
//        this.destination = desination;
//    }
//
    public void updateTriggerState(boolean tActive) {

        if (tActive) {
            setFill(TriggerShape.TRIGGER_FILL_ACTIVE);
        } else {
            setFill(TriggerShape.TRIGGER_FILL_DEFAULT);
        }
    }

    /**
     * 
     * @param scaleX usually the width of the @Scene
     * @param scaleY usually the height of the @Scene
     */
    public void setScale( double scaleX, double scaleY ) {

        this.setLayoutX(scaleX * rawX);
        this.setLayoutY(scaleY * rawY);
        
        this.setWidth( scaleX * rawW);
        this.setHeight(scaleY * rawH);        
    }
        
//    /**
//     * @return the playerX
//     */
//    public double getPlayerX() {
//        //return scaleX*playerX;
//        return playerX;
//    }
//
//    /**
//     * @return the playerY
//     */
//    public double getPlayerY() {
//        //return scaleY*playerY;
//        return playerY;
//    }
//
//    /**
//     * @return the playerDir
//     */
//    public Direction getPlayerDir() {
//        return playerDir;
//    }
//
//    /**
//     * @param playerDir the playerDir to set
//     */
//    public void setPlayerDir(Direction playerDir) {
//        this.playerDir = playerDir;
//    }

}
