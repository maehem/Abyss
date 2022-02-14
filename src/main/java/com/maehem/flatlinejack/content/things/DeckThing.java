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
package com.maehem.flatlinejack.content.things;

import com.maehem.flatlinejack.engine.Player;
import com.maehem.flatlinejack.engine.Thing;
import com.maehem.flatlinejack.engine.gui.widgets.Gauge;
import java.util.ArrayList;
import java.util.Properties;
import javafx.geometry.Pos;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/**
 * First Cyberspace deck.
 * 
 * 
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public abstract class DeckThing extends Thing /* implements CyberDeck */{

    private static final String PROPERTY_CONDITION = "condition";
    private static final int CONDITION_DEFAULT = 1000;
    private static final int REPAIR_SKILL_MIN = 1;
    
    private Integer condition = CONDITION_DEFAULT;
    private final ArrayList<SoftwareThing> slots = new ArrayList<>();
    private final Gauge conditionGauge = new Gauge("Condition:", 100, 20, 600, CONDITION_DEFAULT);
    private FlowPane detailPane;

    public DeckThing() {
        // Fill the inventory with EmptyThing placeholders.
    }
    
    @Override
    public Properties saveProperties() {
        Properties p = new Properties();
        p.setProperty(PROPERTY_CONDITION, condition.toString());
        
        return p;
    }
    
    @Override
    public void loadProperties(Properties p) {
        setCondition(Integer.valueOf(p.getProperty(PROPERTY_CONDITION, String.valueOf(CONDITION_DEFAULT))));
    }

    @Override
    public Pane getDetailPane() {        
        if (detailPane == null ) {
            HBox gaugePane = new HBox(conditionGauge);
            detailPane = new FlowPane(gaugePane);
            detailPane.setAlignment(Pos.TOP_CENTER);
        }
        
        return detailPane;
    }

    public  void setCapacity(int cap){
        // Fill the slots with EmptySoftwareThing placeholders.
        for ( int i=0; i< cap; i++ ) {
            slots.add(new EmptySoftwareThing());
        }
    }
    
    /**
     * Add software to first available slot.   Caller should first make
     * sure slots are available.
     * 
     * @param software 
     * @return  
     */
    public boolean addSoftware( SoftwareThing software ) {
        for ( int i=0; i< slots.size(); i++) {
            if ( slots.get(i) instanceof EmptySoftwareThing ) {
                slots.set(i, software);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Return count of empty slots.
     * 
     * @return empty slot count
     */
    public int slotsAvailable() {
        return slots.stream().filter((t) -> ( t instanceof EmptySoftwareThing )).map((_item) -> 1).reduce(0, Integer::sum);
    }
    
    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean canGive() {
        return true;
    }

    @Override
    public boolean canDelete() {
        return true;
    }

    @Override
    public boolean canRepair(Player p) {
        // Check if Player has repair skill
        return true;
    }

    @Override
    public boolean needsRepair() {
        return condition < .95*CONDITION_DEFAULT;
    }

    public void setCondition(Integer condition) {
        this.condition = condition;
        conditionGauge.setValue(condition);
    }

}
