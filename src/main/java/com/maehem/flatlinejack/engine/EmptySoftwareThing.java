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

import java.util.Properties;

/**
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class EmptySoftwareThing extends SoftwareThing {

    public EmptySoftwareThing() {
        super( "Empty Slot" );
    }
    
    /**
     * Cause empty slot to not write the default properties.
     * 
     * @return 
     */
    @Override
    public Properties saveProperties() {
        return new Properties();
    }

    @Override
    public int getAttackDamage() {
        return 0;
    }

    @Override
    public double getRecoveryTime() {
        return 0;
    }

    @Override
    public String getIconPath() {
        return "/icons/empty-software-thing.png";
    }

    @Override
    public String getDescription() {
        return "Empty Software Thing";
    }
    
    
    
}
