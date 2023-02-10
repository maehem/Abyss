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
public class EdgeRunnerSkillChip extends SkillChipThing {
    public static final String NAME = "EdgeRunner";
    public static final Color  COLOR = Color.FUCHSIA;
    public static final String ICON_PATH = "/content/things/deck1-thing.png";
    public static final String DESCRIPTION = 
            "Gain the edge against AIs and site defenses. Recover faster " +
            "after a battle. Software degrades slower. More text for you " +
            "to read.";

    public EdgeRunnerSkillChip() {
        super(NAME);
        setColor(COLOR);
        addBuff( Buff.EVASION, 173 );
        addBuff( Buff.HACKING, 342 );
        addBuff( Buff.SOFTWARE, 122 );
        addBuff( Buff.CRYPTO, 853 );
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
