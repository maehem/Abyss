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

import com.maehem.flatlinejack.engine.gui.widgets.Gauge;
import java.util.ArrayList;
import java.util.Properties;

/**
 *
 * @author mark
 */
public abstract class SkillChipThing extends Thing {

    private static final String PROPERTY_CONDITION = "condition";
    private static final int CONDITION_DEFAULT = 1000;

    private Integer condition = CONDITION_DEFAULT;
    private final ArrayList<SoftwareThing> slots = new ArrayList<>();
    private final Gauge conditionGauge = new Gauge(
            "Condition:", 100, 20, 600, CONDITION_DEFAULT,
            Gauge.ValueLabel.NONE
    );
    //private FlowPane detailPane;

    // <protected>??? Used by settings loaders
    public SkillChipThing() {}

    public SkillChipThing( String name ) {
        super(name);
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

//    @Override
//    public Pane getDetailPane() {        
//        if (detailPane == null ) {
//            HBox gaugePane = new HBox(conditionGauge);
//            detailPane = new FlowPane(gaugePane);
//            detailPane.setAlignment(Pos.TOP_CENTER);
//        }
//        
//        return detailPane;
//    }
    
    public void setCondition(Integer condition) {
        this.condition = condition;
        conditionGauge.setValue(condition);
    }

    @Override
    public String getIconPath() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getPackage() {
        return "skillchip";
    }
    
}
