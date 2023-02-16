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

import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javafx.scene.paint.Color;

/**
 *
 * @author mark
 */
public abstract class SkillChipThing extends Thing {
    public static final String ICON_PATH = "/ui/skillchip-thing.png";

    public enum Buff {      // Value range 1-999  - represents 1-99.9 percent in most cases.

        NEGOTIATE("NEGO", Color.ALICEBLUE),     // Get better price when purchasing
        INTERROGATE("INTR", Color.SPRINGGREEN),  // Get better answers when asking NPCs
        CRYPTO("CRYP", Color.BISQUE ),          // Terminal passwords are easier, partially shown
        MUSIC("MUSI", Color.BLANCHEDALMOND),       // Music instruments will sound more melodic when you encounter them.
        SOFTWARE("SOFT", Color.CHARTREUSE),    // Software description will fill in.
        DEBUG("DEBG", Color.CHOCOLATE),       // Broken things may become repairable.
        REPAIR("REPR", Color.FUCHSIA),      // Condition will increase faster with each attempt
        HACKING("HACK", Color.GOLD),     // Matrix sites will have more crit attacks.
        EVASION("EVAD", Color.KHAKI),     // Disengage from an AI easier.
        RECOVERY("RECO", Color.ROSYBROWN);     // Recover health and constitution faster.
        
        private final String mnemonic;
        private final Color color;
        
        Buff(String mnem, Color color) {
            this.mnemonic = mnem;
            this.color = color;
        }
        
        public String mnemonic() {
            return mnemonic;
        }
        
        public Color color() {
            return color;
        }
    }
    
    private final EnumMap<Buff,Integer> buffs = new EnumMap<>(Buff.class);


    //private static final String PROPERTY_CONDITION = "condition";

//    private Integer condition = CONDITION_DEFAULT;
    //private final ArrayList<SoftwareThing> slots = new ArrayList<>();
//    private final Gauge conditionGauge = new Gauge(
//            "Condition:", 100, 20, 600, CONDITION_DEFAULT,
//            Gauge.ValueLabel.NONE
//    );
    //private FlowPane detailPane;

    // <protected>??? Used by settings loaders
    public SkillChipThing() {}

    public SkillChipThing( String name ) {
        super(name);
    }
    
    public void addBuff( Buff b, Integer val ) {
        buffs.put(b, val);
    }
    
    public Set<Map.Entry<Buff, Integer>> getBuffs() {
        return buffs.entrySet();
    }
    
    @Override
    public Properties saveProperties() {
        Properties p = new Properties();
        //p.setProperty(PROPERTY_CONDITION, condition.toString());
        
        return p;
    }
    
    @Override
    public void loadProperties(Properties p) {
        //setCondition(Integer.valueOf(p.getProperty(PROPERTY_CONDITION, String.valueOf(CONDITION_MAX))));
    }

    @Override
    public int getMaxCondition() {
        return 1000;
    }

    @Override
    public String getPackage() {
        return "skillchip";
    }
    
    @Override
    public String getIconPath() {
        return ICON_PATH;
    }

}
