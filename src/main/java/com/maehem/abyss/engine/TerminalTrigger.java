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

import com.maehem.abyss.engine.bbs.BBSTerminal;
import javafx.scene.paint.Color;


/**
 * Trigger to terminals.
 *
 * @author Mark J Koch [@maehem on GitHub]
 */
public class TerminalTrigger extends TriggerShape {

    private static final Color TRIGGER_FILL = Color.HOTPINK;
    private static final String ICON_IMAGE_FILENAME = "/icons/command-line.png";

    private Class<? extends BBSTerminal>destination = null; // Would load the Public Page
    private boolean usingTerminal = false;

    public TerminalTrigger(double x, double y, double w, double h) {
        super(x, y, w, h);
        
        setTriggerColorDefault(TRIGGER_FILL);
        
        setClickIcon(ICON_IMAGE_FILENAME, 0.0, -200.0);
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

    public boolean isUsingTerminal() {
        return usingTerminal;
    }
    
    public void setUsingTerminal( boolean state ) {
        this.usingTerminal = state;
    }

    @Override
    public void onClick() {
        setUsingTerminal(true);
    }
    
    @Override
    public void onIconShowing(boolean show) {
        if ( !show ) {
            setUsingTerminal(false);
        }
    }
}
