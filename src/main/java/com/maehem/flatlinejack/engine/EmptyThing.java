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

import com.maehem.flatlinejack.engine.Thing;
import java.util.Properties;

/**
 *
 * @author Mark J Koch <mark at maehem dot com>
 */
public class EmptyThing extends Thing {

    public EmptyThing() {
        super("Empty");
    }

    
    @Override
    public Properties saveProperties() { 
        return new Properties();
    }

    @Override
    public void loadProperties(Properties p) {}

//    @Override
//    public Pane getDetailPane() {
//        return new FlowPane();
//    }

    @Override
    public String getIconPath() {
        return null;
    }

    @Override
    public void saveState(String key, Properties p) {
        return;
    }

    @Override
    public String getPackage() {
        return "";
    }
    
}
