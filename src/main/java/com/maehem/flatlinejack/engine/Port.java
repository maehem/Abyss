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

import com.maehem.flatlinejack.engine.PoseSheet.Direction;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 * Doors and other transitions to other Vignettes.
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class Port extends Rectangle {

    public static final Color TRIGGER_FILL_DEFAULT = Color.GOLD;
    public static final Color TRIGGER_FILL_ACTIVE = Color.RED;
    
    //private Shape trigger;
    private String destination;
//    private boolean visible = false;
    private double playerX = -1;
    private double playerY = -1;
    private Direction playerDir;
    
    public Port(String vingette) {
        //setDestination(vingette);
        // Dummy Port for providing a default vignette. (like at game start)
        this(0,0,1,1,-1,-1,Direction.RIGHT,vingette);
    }
    
    public Port(int x, int y, int w, int h, double px, double py, Direction pdir, String vignette) {
        super(x, y, w, h);
        setFill(TRIGGER_FILL_DEFAULT);
        
        //setTrigger(new Rectangle(x, y, w, h));
        setDestination(vignette);
        
        this.playerX = px;
        this.playerY = py;
        this.playerDir = pdir;
        
        
    }

//    /**
//     * @return the trigger
//     */
//    public Shape getTrigger() {
//        return trigger;
//    }
//
//    /**
//     * @param trigger the trigger to set
//     */
//    public final void setTrigger(Shape trigger) {
//        this.trigger = trigger;
//        if (trigger.getFill() == null) {
//            trigger.setFill(TRIGGER_FILL_DEFAULT);
//        }        
//    }

    /**
     * @return the @destination
     */
    public String getDestination() {
        return destination;
    }

    /**
     * @param desination the @destination to set
     */
    public final void setDestination(String desination) {
        this.destination = desination;
    }
    
    public void updateTriggerState(boolean tActive) {
        
//        if (visible) {
            if (tActive) {
                setFill(Port.TRIGGER_FILL_ACTIVE);
            } else {
                setFill(Port.TRIGGER_FILL_DEFAULT);
            }
//        } else {
//            setFill(Color.TRANSPARENT);
//        }
        
    }
    
//    public void setVisible(boolean visible) {
//        this.visible = visible;
//        updateTriggerState(visible);
//    }
//    
//    public boolean isVisible() {
//        return visible;
//    }
    
    /**
     * @return the playerX
     */
    public double getPlayerX() {
        return playerX;
    }

    /**
     * @param playerX the playerX to set
     */
    public void setPlayerX(int playerX) {
        this.playerX = playerX;
    }

    /**
     * @return the playerY
     */
    public double getPlayerY() {
        return playerY;
    }

    /**
     * @param playerY the playerY to set
     */
    public void setPlayerY(int playerY) {
        this.playerY = playerY;
    }

    /**
     * @return the playerDir
     */
    public Direction getPlayerDir() {
        return playerDir;
    }

    /**
     * @param playerDir the playerDir to set
     */
    public void setPlayerDir(Direction playerDir) {
        this.playerDir = playerDir;
    }
    
}
