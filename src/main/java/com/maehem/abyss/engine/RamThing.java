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

import java.util.Properties;

/**
 *
 * Memory Module for use in Decks
 *
 * @author mark
 */
public abstract class RamThing extends Thing {
    private static final String PROPERTY_CONDITION = "condition";
    private static final String PROPERTY_CAPACITY = "capacity";

    private static final int CONDITION_DEFAULT = 1000;
    private static final int CAPACITY_DEFAULT = 128;

    private Integer condition = CONDITION_DEFAULT;
    private Integer capacity = CAPACITY_DEFAULT;


    public RamThing() {}

    public RamThing( String name) { //, int capacity ) {
        super( name );
    }

    @Override
    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public int getCapacity() {
        return capacity;
    }

    protected void setCapacity( int val ) {
        this.capacity = val;
    }

    @Override
    public void loadProperties(Properties p) {
        setCondition(Integer.parseInt(p.getProperty(PROPERTY_CONDITION, String.valueOf(CONDITION_DEFAULT))));
        setCapacity(Integer.parseInt(p.getProperty(PROPERTY_CAPACITY, String.valueOf(CAPACITY_DEFAULT))));
    }

    @Override
    public Properties saveProperties() {
        Properties p = new Properties();
        //p.setProperty("class", getPackage() + "." + getClass().getSimpleName());
        p.setProperty(PROPERTY_CONDITION, condition.toString());
        
        return p;
    }

    @Override
    public String getIconPath() {
        return null;
    }

    @Override
    public String getPackage() {
        return "ram";
    }

}
