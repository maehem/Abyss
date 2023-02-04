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


/**
 * Trigger to Matrix Sites.
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class MatrixTrigger extends TriggerShape {

    private static final Color TRIGGER_FILL = Color.FORESTGREEN;
    private static final String JACK_ICON_IMAGE_FILENAME = "/icons/jack-icon.png";
    
    private int destination;
    private boolean jacking = false;

    public MatrixTrigger(double x, double y, double w, double h, int matrixAddress) {
        super(x, y, w, h);
        
        setTriggerColorDefault(TRIGGER_FILL);
        
        setDestination(matrixAddress);

        setClickIcon( JACK_ICON_IMAGE_FILENAME, 0.0, -200.0 );
    }

    /**
     * @return the @destination
     */
    public int getDestination() {
        return destination;
    }

    /**
     * @param matrixAddress the @destination to set
     */
    public final void setDestination(int matrixAddress) {
        this.destination = matrixAddress;
    }
    
    public boolean isJacking() {
        return jacking;
    }
    
    public void setJacking( boolean state ) {
        this.jacking = state;
    }
    
    @Override
    public void onIconShowing(boolean show) {
        if ( !show ) {
            // Only turn off jacking if it was already on.
            setJacking(false);
        }
    }
    
    /**
     * User clicked the action icon.  Do something.
     * 
     */
    @Override
    public void onClick() {
        setJacking(true);
    }
}
