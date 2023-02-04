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

import static com.maehem.flatlinejack.Engine.LOGGER;
import com.maehem.flatlinejack.engine.gui.bbs.BBSTerminal;
import java.util.logging.Level;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;


/**
 * Doors and other transitions to other Vignettes.
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class TerminalTrigger extends TriggerShape {

    private static final Color TRIGGER_FILL = Color.HOTPINK;
    private static final String ICON_IMAGE_FILENAME = "/icons/command-line.png";
//    public static final Color TRIGGER_FILL_DEFAULT = Color.GOLD;
//    public static final Color TRIGGER_FILL_ACTIVE = Color.RED;
    
    private ImageView icon = new ImageView();
    private Class<? extends BBSTerminal>destination = null; // Would load the Public Page
    private boolean usingTerminal = false;

//    public MatrixTrigger(String vingette) {
//        // Dummy Port for providing a default vignette. (like at game start)
//        this(0, 0, 1, 1, vingette);
//    }

    public TerminalTrigger(double x, double y, double w, double h) {
        super(x, y, w, h);
        
        setTriggerColorDefault(TRIGGER_FILL);
        
        //setDestination(terminal);
        initIcon();
    }
    
    public TerminalTrigger(double x, double y, double w, double h, Class<? extends BBSTerminal> terminal) {
        this(x, y, w, h);
        this.setDestination( terminal );        
    }

    /**
     * @return the @destination
     */
    public Class<? extends BBSTerminal> getDestination() {
        return destination;
    }

    /**
     * @param terminal the @destination to set
     */
    public final void setDestination(Class<? extends BBSTerminal> terminal) {
        this.destination = terminal;
    }

    private void initIcon() {        
        icon = new ImageView();
        icon.setImage(new Image(getClass().getResourceAsStream(ICON_IMAGE_FILENAME)));
        icon.setPreserveRatio(true);
        icon.setFitWidth(50);
        icon.setX(0);
        icon.setY(-200);
        
        getChildren().add(icon);        
        
        icon.setOnMouseClicked((event) -> {
            LOGGER.log(Level.INFO, "Opacity = {0}", icon.getOpacity());
            event.consume();
            if ( icon.getOpacity() > 0.0 ) {
                setUsingTerminal(true);
            }
        });
        
        showIcon(false);
    }
    
    public void showIcon(boolean show) {
        icon.setVisible(show);
        if ( !show ) {
            setUsingTerminal(false);
        }
    }
    
    public boolean isUsingTerminal() {
        return usingTerminal;
    }
    
    public void setUsingTerminal( boolean state ) {
        this.usingTerminal = state;
    }
}
