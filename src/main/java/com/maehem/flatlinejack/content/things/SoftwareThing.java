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

import com.maehem.flatlinejack.engine.Thing;
import com.maehem.flatlinejack.engine.gui.widgets.Gauge;
import java.util.Properties;
import javafx.scene.layout.Pane;

/**
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class SoftwareThing extends Thing {
    private static final String PROPERTY_CONDITION = "condition";
    private static final int CONDITION_DEFAULT = 1000;
    private static final int REPAIR_SKILL_MIN = 1;
    
    private Integer condition = CONDITION_DEFAULT;
    private final Gauge conditionGauge = new Gauge("Condition:", 100, 20, condition, CONDITION_DEFAULT);

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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void setCondition(Integer condition) {
        this.condition = condition;
        conditionGauge.setValue(condition);
    }
}
