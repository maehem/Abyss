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

/**
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class KomodoDeckThing extends DeckThing {
    public static final String DEFAULT_NAME = "Komodo Spark M3";
    public static final int SOFTWARE_CAPACITY = 20;
    public static final int RAM_SLOTS = 8;
    public static final String ICON_PATH = "/content/things/deck1-thing.png";
    
    public KomodoDeckThing() {
        super( DEFAULT_NAME, SOFTWARE_CAPACITY, RAM_SLOTS );
    }

    @Override
    public String getIconPath() {
        return ICON_PATH;
    }
    
}
