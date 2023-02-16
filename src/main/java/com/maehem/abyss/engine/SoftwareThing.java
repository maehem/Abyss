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

import java.util.ArrayList;
import java.util.Properties;

/**
 *
 * @author Mark J Koch [@maehem on GitHub]
 */
public abstract class SoftwareThing extends Thing {
    // Already tracked via super class.
    //private static final String PROPERTY_CONDITION = "condition";
    
    private static final int CONDITION_MAX = 1000; // Behaves like shield for player.
    private static final int REPAIR_SKILL_MIN = 1;
    
    private static final int ATTACK_DAMAGE = 100; // Reduces random site shield by this amount per attack.
    private static final int SLOW_EFFECT = 0;     // Slows attack rate of enemy.
    private static final double RECOVERY_TIME = 0.84; // Cooldown between next use of this attack.
    
    //private Integer condition = CONDITION_DEFAULT;
    public static double MAX_RECHARGE = 1.0;
    private double rechargeStatus = 0.0;
    
    private final ArrayList<SoftwareListener> listeners = new ArrayList<>();
    private SoftwareUser user;
    
    public SoftwareThing() {}
    
    public SoftwareThing(String name) {
        super(name);
        
        setCondition(CONDITION_MAX);
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
    public String getPackage() {
        return "software";
    }
    
    public int getAttackDamage() {
        return ATTACK_DAMAGE;
    }
    
    public int getSlowEffect() {
        return ATTACK_DAMAGE;
    }
    
    public double getRecoveryTime() {
        return RECOVERY_TIME;
    }
    
    public void setRechargeSatatus( double value ) {
        if ( value > 1.0 ) {
            rechargeStatus = 1.0;
        } else if ( value < 0.0 ) {
            rechargeStatus = 0.0;
        } else {
            rechargeStatus = value;
        }
    }
    
    public double getRechargeStatus() {
        return rechargeStatus;
    }
    
    public void addListener( SoftwareListener l ) {
        listeners.add(l);
    }
    
    public void removeListener( SoftwareListener l ) {
        listeners.remove(l);
    }

    @Override
    public void adjustCondition(int amount) {
        super.adjustCondition(amount);
        for ( SoftwareListener l: listeners ) {
            l.softwareConditionChanged( this, amount );
        }
    }
    
    /**
     * Set when installing software in a DeckThing or a MatrixSite.
     * Null when not installed or uninstalled ( Inventory Item ).
     * 
     * @param user 
     */
    public void setUser( SoftwareUser user ) {
        this.user = user;
    }
    
    
}
