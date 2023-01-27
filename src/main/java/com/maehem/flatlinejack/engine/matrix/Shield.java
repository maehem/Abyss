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
package com.maehem.flatlinejack.engine.matrix;

import com.maehem.flatlinejack.engine.SoftwareThing;
import java.util.ArrayList;
import java.util.List;

/**
 * Data controller and model for one of a site's shield.  A site can have multiple shields.
 * 
 * A shield is attacked until it's condition reaches 0.
 * Once all of the shields of a site are defeated, the site stops
 * defending and the player has access to the terminal of the site.
 * 
 * @author mark
 */
public class Shield {

    private int condition;      // Health of shield.  Non-functional when zero.
    private int counterDamage;  // Health Damage cast back at attacking SoftwareThing.
    
    // Double damage suffered against these Softwares
    private final ArrayList<Class<? extends SoftwareThing>> weakTo = new ArrayList<>();
    // Half damage suffered against these Softwares
    private final ArrayList<Class<? extends SoftwareThing>> strongAgainst = new ArrayList<>();
    
        
    
    public Shield( int condition, int counterDamage ) {
        this.condition = condition;
        this.counterDamage = counterDamage;
    }
    
//    /**
//     * Class to load for 3D Node
//     * 
//     * @return 
//     */
//    public Class<? extends ShieldNode> getNodeClass() {
//        return nodeClass;
//    }
    
    public void changeCondition( int amount ) {
        // Range checking
        if ( amount + condition < 0 ) {
            condition = 0;
        } else 
        if ( amount + condition > Integer.MAX_VALUE ) {
            condition = Integer.MAX_VALUE;
        } else {
            condition += amount;
        }
    }
    
    public boolean isUp() {
        return condition > 0;
    }
    
    public int getCondition() {
        return condition;
    }
    
    public int getCounterDamage() {
        return counterDamage;
    }
    
    public void setCounterDamage( int dmg ) {
        counterDamage = dmg;
    }
    
    public void attackedBy( SoftwareThing s ) {
        int dmg = s.getAttackDamage();
        int counterDamge = getCounterDamage();
        if ( weakTo.contains(s.getClass())) {
            dmg *= 2;
            counterDamge *= 0.7;
        } else if ( strongAgainst.contains(s.getClass())) {
            dmg /= 2;
            counterDamge *= 1.4;
        }
        changeCondition(-dmg);
        s.adjustCondition(-counterDamge);
    }
    
    public List<Class<? extends SoftwareThing>> getWeakTo() {
        return weakTo;
    }
    
    public List<Class<? extends SoftwareThing>> getStrongAgainst() {
        return strongAgainst;
    }
}
