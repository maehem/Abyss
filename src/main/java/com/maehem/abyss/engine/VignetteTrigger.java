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

import static com.maehem.abyss.Engine.LOGGER;
import com.maehem.abyss.engine.PoseSheet.Direction;
import java.util.logging.Level;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Doors and other transitions to other Vignettes.
 *
 * @author Mark J Koch [@maehem on GitHub]
 */
public class VignetteTrigger extends TriggerShape {

    public static final boolean SHOW_TRIGGER = true;

    private String destination;
    private double playerX = -1;
    private double playerY = -1;
    private Direction playerDir;
    private boolean locked = false;

    public VignetteTrigger(String vingette) {
        // Dummy Port for providing a default vignette. (like at game start)
        this(0, 0, 1, 1, false, -1, -1, Direction.RIGHT, vingette);
    }

    public VignetteTrigger(double x, double y, double w, double h, double px, double py, Direction pdir, String vignette) {
        this(x,y,w,h, false,px,py,pdir,vignette);
    }

    public VignetteTrigger(double x, double y, double w, double h, boolean showTrigger, double px, double py, Direction pdir, String vignette) {
        super(x, y, w, h);

        setDestination(vignette);

        if ( showTrigger ) {
            setIconNode( 0, 0);
        }

        this.playerX = px;
        this.playerY = py;
        this.playerDir = pdir;
    }

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

    /**
     * @return the playerX
     */
    public double getPlayerX() {
        //return scaleX*playerX;
        return playerX;
    }

    /**
     * @return the playerY
     */
    public double getPlayerY() {
        //return scaleY*playerY;
        return playerY;
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

    /**
     * @return the locked
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * @param locked the locked to set
     */
    public void setLocked(boolean locked) {
        LOGGER.log(Level.CONFIG, "Set " + playerDir.name() + " Door lock state to: " + (locked ? "Locked" : "Open"));
        this.locked = locked;
    }

    public final void setIconNode( double offX, double offY ) {
        Rectangle r = new Rectangle(getPrefWidth(), getPrefHeight(), new Color(1.0,1.0,1.0,0.2));
        getIcon().getChildren().clear();
        getIcon().getChildren().add(r);
        getIcon().setLayoutX(offX);
        getIcon().setLayoutY(offY);
        requestLayout();
    }

}
