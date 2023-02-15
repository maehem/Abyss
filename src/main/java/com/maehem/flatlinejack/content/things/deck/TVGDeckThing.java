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
package com.maehem.flatlinejack.content.things.deck;

import com.maehem.flatlinejack.engine.DeckThing;
import javafx.scene.paint.Color;

/**
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class TVGDeckThing extends DeckThing {
    public static final String DEFAULT_NAME = "Yamauchi TVG-6";
    public static final Color COLOR = Color.TEAL;
    public static final int BASE_RAM = 64;
    public static final int BASE_SHIELD = 130;
    public static final int SOFTWARE_CAPACITY = 8;
    public static final int RAM_SLOTS = 2;
    public static final int REPAIR_SKILL_MIN = 6;
    
    public static final String ICON_PATH = "/content/things/deck/TVG-6-thing.png";
    
    public TVGDeckThing() {
        super(  DEFAULT_NAME,
                BASE_RAM, BASE_SHIELD, 
                SOFTWARE_CAPACITY, RAM_SLOTS
        );
        setRepairSkill(REPAIR_SKILL_MIN);
        setColor(COLOR);
    }

    @Override
    public String getIconPath() {
        return ICON_PATH;
    }

    @Override
    public String getDescription() {
        return "This limited edition hardware has a few surpises in it. " +
                "More RAM than the average matrix deck and an always on light " +
                "that says 'Turbo'";
    }
    
    
}
