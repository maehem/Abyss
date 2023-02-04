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
 * Doors and other transitions to other Vignettes.
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class MatrixTrigger extends TriggerShape {

    private static final Color TRIGGER_FILL = Color.FORESTGREEN;
    private static final String JACK_ICON_IMAGE_FILENAME = "/icons/jack-icon.png";
//    public static final Color TRIGGER_FILL_DEFAULT = Color.GOLD;
//    public static final Color TRIGGER_FILL_ACTIVE = Color.RED;
    
    //private ImageView jackIcon = new ImageView();
    private int destination;
    private boolean jacking = false;

//    public MatrixTrigger(String vingette) {
//        // Dummy Port for providing a default vignette. (like at game start)
//        this(0, 0, 1, 1, vingette);
//    }

    public MatrixTrigger(double x, double y, double w, double h, int matrixAddress) {
        super(x, y, w, h);
        
        setTriggerColorDefault(TRIGGER_FILL);
        
        setDestination(matrixAddress);
        //initJackIcon();
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

//    private void initJackIcon() {        
//        jackIcon = new ImageView();
//        jackIcon.setImage(new Image(getClass().getResourceAsStream(JACK_ICON_IMAGE_FILENAME)));
//        jackIcon.setPreserveRatio(true);
//        jackIcon.setFitWidth(50);
//        jackIcon.setX(0);
//        jackIcon.setY(-200);
//        
//        getChildren().add(jackIcon);        
//        
//        jackIcon.setOnMouseClicked((event) -> {
//            LOGGER.log(Level.INFO, "Opacity = {0}", jackIcon.getOpacity());
//            event.consume();
//            if ( jackIcon.getOpacity() > 0.0 ) {
//                setJacking(true);
//            }
//        });
//        
//        showJackInIcon(false);
//    }
    
//    public void showJackInIcon(boolean show) {
//        jackIcon.setOpacity(show ? 1.0 : 0.0);
//        if ( !show ) {
//            setJacking(false);
//        }
//    }
    
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
