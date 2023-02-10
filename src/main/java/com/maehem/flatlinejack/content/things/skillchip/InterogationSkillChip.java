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
package com.maehem.flatlinejack.content.things.skillchip;

import com.maehem.flatlinejack.engine.SkillChipThing;
import javafx.scene.paint.Color;

/**
 * Improves the ability to ask questions and get better answers sooner.
 * 
 * @author mark
 */
public class InterogationSkillChip extends SkillChipThing {
    public static final String NAME = "Interrogation";
    public static final Color  COLOR = Color.CHARTREUSE;
    public static final String ICON_PATH = "/content/things/deck1-thing.png";
    public static final String DESCRIPTION = 
            "Get to the best answers when asking questions much faster " +
            "than the old way.  Your new charm will have them confessing " +
            "to things they never did.";

    public InterogationSkillChip() {
        super(NAME);
        setColor(COLOR);
        addBuff( Buff.INTERROGATE, 200 );
    }

    @Override
    public String getIconPath() {
        return ICON_PATH;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
    
}
