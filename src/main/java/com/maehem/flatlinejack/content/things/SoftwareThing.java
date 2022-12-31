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
import java.util.Properties;

/**
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public abstract class SoftwareThing extends Thing {
    //private static final String PROPERTY_CONDITION = "condition";
    
    private static final int CONDITION_MAX = 1000;
    private static final int REPAIR_SKILL_MIN = 1;
    
    //private Integer condition = CONDITION_DEFAULT;

    public SoftwareThing() {}
    
    public SoftwareThing(String name) {
        super(name);
    }

    @Override
    public Properties saveProperties() {
        Properties p = new Properties();
        //p.setProperty("class", getClass().getSimpleName());
        //p.setProperty(PROPERTY_CONDITION, condition.toString());
        
        return p;
    }

    @Override
    public int getMaxCondition() {
        return CONDITION_MAX;
    }

    
    @Override
    public void loadProperties(Properties p) {
        //setCondition(Integer.valueOf(p.getProperty(PROPERTY_CONDITION, String.valueOf(CONDITION_DEFAULT))));
    }
    
//    @Override
//    public void setCondition(Integer condition) {
//        this.condition = condition;
//        //conditionGauge.setValue(condition);
//    }

    @Override
    public String getIconPath() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getPackage() {
        return "software";
    }
    
}
